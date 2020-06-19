package com.iqonic.woobox.activity

import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.extensions.getSharedPrefInstance
import com.iqonic.woobox.utils.extensions.launchActivity
import com.iqonic.woobox.utils.extensions.launchActivityWithNewTask
import com.iqonic.woobox.utils.extensions.runDelayed
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SplashActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        try {
            val info = packageManager.getPackageInfo(packageName, GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))
                }
            }
        } catch (e: NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) { }
        runDelayed(1000) {
            if (getSharedPrefInstance().getBooleanValue(Constants.SharedPref.SHOW_SWIPE)) {
                launchActivity<DashBoardActivity>()
              /*  launchActivity<ProductDetailActivityNew> {
                    putExtra(Constants.KeyIntent.PRODUCT_ID,27)
                }*/
            } else {

                launchActivity<WalkThroughActivity>()
            }
            finish()
        }
    }
}