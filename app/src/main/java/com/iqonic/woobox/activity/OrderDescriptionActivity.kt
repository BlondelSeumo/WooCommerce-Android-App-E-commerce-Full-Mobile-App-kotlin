package com.iqonic.woobox.activity

import android.os.Bundle
import android.view.View
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemOrderBinding
import com.iqonic.woobox.models.MyOrderData
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.Constants.OrderStatus.CANCELLED
import com.iqonic.woobox.utils.Constants.OrderStatus.COMPLETED
import com.iqonic.woobox.utils.Constants.OrderStatus.ONHOLD
import com.iqonic.woobox.utils.Constants.OrderStatus.PENDING
import com.iqonic.woobox.utils.Constants.OrderStatus.PROCESSING
import com.iqonic.woobox.utils.Constants.OrderStatus.REFUNDED
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_orderdescription.*
import kotlinx.android.synthetic.main.layout_paymentdetail.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import kotlin.math.roundToInt

class OrderDescriptionActivity : AppBaseActivity() {

    private lateinit var mOrderItemAdapter: BaseRecyclerAdapter<MyOrderData.LineItem, ItemOrderBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderdescription)
        title = getString(R.string.title_my_orders)
        setToolbar(toolbar)
        //disableHardwareRendering(llTrack)

        val orderModel = intent.getSerializableExtra(Constants.KeyIntent.DATA) as MyOrderData
        mOrderItemAdapter = object : BaseRecyclerAdapter<MyOrderData.LineItem, ItemOrderBinding>() {
            override val layoutResId: Int = R.layout.item_order

            override fun onBindData(model: MyOrderData.LineItem, position: Int, dataBinding: ItemOrderBinding) {
                dataBinding.tvPrice.text = model.total.roundToInt().toString().currencyFormat(orderModel.currency)
                dataBinding.tvOriginalPrice.text = model.price.toString().currencyFormat(orderModel.currency)
                dataBinding.tvTotalItem.text = String.format(getString(R.string.   lbl_total_item_count) + model.quantity)
                dataBinding.tvOriginalPrice.applyStrike()
            }

            override fun onItemClick(view: View, model: MyOrderData.LineItem, position: Int, dataBinding: ItemOrderBinding) {}

            override fun onItemLongClick(view: View, model: MyOrderData.LineItem, position: Int) {}

        }
        rvOrderItems.setVerticalLayout()
        rvOrderItems.adapter = mOrderItemAdapter
        bindOrderData(orderModel)

        llTrack.onClick {
            launchActivity<TrackItemActivity> {
                putExtra(Constants.KeyIntent.DATA, orderModel)
            }
        }
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            txtRatings.text = rating.toString()
        }
        tvOffer.text = getString(R.string.text_offer_not_available)
    }

    private fun bindOrderData(orderModel: MyOrderData) {
        mOrderItemAdapter.addItems(orderModel.line_items)
        val track1: String
        val track2: String
        ivCircle.setCircleColor(color(R.color.track_yellow))

        when (orderModel.status) {
            PENDING -> {
                track1 = "Order <font color=#ECC134>Pending</font>"
                track2 = getString(R.string.lbl_order_pending)
            }
            PROCESSING -> {
                track1 = "Order <font color=#64B931>Processing</font>"
                track2 = getString(R.string.lbl_item_delivering)
                ivCircle.setCircleColor(color(R.color.track_green))
            }
            ONHOLD -> {
                track1 = "Order <font color=#ECC134>On Hold</font>"
                track2 = "Order on hold"
            }
            COMPLETED -> {
                track1 = "Order <font color=#64B931>Placed</font>"
                track2 = "Order <font color=#64B931>Completed</font>"
                tvDeliveryDate.text = toDate(orderModel.date_completed!!)
                tvTrack2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black, 0)
                tvDeliveryDate.show()
                tvDelivered.show()
                ivCircle.setCircleColor(color(R.color.track_green))
                ivLine.setLineColor(color(R.color.track_green))
                ivCircle1.setCircleColor(color(R.color.track_green))
            }
            CANCELLED -> {
                ivCircle.setCircleColor(color(R.color.track_red))
                track1 = "Order <font color=#F61929>Cancelled</font>"
                track2 = "Order Cancelled"
            }
            REFUNDED -> {
                ivCircle.setCircleColor(color(R.color.track_grey))
                track1 = "Order <font color=#D3D3D3>Refunded</font>"
                track2 = "Order Refunded"
            }
            else -> {
                ivCircle.setCircleColor(color(R.color.track_red))
                track1 = "Order <font color=#F61929>Trashed</font>"
                track2 = "Order Trashed"
            }
        }

        tvDate.text = toDate(orderModel.date_created)
        tvTrack1.text = track1.getHtmlString()
        tvTrack2.text = track2.getHtmlString()
        tvOrderId.text = orderModel.number.toString()
        title = orderModel.number.toString()
        tvOrderDate.text = toDate(orderModel.date_created)
        if (orderModel.payment_method_title.isNotEmpty()) {
            llPaymentMethod.show()
            tvPaymentMethod.text = orderModel.payment_method_title
        }
        if (orderModel.shipping_total == 0.0) {
            tvShippingCharge.text = getString(R.string.lbl_free)
        } else {
            tvShippingCharge.text = orderModel.shipping_total.roundToInt().toString().currencyFormat(orderModel.currency)
        }
        tvTotalAmount.text = ((orderModel.total - orderModel.discount_total) + orderModel.shipping_total).toString().currencyFormat(orderModel.currency)
    }
}
