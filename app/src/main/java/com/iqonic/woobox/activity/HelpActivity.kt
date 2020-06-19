package com.iqonic.woobox.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class HelpActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        title = getString(R.string.title_help)
        setToolbar(toolbar)

        btnSubmit.onClick {
            when {
                validate() -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.text_iqonicdesign_gmail_com), null))
                    emailIntent.putExtra(Intent.EXTRA_TEXT, edtDescription.text.toString())
                    startActivity(Intent.createChooser(emailIntent, "Send email..."))
                    finish()
                }
            }
        }
        BroadcastReceiverExt(this) {
            onAction(CART_COUNT_CHANGE) {
                setCartCount()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        val menuWishItem: MenuItem = menu!!.findItem(R.id.action_cart)
        val menuSearch: MenuItem = menu.findItem(R.id.action_search)
        menuWishItem.isVisible = true
        menuSearch.isVisible = false
        mMenuCart = menuWishItem.actionView
        mMenuCart.ivCart.setColorFilter(this.color(R.color.textColorPrimary))
        setCartCount()
        menuWishItem.actionView.onClick {
            launchActivity<MyCartActivity> { }
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun setCartCount() {
        val count = getCartCount()
        mMenuCart.tvNotificationCount.text = count
        if (count.checkIsEmpty()) {
            mMenuCart.tvNotificationCount.hide()
        } else {
            mMenuCart.tvNotificationCount.show()
        }
    }

    private fun validate(): Boolean {
        return when {
            edtContact.checkIsEmpty() -> {
                edtContact.showError(getString(R.string.error_field_required))
                false
            }
            !edtContact.isValidPhoneNumber() -> {
                edtContact.showError(getString(R.string.error_enter_valid_contact))
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
            edtDescription.checkIsEmpty() -> {
                edtDescription.showError(getString(R.string.error_field_required))
                false
            }
            else -> true
        }
    }

}
