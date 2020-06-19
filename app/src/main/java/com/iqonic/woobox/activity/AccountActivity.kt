package com.iqonic.woobox.activity

import android.app.Activity
import android.os.Bundle
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.txtDisplayName
import kotlinx.android.synthetic.main.toolbar.*

class AccountActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setToolbar(toolbar)
        title = getString(R.string.title_account)

        txtDisplayName.text = getUserFullName()
        ivProfileImage.loadImageFromUrl(getUserProfile(),aPlaceHolderImage = R.drawable.ic_profile)
        btnSignOut.onClick {
            val dialog = getAlertDialog(getString(R.string.lbl_logout_confirmation), onPositiveClick = { dialog, i ->
                clearLoginPref()
                launchActivityWithNewTask<DashBoardActivity>()
            }, onNegativeClick = { dialog, i ->
                dialog.dismiss()
            })
            dialog.show()
        }
        tvOrders.onClick { launchActivity<OrderActivity>() }
        tvOffer.onClick { launchActivity<OfferActivity>() }
        ivProfileImage.onClick { launchActivity<EditProfileActivity>() }
        tvHelpCenter.onClick { launchActivity<HelpActivity>() }
        btnVerify.onClick { launchActivity<OTPActivity>() }
        tvAddressManager.onClick {
            if (getAddressList().size == 0) {
                launchActivity<AddAddressActivity>()
            } else {
                launchActivity<AddressManagerActivity>()
            }
        }
        tvWishlist.onClick {
            setResult(Activity.RESULT_OK)
            finish()
        }
        showBannerAds()
    }
}