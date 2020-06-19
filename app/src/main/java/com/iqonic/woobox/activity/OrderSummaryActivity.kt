package com.iqonic.woobox.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemCartBinding
import com.iqonic.woobox.databinding.ItemUserAddressBinding
import com.iqonic.woobox.models.*
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.Constants.KeyIntent.DATA
import com.iqonic.woobox.utils.Constants.KeyIntent.PRODUCT_ID
import com.iqonic.woobox.utils.Constants.RequestCode.ADD_ADDRESS
import com.iqonic.woobox.utils.Constants.SharedPref.CART_DATA
import com.iqonic.woobox.utils.Constants.isAllowedToCreate
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.dialog_change_address.*
import kotlinx.android.synthetic.main.item_address.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class OrderSummaryActivity : AppBaseActivity() {

    private lateinit var dialog: Dialog
    private val mImg = ArrayList<String>()
    var mCartModel: CartResponse? = null
    var selected: Int = 0

    private var mAddressAdapter = object : BaseRecyclerAdapter<Address, ItemUserAddressBinding>() {
        override val layoutResId: Int get() = R.layout.item_user_address

        override fun onBindData(
            model: Address,
            position: Int,
            dataBinding: ItemUserAddressBinding
        ) {
            if (position == selected) {
                dataBinding.included.tvEdit.show()
            } else {
                dataBinding.included.tvEdit.hide()
            }

            dataBinding.rbDefaultAddress.isChecked = position == selected
            dataBinding.included.tvUserName.text = """${model.first_name} ${model.last_name}"""
            dataBinding.included.tvAddress.text = model.getAddress()
            dataBinding.included.tvMobileNo.text = model.contact

            dataBinding.included.tvEdit.onClick {
                launchActivity<AddAddressActivity>(ADD_ADDRESS) {
                    putExtra(DATA, model)
                }
            }
        }

        override fun onItemClick(view: View, model: Address, position: Int, dataBinding: ItemUserAddressBinding) {
            setDefaultAddress(position)
        }

        override fun onItemLongClick(view: View, model: Address, position: Int) {

        }
    }

    private var cartAdapter: BaseRecyclerAdapter<CartResponse, ItemCartBinding> =
        object : BaseRecyclerAdapter<CartResponse, ItemCartBinding>() {
            override val layoutResId: Int = R.layout.item_cart

            override fun onBindData(
                model: CartResponse,
                position: Int,
                dataBinding: ItemCartBinding
            ) {
                mCartModel = model
                if (model.sale_price.isNotEmpty()) {
                    dataBinding.tvPrice.text =
                        (model.sale_price.toInt() * model.quantity.toInt()).toString()
                            .currencyFormat()
                } else {
                    dataBinding.tvPrice.text =
                        (model.price.toFloat().toInt() * model.quantity.toInt()).toString()
                            .currencyFormat()
                }
                dataBinding.tvOriginalPrice.text = model.regular_price.currencyFormat()
                dataBinding.tvOriginalPrice.applyStrike()
                dataBinding.edtQty.text = model.quantity
                if (model.full != null) dataBinding.ivProduct.loadImageFromUrl(model.full)
            }

            override fun onItemClick(view: View, model: CartResponse, position: Int, dataBinding: ItemCartBinding) {
                when (view.id) {
                    else -> {
                        launchActivity<ProductDetailActivityNew> {
                            putExtra(PRODUCT_ID, model.pro_id.toInt())
                        }
                    }
                }
            }

            override fun onItemLongClick(view: View, model: CartResponse, position: Int) {
            }
        }


    private fun setDefaultAddress(position: Int) {
        selected = position
        mAddressAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        setToolbar(toolbar)
        title = getString(R.string.order_summary)
        BroadcastReceiverExt(this) {
            onAction(Constants.AppBroadcasts.ADDRESS_UPDATE) {
                loadAddressList()
            }
        }
        getOffers()

        rvItems.apply { setVerticalLayout(); adapter = cartAdapter }
        cartAdapter.addItems(getCartListFromPref())
        getCartTotal()

        initChangeAddressDialog()
        btnChangeAddress.onClick {
            if (mAddressAdapter.size == 0) {
                launchActivity<AddAddressActivity>(ADD_ADDRESS)
            } else {
                dialog.show()
            }
        }
        val mPaymentDetail = getCartTotal()
        tvReset.text = mPaymentDetail.toString().currencyFormat()
        tvApply.onClick { createOrder() }
        if (getAddressList().size == 0) {
            launchActivity<AddAddressActivity>(ADD_ADDRESS)
            llAddress.hide()
        } else {
            llAddress.show()
        }
    }

    private fun getOffers() {
        getSlideImagesFromPref().forEach { mImg.add(it.image) }
        if (mImg.isNotEmpty()) {
            val handler = Handler()
            val runnable = object : Runnable {
                var i = 0
                override fun run() {
                    ivOffer.loadImageFromUrl(mImg[i])
                    i++
                    if (i > mImg.size - 1) {
                        i = 0
                    }
                    handler.postDelayed(this, 3000)
                }
            }
            handler.postDelayed(runnable, 3000)
        }

    }

    private fun initChangeAddressDialog() {

        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.dialog_change_address)
        dialog.window?.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.tvAddNewAddress.onClick {
            launchActivity<AddAddressActivity>(ADD_ADDRESS)
        }
        dialog.tvItemDeliverHere.onClick {
            dialog.dismiss()
            llAddress.show()
            updateAddress()
        }
        dialog.rvAddress.setVerticalLayout()
        dialog.rvAddress.adapter = mAddressAdapter
        loadAddressList()
        updateAddress()
    }

    private fun updateAddress() {
        if (mAddressAdapter.mModelList.isNotEmpty()) {
            var it = mAddressAdapter.mModelList[selected]
            tvUserName.text = "${it.first_name} ${it.last_name}"
            tvAddress.text = it.getAddress()
            tvMobileNo.text = it.contact

        }
    }

    private fun loadAddressList() {
        val list = getAddressList()
        mAddressAdapter.clearData()
        val id = getSharedPrefInstance().getIntValue(Constants.SharedPref.KEY_ADDRESS, 0)
        list.forEachIndexed { index, address ->
            if (address.ID == id) {
                selected = index
            }
        }
        mAddressAdapter.addItems(list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ADDRESS && resultCode == Activity.RESULT_OK) {
            loadAddressList()
            dialog.show()
        }
    }

    private fun createOrder() {
        val requestModel = RequestModel()
        val mData = ArrayList<LinItemsRequest>()
        getCartListFromPref().forEach {
            val mlineitem = LinItemsRequest()
            mlineitem.product_id = it.pro_id.toInt()
            mlineitem.quantity = it.quantity.toInt()
            mlineitem.variation_id = it.pro_id.toInt()
            mData.add(mlineitem)
        }
        requestModel.line_items = mData
        val mShipping = ArrayList<BillingShippingRequest>()
        getAddressList().forEach {
            val mShippingRequest = BillingShippingRequest()
            mShippingRequest.email= getEmail()
            mShippingRequest.first_name = it.first_name
            mShippingRequest.last_name = it.last_name
            mShippingRequest.address_1 = it.address_1
            mShippingRequest.address_2 = it.address_2
            mShippingRequest.city = it.city
            mShippingRequest.state = it.state
            mShippingRequest.postcode = it.postcode
            mShippingRequest.country = it.state
            mShippingRequest.phone = it.contact
            mShipping.add(mShippingRequest)
        }
        requestModel.shipping = mShipping
        requestModel.customer_id = getUserId().toInt()

        if (isAllowedToCreate) {
            getAlertDialog(getString(R.string.msg_want_to_order), onPositiveClick = { dialog, i ->
                dialog.dismiss()
                showProgress(true)
                callApi(getRestApis().createOrder(requestModel), onApiSuccess = {
                    val request = RequestModel(); request.order_id = it.id
                    callApi(getRestApis(false).getCheckoutUrl(request), onApiSuccess = { res ->
                        snackBar("Successfully Processed")
                        cartAdapter.clearData()
                        showProgress(false)
                        fetchAndStoreCartData()
                        launchActivityWithNewTask<DashBoardActivity>()
                        if (res.checkout_url.isNotEmpty()) {
                            openCustomTab(res.checkout_url)
                        }
                    }, onApiError = {
                        showProgress(false)
                        snackBarError(it)
                    }, onNetworkError = {
                        showProgress(false)
                        noInternetSnackBar()
                    })
                }, onApiError = {
                    showProgress(false)
                    snackBarError(it)
                }, onNetworkError = {
                    showProgress(false)
                    noInternetSnackBar()
                })
            }, onNegativeClick = { dialog, i ->
                dialog.dismiss()
            }).show()

        } else {
            toast(getString(R.string.msg_not_allowed))
        }
    }

    override fun onResume() {
        if (getSharedPrefInstance().getStringValue(CART_DATA) == "") {
            finish()
        }
        super.onResume()
    }
}