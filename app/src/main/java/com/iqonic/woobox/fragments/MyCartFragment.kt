package com.iqonic.woobox.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.activity.*
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemCartBinding
import com.iqonic.woobox.models.CartResponse
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.utils.Constants.KeyIntent.PRODUCT_ID
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.layout_paymentdetail.*

class MyCartFragment : BaseFragment() {

    private var mCartAdapter: BaseRecyclerAdapter<CartResponse, ItemCartBinding> =
        object : BaseRecyclerAdapter<CartResponse, ItemCartBinding>() {
            override val layoutResId: Int = R.layout.item_cart

            override fun onBindData(
                model: CartResponse,
                position: Int,
                dataBinding: ItemCartBinding
            ) {
                dataBinding.llButton.show()
                dataBinding.llMoveTocart.hide()
                dataBinding.tvOriginalPrice.applyStrike()
                dataBinding.edtQty.setText(model.quantity)
                if (model.full != null) dataBinding.ivProduct.loadImageFromUrl(model.full)
                if (model.sale_price.isNotEmpty()) {
                    dataBinding.tvPrice.text =
                        (model.sale_price.toInt() * model.quantity.toInt()).toString()
                            .currencyFormat()
                } else if (model.price.isNotEmpty()) {
                    dataBinding.tvPrice.text =
                        (model.price.toFloat().toInt() * model.quantity.toInt()).toString()
                            .currencyFormat()
                }
                dataBinding.tvOriginalPrice.text = (model.regular_price.toFloat().toInt()*model.quantity.toInt()).toString().currencyFormat()
            }

            override fun onItemClick(
                view: View,
                model: CartResponse,
                position: Int,
                dataBinding: ItemCartBinding
            ) {
                when (view.id) {
                    R.id.llRemove -> {
                        val requestModel = RequestModel()
                        requestModel.pro_id = model.pro_id

                        removeCartItem(requestModel)
                    }
                    R.id.ivIncreaseQuantity -> {
                        val qty = model.quantity.toInt()
                        if (model.stock_quantity != null) {
                            if (qty < model.stock_quantity) {
                                mModelList[position].quantity = qty.plus(1).toString()
                                notifyItemChanged(position)
                                updateCartItem(mModelList[position])
                            } else {
                                activity?.snackBarError("${getString(R.string.lbl_qty_error)} ${model.stock_quantity}")
                            }
                        } else {
                            if (qty < 10) {
                                mModelList[position].quantity = qty.plus(1).toString()
                                notifyItemChanged(position)
                                updateCartItem(mModelList[position])
                            }else{
                                activity?.snackBarError("${getString(R.string.lbl_qty_error)} 10")
                            }
                        }
                    }
                    R.id.ivDecreaseQuantity -> {
                        val qty = model.quantity.toInt()
                        if (qty >1) {
                            mModelList[position].quantity = qty.minus(1).toString()
                            notifyItemChanged(position)
                            updateCartItem(mModelList[position])
                        }
                    }
                    else -> {
                        activity?.launchActivity<ProductDetailActivityNew> {
                            putExtra(
                                PRODUCT_ID,
                                model.pro_id.toInt()
                            )
                        }
                    }
                }
            }

            override fun onItemLongClick(view: View, model: CartResponse, position: Int) {}
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        rvCart.setVerticalLayout()
        rvCart.adapter = mCartAdapter
        tvClear.onClick {
            clearCartItems()
        }
        btnShopNow.onClick {
            if (activity!! is DashBoardActivity) {
                (activity as DashBoardActivity).loadHomeFragment()
            } else if (activity!! is MyCartActivity) {
                (activity as MyCartActivity).shopNow()
            }
        }
        tvContinue.onClick { activity?.launchActivity<OrderSummaryActivity> { } }
        llSeePriceDetail.onClick { scrollToPriceDetail() }
    }

    private fun clearCartItems() {
        showProgress()
        callApi(getRestApis(false).clearCartItems(), onApiSuccess = {
            activity?.fetchAndStoreCartData()
        })
    }

    private fun scrollToPriceDetail() {
        Handler().post { nsvCart.scrollTo(nsvCart.top, llPayment.top) }
    }

    private fun invalidatePaymentLayout(b: Boolean) {
        if (!b) {
            if (activity != null) {
                llNoItems.show()
                llPayment.hide()
                lay_button.hide()
                rvCart.hide()
                tvTotalItem.hide()
                tvClear.hide()
            }
        } else {
            if (activity != null) {
                llNoItems.hide()
                llPayment.show()
                lay_button.show()
                rvCart.show()
                tvTotalItem.show()
                tvClear.show()
            }
        }

    }

    private fun removeCartItem(model: RequestModel) {
        activity?.getAlertDialog(
            getString(R.string.msg_confirmation),
            onPositiveClick = { dialog, i ->
                showProgress()
                callApi(getRestApis(false).removeCartItem(request = model), onApiSuccess = {
                    hideProgress()
                    snackBar(activity!!.getString(R.string.success))
                    activity?.fetchAndStoreCartData()
                }, onApiError = {
                    hideProgress()
                    if (activity != null && activity is DashBoardActivity) {
                        if ((activity as DashBoardActivity).selectedFragment is MyCartFragment) {
                            activity?.snackBarError(it)
                        }
                    }
                }, onNetworkError = {
                    hideProgress()
                    activity?.noInternetSnackBar()
                })
            },
            onNegativeClick = { dialog, i ->
                dialog.dismiss()
            })?.show()
    }

    private fun updateCartItem(model: CartResponse) {
        showProgress()
        val requestModel = RequestModel(); requestModel.pro_id = model.pro_id; requestModel.quantity = model.quantity.toInt(); requestModel.cart_id = model.cart_id.toInt()
        callApi(getRestApis(false).updateItemInCart(request = requestModel), onApiSuccess = {
            snackBar(getString(R.string.lbl_success))
            activity?.fetchAndStoreCartData()
            hideProgress()
        }, onApiError = {
            hideProgress()
            activity?.snackBarError(it)
        }, onNetworkError = {
            hideProgress()
            activity?.noInternetSnackBar()
        })
    }
    fun invalidateCartLayout(it: ArrayList<CartResponse>) {
        hideProgress()
        if (it.size == 0) {
            invalidatePaymentLayout(false)
        } else {
            if (activity != null) {
                llNoItems.hide()
                if (it.size == 1) {
                    tvTotalItem.text = getString(R.string.lbl_total_items) + "${it.size})"
                } else {
                    tvTotalItem.text = getString(R.string.lbl_total_items) + "${it.size})"
                }
                if (it.size > 5) txtSeePriceDetails.text =
                    getString(R.string.lbl_see_price_detail) else txtSeePriceDetails.text =
                    getString(R.string.lbl_total_amount)
                val total = (activity as AppBaseActivity).getCartTotal()
                tvTotalCartAmount.text = total.toString().currencyFormat()
                tvShippingCharge.text = getString(R.string.lbl_free)
                tvTotalAmount.text = total.toString().currencyFormat()
                invalidatePaymentLayout(true)
                mCartAdapter.addItems(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        invalidateCartLayout(getCartListFromPref())
    }
}
