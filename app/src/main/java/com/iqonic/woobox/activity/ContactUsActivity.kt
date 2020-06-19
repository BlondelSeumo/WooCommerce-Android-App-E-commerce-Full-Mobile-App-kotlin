package com.iqonic.woobox.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class ContactUsActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        title = getString(R.string.title_contactus)
        setToolbar(toolbar)

        llCallRequest.onClick {
            dialNumber(getString(R.string.contact_phone))
        }
        llEmail.onClick {
            launchActivity<EmailActivity>()
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

        menuWishItem.actionView.onClick { launchActivity<MyCartActivity> { } }
        setCartCount()
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
}
