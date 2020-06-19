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
import kotlinx.android.synthetic.main.activity_email.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class EmailActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)
        title = getString(R.string.lbl_email)
        setToolbar(toolbar)
        btnSendMail.onClick {
            when {
                validate() -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.text_iqonicdesign_gmail_com), null))
                    emailIntent.putExtra(Intent.EXTRA_TEXT, edtDescription.text.toString())
                    startActivity(Intent.createChooser(emailIntent, context.getString(R.string.lbl_send_email)))
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

    private fun setCartCount() {
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
            edtDescription.checkIsEmpty() -> {
                edtDescription.showError(getString(R.string.error_field_required))
                false
            }
            else -> true
        }
    }

}
