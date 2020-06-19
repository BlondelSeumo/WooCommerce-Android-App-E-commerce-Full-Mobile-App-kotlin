package com.iqonic.woobox.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import android.widget.FrameLayout
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.iqonic.woobox.fragments.SignInFragment
import com.iqonic.woobox.fragments.SignUpFragment
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.extensions.*


class SignInUpActivity : AppBaseActivity() {

    private val mSignInFragment: SignInFragment = SignInFragment()
    private val mSignUpFragment: SignUpFragment = SignUpFragment()
    private var callbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_up)
        FacebookSdk.sdkInitialize(applicationContext)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        authenticate(FacebookAuthProvider.getCredential(loginResult.accessToken.token), loginResult.accessToken.token, "facebook")
                    }

                    override fun onCancel() {
                        Log.d("Sign in", "facebook:onCancel")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d("Sign in", "facebook:onError", error)
                    }
                })
        /**
         * Load Default Fragment
         */
        loadSignInFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RequestCode.SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                authenticate(GoogleAuthProvider.getCredential(account?.idToken, null), account?.idToken!!, "google")
            } catch (e: ApiException) {
                Log.w("SIGN IN", "Google sign in failed", e)
            }
        }
    }

    private fun authenticate(credential: AuthCredential, token: String, type: String) {
        showProgress(true)
        mAuth?.signInWithCredential(credential)
                ?.addOnFailureListener {
                    snackBar(getString(R.string.lbl_sign_in_failed))
                }
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = mAuth?.currentUser
                        //Log.e("toiken","gbgh****\n"+mAuth?.getAccessToken(true)?.result?.token)
                        doSocialLogin(user!!, token, type)
                    } else {
                        snackBar(getString(R.string.lbl_sign_in_failed))
                        showProgress(false)

                    }
                }
    }

    private fun doSocialLogin(user: FirebaseUser, token: String, type: String) {
        var firstName = ""
        var lastName = ""
        if (user.displayName != null && user.displayName?.split(" ")?.size!! >= 2) {
            firstName = user.displayName?.split(" ")?.get(0)!!
            lastName = user.displayName?.split(" ")?.get(1)!!
        } else {
            firstName = user.displayName!!
        }
        getSharedPrefInstance().setValue(Constants.SharedPref.USER_PROFILE,user.photoUrl.toString())
        socialLogin(user.email!!, token, firstName.trim(), lastName.trim(), type, user.photoUrl.toString(), onResult = {
            showProgress(false)
            if (it) launchActivityWithNewTask<DashBoardActivity>()
        }, onError = {
            showProgress(false)
            snackBarError(it)
        })
    }

    fun loadSignUpFragment() {
        if (mSignUpFragment.isAdded) {
            replaceFragment(mSignUpFragment, R.id.fragmentContainer)
            findViewById<FrameLayout>(R.id.fragmentContainer).fadeIn(500)
        } else {
            addFragment(mSignUpFragment, R.id.fragmentContainer)
        }
    }

    fun loadSignInFragment() {
        if (mSignInFragment.isAdded) {
            replaceFragment(mSignInFragment, R.id.fragmentContainer)
            findViewById<FrameLayout>(R.id.fragmentContainer).fadeIn(500)
        } else {
            addFragment(mSignInFragment, R.id.fragmentContainer)
        }
    }

    override fun onBackPressed() {
        when {
            mSignUpFragment.isVisible -> removeFragment(mSignUpFragment)
            else -> super.onBackPressed()

        }
    }

    fun doFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "user_photos", "public_profile")
        )
    }

    fun doGoogleLogin() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, Constants.RequestCode.SIGN_IN)
    }
}