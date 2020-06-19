package com.iqonic.woobox.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*


class CardActivity : AppBaseActivity() {

    private var mYearAdapter: ArrayAdapter<String>? = null
    private lateinit var mMenuCart: View
    var mMonthAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        title = getString(R.string.lbl_add_card)
        setToolbar(toolbar)

        edDigit1.addTextChangedListener(GenericTextWatcher(edDigit1))
        edDigit2.addTextChangedListener(GenericTextWatcher(edDigit2))
        edDigit3.addTextChangedListener(GenericTextWatcher(edDigit3))
        edDigit4.addTextChangedListener(GenericTextWatcher(edDigit4))

        val mMonthList = ArrayList<String>()
        val mYearList = ArrayList<String>()
        mMonthList.add(0, "Select Month")
        mYearList.add(0, "Select Year")
        for (j in 1..12) {
            mMonthList.add(j.toString())
        }
        for (i in 2019..2040) {
            mYearList.add(i.toString())
        }

        mYearAdapter = ArrayAdapter(this, R.layout.spinner_items, mYearList)
        mMonthAdapter = ArrayAdapter(this, R.layout.spinner_items, mMonthList)

        spYear.adapter = this.mYearAdapter
        spMonth.adapter = mMonthAdapter

        ivShowPwd.onClick {
            ivShowPwd.hide()
            ivHidePwd.show()
            edCvv.setSelection(edCvv.length())
            edCvv.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        ivHidePwd.onClick {
            edCvv.setSelection(edCvv.length())
            edCvv.transformationMethod = PasswordTransformationMethod.getInstance()
            ivShowPwd.show()
            ivHidePwd.hide()
        }
        BroadcastReceiverExt(this) {
            onAction(CART_COUNT_CHANGE) {
                setCartCount()
            }
        }
    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {

        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.edDigit1 -> if (text.length == 4)
                    edDigit2.requestFocus()
                R.id.edDigit2 -> if (text.length == 4)
                    edDigit3.requestFocus()
                else if (text.isEmpty())
                    edDigit1.requestFocus()
                R.id.edDigit3 -> if (text.length == 4)
                    edDigit4.requestFocus()
                else if (text.isEmpty())
                    edDigit2.requestFocus()
                R.id.edDigit4 -> if (text.isEmpty())
                    edDigit3.requestFocus()
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        val menuWishItem: MenuItem = menu!!.findItem(R.id.action_cart)
        val menuSearch: MenuItem = menu.findItem(R.id.action_search)
        menuWishItem.isVisible = true
        menuSearch.isVisible = false
        mMenuCart = menuWishItem.actionView
        mMenuCart.ivCart.setColorFilter(this.color(R.color.textColorPrimary))
        menuWishItem.actionView.onClick {
            launchActivity<MyCartActivity> { }
        }
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
