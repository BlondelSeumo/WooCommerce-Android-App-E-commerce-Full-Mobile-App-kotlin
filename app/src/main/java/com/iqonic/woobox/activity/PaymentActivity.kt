package com.iqonic.woobox.activity

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.adapter.RecyclerViewAdapter
import com.iqonic.woobox.models.MyOrderData
import com.iqonic.woobox.models.Payment
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.utils.Constants.KeyIntent.DATA
import com.iqonic.woobox.utils.extensions.*
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.item_payment.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject

class PaymentActivity : AppBaseActivity(), PaymentResultListener {
    private var orderData: MyOrderData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setToolbar(toolbar)
        title = getString(R.string.title_payment)

        orderData = intent.getSerializableExtra(DATA) as MyOrderData

        Checkout.preload(this)

        callApi(getRestApis().paymentGateways(), onApiSuccess = {
            val paymentGatewaysAdapter = RecyclerViewAdapter<Payment>(R.layout.item_payment, onBind = { view, item, position ->
                view.tvPaymentGateway.text = item.method_title
            })
            rvPaymentGateways.layoutManager = LinearLayoutManager(this@PaymentActivity)
            rvPaymentGateways.adapter = paymentGatewaysAdapter
            paymentGatewaysAdapter.addItems(it)
            paymentGatewaysAdapter.onItemClick = { pos, view, item ->
                handlerPaymentClick(item)
            }
        }, onApiError = {
            toast(it)
        }, onNetworkError = {
            noInternetSnackBar()
        })

        addPaymentDetails()



        tvPayWithPayPal.onClick { createPaymentRequest("paypal") }
        tvNetBanking.onClick {}
        tvCash.onClick { createPaymentRequest("cod") }
    }

    private fun handlerPaymentClick(item: Payment) {
        when (item.id) {
            "cod" -> {
                createPaymentRequest("cod")
            }
            "paypal" -> {
                createPaymentRequest("paypal")
            }
            "woobox_razorpay" -> {
                handleRazorPay()
            }
        }
    }

    private fun handleRazorPay() {
        val checkout = Checkout()
        checkout.setImage(R.drawable.ic_app_icon)

        try {
            val options = JSONObject()
            options.put("name", "Iqonic")
            options.put("description", "")
            options.put("currency", "INR")
            //options.put("order_id", orderData?.id)
            options.put("amount", (getCartTotalAmount()!! * 100).toDouble())
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png")

            Log.d(this.localClassName, options.toString())
            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e(this.localClassName, "Error in starting Razorpay Checkout", e)
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Log.d(this.localClassName, code.toString())
        toast(response!!)
        Log.d(this.localClassName, response)
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Log.d(this.localClassName, razorpayPaymentID)

        val requestModel = RequestModel().apply {
            order_id = orderData?.id
            payment_method = "woobox_razorpay"
            txn_id = razorpayPaymentID
        }

        showProgress(true)
        callApi(getRestApis().processOtherPayment(requestModel), onApiSuccess = {
            callApi(getRestApis(false).clearCartItems(), onApiSuccess = {
                showProgress(false)
                fetchAndStoreCartData()
                launchActivityWithNewTask<DashBoardActivity>()
            })
        }, onApiError = {
            showProgress(false)
            toast(it)
        }, onNetworkError = {
            showProgress(false)
            noInternetSnackBar()
        })
    }

    private fun createPaymentRequest(s: String) {
        val requestModel = RequestModel()
        requestModel.order_id = orderData?.id
        requestModel.payment_method = s
        processPayment(requestModel)
    }

    private fun addPaymentDetails() {
        getCartTotal()
    }
}