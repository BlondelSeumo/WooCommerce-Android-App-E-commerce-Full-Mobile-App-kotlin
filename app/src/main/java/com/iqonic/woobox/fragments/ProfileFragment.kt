package com.iqonic.woobox.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.BuildConfig
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.utils.Constants.SharedPref.IS_SOCIAL_LOGIN
import com.iqonic.woobox.utils.Constants.SharedPref.USER_PASSWORD
import com.iqonic.woobox.utils.ImagePicker
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_reset.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.btnChangePassword
import java.io.File
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import com.iqonic.woobox.R
import com.theartofdev.edmodo.cropper.CropImage
import android.app.Activity.RESULT_OK
import com.iqonic.woobox.activity.DashBoardActivity
import com.theartofdev.edmodo.cropper.CropImageView


class ProfileFragment : BaseFragment() {


    private var encodedImage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isLoggedIn()) {
            edtEmail.setText(getEmail())
            edtFirstName.setText(getFirstName())
            edtLastName.setText(getLastName())
            edtFirstName.setSelection(edtFirstName.text.length)
            ivProfileImage.loadImageFromUrl(getUserProfile(),aPlaceHolderImage = R.drawable.ic_profile)
            if (getSharedPrefInstance().getBooleanValue(IS_SOCIAL_LOGIN)) {
                btnChangePassword.hide()
            } else {
                btnChangePassword.show()
            }
        }
        setUpListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                ivProfileImage.setImageURI(resultUri)
                val imageStream = activity!!.contentResolver.openInputStream(resultUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                encodedImage = encodeImage(selectedImage)
                if (encodedImage != null) {
                    updateProfilePhoto()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                if (error?.message != null){
                    snackBar(error.message!!)
                }
            }
        }else{
            if (data != null && data.data != null) ivProfileImage.setImageURI(data.data)
            val path: String? = ImagePicker.getImagePathFromResult(activity!!, requestCode, resultCode, data) ?: return
            val uri = FileProvider.getUriForFile(
                activity!!,
                BuildConfig.APPLICATION_ID + ".provider",
                File(path)
            )
            CropImage.activity(uri)
                .setOutputCompressQuality(40)
                .start(activity!!)


        }


    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun setUpListener() {
        btnSaveProFile.onClick {
            if (validate()) {
                showProgress()
                updateProfile()
            }
        }
        btnChangePassword.onClick { showChangePasswordDialog() }
        btnDeactivate.onClick {

        }
        editProfileImage.onClick {
            activity?.requestPermissions(
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), onResult = {
                    if (it) {
                       /* ImagePicker.pickImage(
                            this@ProfileFragment,
                            context.getString(R.string.lbl_select_image),
                            ImagePicker.mPickImageRequestCode,
                            false
                        )*/
                        CropImage.activity()
                            .setAspectRatio(1,1)
                            .setGuidelines(CropImageView.Guidelines.OFF)
                            .setRequestedSize(300,300)
                            .setOutputCompressQuality(40)
                            .start(context,this@ProfileFragment)
                    } else {
                        activity!!.showPermissionAlert(this)
                    }
                })
        }

    }

    private fun updateProfilePhoto() {
        showProgress()
        val requestModel = RequestModel()
        requestModel.base64_img = encodedImage
        activity!!.saveProfileImage(requestModel, onSuccess = {
            hideProgress()
            encodedImage=null
            (activity as DashBoardActivity).changeProfile()
        })

    }

    private fun showChangePasswordDialog() {
        val changePasswordDialog = Dialog(activity!!)
        changePasswordDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        changePasswordDialog.setContentView(R.layout.dialog_reset)
        changePasswordDialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        changePasswordDialog.edtOldPwd.transformationMethod = biggerDotTranformation
        changePasswordDialog.edtConfirmPwd.transformationMethod = biggerDotTranformation
        changePasswordDialog.edtNewPwd.transformationMethod = biggerDotTranformation
        changePasswordDialog.btnChangePassword.onClick {
            val mPassword = getSharedPrefInstance().getStringValue(USER_PASSWORD)
            when {
                changePasswordDialog.edtOldPwd.checkIsEmpty() -> {
                    changePasswordDialog.edtOldPwd.showError(getString(R.string.error_field_required))
                }
                changePasswordDialog.edtNewPwd.checkIsEmpty() -> {
                    changePasswordDialog.edtNewPwd.showError(getString(R.string.error_field_required))
                }
                changePasswordDialog.edtNewPwd.validPassword() -> {
                    changePasswordDialog.edtNewPwd.showError(getString(R.string.error_pwd_digit_required))
                }
                changePasswordDialog.edtConfirmPwd.checkIsEmpty() -> {
                    changePasswordDialog.edtConfirmPwd.showError(getString(R.string.error_field_required))
                }
                changePasswordDialog.edtConfirmPwd.validPassword() -> {
                    changePasswordDialog.edtConfirmPwd.showError(getString(R.string.error_pwd_digit_required))
                }
                !changePasswordDialog.edtConfirmPwd.text.toString().equals(
                    changePasswordDialog.edtNewPwd.text.toString(),
                    false
                ) -> {
                    changePasswordDialog.edtConfirmPwd.showError(getString(R.string.error_password_not_matches))
                }
                !changePasswordDialog.edtOldPwd.text.toString().equals(mPassword, false) -> {
                    changePasswordDialog.edtOldPwd.showError(getString(R.string.error_old_password_not_matches))
                }
                changePasswordDialog.edtNewPwd.text.toString().equals(mPassword, false) -> {
                    changePasswordDialog.edtNewPwd.showError(getString(R.string.error_new_password_same))
                }
                else -> {
                    val requestModel = RequestModel()
                    requestModel.password = changePasswordDialog.edtNewPwd.text.toString()
                    showProgress()
                    activity!!.changePassword(requestModel, onSuccess = {
                        hideProgress()
                        changePasswordDialog.dismiss()
                    })
                }
            }
        }
        changePasswordDialog.show()
    }

    private fun updateProfile() {
        val requestModel = RequestModel()
        requestModel.email = edtEmail.textToString()
        requestModel.first_name = edtFirstName.textToString()
        requestModel.last_name = edtLastName.textToString()
        (activity as AppBaseActivity).createCustomer(requestModel) {
            snackBar(getString(R.string.lbl_profile_saved))
                hideProgress()
        }
    }

    private fun validate(): Boolean {
        return when {
            edtFirstName.checkIsEmpty() -> {
                edtFirstName.showError(getString(R.string.error_field_required))
                false
            }
            edtLastName.checkIsEmpty() -> {
                edtLastName.showError(getString(R.string.error_field_required))
                false
            }
            edtEmail.checkIsEmpty() -> {
                edtEmail.showError(getString(R.string.error_field_required))
                false
            }
            !edtEmail.isValidEmail() -> {
                edtEmail.showError(getString(R.string.error_enter_valid_email))
                false
            }
            else -> true
        }

    }
}