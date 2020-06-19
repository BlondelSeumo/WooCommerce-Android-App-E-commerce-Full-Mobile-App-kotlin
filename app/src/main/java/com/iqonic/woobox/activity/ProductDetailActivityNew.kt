package com.iqonic.woobox.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.adapter.ProductImageAdapter
import com.iqonic.woobox.adapter.RecyclerViewAdapter
import com.iqonic.woobox.models.ProductModelNew
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.utils.Constants.KeyIntent.IS_ADDED_TO_CART
import com.iqonic.woobox.utils.Constants.KeyIntent.PRODUCT_ID
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_product_detail_new.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlin.math.abs


class ProductDetailActivityNew : AppBaseActivity() {
    private var mPId = 0
    private val mImages = ArrayList<String>()
    private lateinit var mMenuCart: View
    private var isAddedTocart: Boolean = false
    var i: Int = 0
    private var mAttributeAdapter: RecyclerViewAdapter<String>? = null
    private var mYearAdapter: ArrayAdapter<String>? = null
    private var mProductModel: ProductModelNew? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_detail_new)
        setToolbar(toolbar)

        if (intent?.extras?.get(PRODUCT_ID) == null) {
            toast(R.string.error_something_went_wrong)
            finish()
            return
        }
        getProductDetail()

        tvItemProductOriginalPrice.applyStrike()

        isAddedTocart = intent.getBooleanExtra(IS_ADDED_TO_CART, false)
        if (!isAddedTocart) btnAddCard.text =
            getString(R.string.lbl_add_to_cart) else btnAddCard.text =
            getString(R.string.lbl_remove_cart)

        btnAddCard.onClick {
            if (isLoggedIn()) {
                if (isAddedTocart) removeCartItem() else addItemToCart()
            } else launchActivity<SignInUpActivity>()

        }


        toolbar_layout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) - app_bar.totalScrollRange == 0) {
                toolbar_layout.title = tvName.text
            } else {
                toolbar_layout.title = ""
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        val menuWishItem: MenuItem = menu!!.findItem(R.id.action_cart)
        val menuSearch: MenuItem = menu.findItem(R.id.action_search)
        menuWishItem.isVisible = true
        menuSearch.isVisible = false
        mMenuCart = menuWishItem.actionView
        menuWishItem.actionView.onClick {
            launchActivity<MyCartActivity> { }
        }
        setCartCount()
        return super.onCreateOptionsMenu(menu)
    }


    fun setCartCount() {
        val count = getCartCount()
        mMenuCart.tvNotificationCount.text = count
        if (count.checkIsEmpty() || count == "0") {
            mMenuCart.tvNotificationCount.hide()
        } else {
            mMenuCart.tvNotificationCount.show()
        }
    }


    private fun addItemToCart() {
        if (mProductModel?.stock_quantity != null && mProductModel?.stock_quantity!! < 1) {
            toast(R.string.msg_sold_out); return
        }
        val requestModel = RequestModel(); requestModel.pro_id =
            mProductModel?.id.toString(); requestModel.quantity = 1;
        requestModel.color = "";requestModel.size = ""
        showProgress(true)
        callApi(getRestApis(false).addItemToCart(request = requestModel), onApiSuccess = {
            showProgress(false)
            snackBar(getString(R.string.success_add))
            fetchAndStoreCartData()
        }, onApiError = {
            showProgress(false)
            snackBarError(it)
            fetchAndStoreCartData()
        }, onNetworkError = {
            showProgress(false)
            noInternetSnackBar()
        })
        btnAddCard.text = getString(R.string.lbl_remove_cart)
    }

    private fun removeCartItem() {
        getAlertDialog(getString(R.string.msg_confirmation), onPositiveClick = { dialog, i ->
            val requestModel = RequestModel()
            requestModel.pro_id = mProductModel?.id.toString()
            showProgress(true)
            callApi(getRestApis(false).removeCartItem(request = requestModel), onApiSuccess = {
                showProgress(false)
                btnAddCard.text = getString(R.string.lbl_add_to_cart)
                snackBar(getString(R.string.success))
                fetchAndStoreCartData()
            }, onApiError = {
                showProgress(false)
                snackBarError(it)
                //getCartItems()
                fetchAndStoreCartData()
            }, onNetworkError = {
                showProgress(false)
                noInternetSnackBar()
            })
        }, onNegativeClick = { dialog, i ->
            dialog.dismiss()
        }).show()
    }

    @SuppressLint("SetTextI18n")
    private fun getProductDetail() {
        mPId = intent?.getIntExtra(PRODUCT_ID, 0)!!
        scrollView.visibility = View.GONE
        if (isNetworkAvailable()) {
            showProgress(true)
            callApi(getRestApis().listSingleProduct(mPId), onApiSuccess = {

                if (it.isEmpty()) {
                    return@callApi
                }
                mProductModel = it[0]

                scrollView.visibility = View.VISIBLE
                /**
                 * Header Images
                 *
                 */
                it[0].images.forEach { image ->
                    mImages.add(image.src)
                }
                llReviews.onClick {
                    Log.e("pro id",mProductModel?.id.toString())
                    launchActivity<ReviewsActivity> {
                        putExtra(PRODUCT_ID, it[0].id)

                    }
                }
                tvAllReviews.onClick {
                    Log.e("pro id",mProductModel?.id.toString())
                    launchActivity<ReviewsActivity> {
                        putExtra(PRODUCT_ID, it[0].id)

                    }
                }

                val adapter1 = ProductImageAdapter(mImages)
                productViewPager.adapter = adapter1
                dots.attachViewPager(productViewPager)
                dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
                tvItemProductOriginalPrice.applyStrike()
                /**
                 * Other Information
                 *
                 */
                tvName.text = it[0].name
                toolbar_layout.title = it[0].name
                tvItemProductRating.rating = it[0].average_rating.toFloat()
                tvTags.setText(it[0].description.getHtmlString().toString())
                tvTags.isMoreLessShow = true

                /**
                 * check stock
                 */
                if (it[0].in_stock) {
                    btnOutOfStock.hide()
                    btnAddCard.show()
                } else {
                    btnOutOfStock.show()
                    btnAddCard.hide()
                }

                /**
                 * Additional information
                 *
                 */
                for (att in it[0].attributes!!) {
                    val vi =
                        applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val v: View = vi.inflate(R.layout.view_attributes, null)
                    val textView =
                        v.findViewById<View>(R.id.txtAttName) as TextView
                    textView.text = att.name.toString() + " : "

                    val sizeList = ArrayList<String>()
                    val sizes = att.options
                    sizes?.forEachIndexed { i, s ->
                        sizeList.add(s.trim())
                    }
                    mAttributeAdapter =
                        RecyclerViewAdapter(
                            R.layout.item_attributes,
                            onBind = { vv, item, position ->
                                if (item.isNotEmpty()) {
                                    val attSize =
                                        vv.findViewById<View>(R.id.tvSize) as TextView
                                    if (sizeList.size - 1 == position) {
                                        attSize.text = item
                                    } else {
                                        attSize.text = "$item ,"
                                    }
                                }
                            })
                    mAttributeAdapter?.addItems(sizeList)
                    val recycleView = v.findViewById<View>(R.id.rvAttributeView) as RecyclerView
                    recycleView.setHorizontalLayout()
                    recycleView.adapter = mAttributeAdapter

                    llAttributeView.addView(
                        v,
                        0,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT
                        )
                    )
                }

                /**
                 *  Attribute Information
                 */
                if (it[0].type == "simple") {
                    tvAvailability.text = it[0].attributes!![0].name.toString()
                    llAttribute.hide()
                    if (it[0].on_sale) {
                        tvPrice.text = it[0].sale_price.currencyFormat()
                        tvSale.show()
                        tvItemProductOriginalPrice.applyStrike()
                        tvItemProductOriginalPrice.text = it[0].regular_price.currencyFormat()
                        val discount =
                            calculateDiscount(it[0].sale_price, it[0].regular_price)
                        if (discount > 0.0) {
                            tvSaleDiscount.visibility = View.VISIBLE
                            tvSaleDiscount.text =
                                String.format("%.2f", discount) + "% Off"
                        }
                        upcomingSale.visibility = View.GONE
                    } else {
                        tvPrice.text = it[0].price?.currencyFormat()
                        tvSale.hide()
                        showUpComingSale(it[0])
                    }
                    mProductModel = it[0]
                } else if (it[0].type == "variable") {
                    llAttribute.show()
                    mProductModel = it[0]
                    if (it[0].attributes.isNotEmpty()) {
                        val sizeList = ArrayList<String>()
                        val mVariationsList = ArrayList<Int>()
                        val mVariations = it[0].variations
                        it.forEachIndexed { i, details ->
                            if (i > 0) {
                                var option = ""
                                it[i].attributes!!.forEach { attr ->
                                    option = if (option.isNotBlank()) {
                                        option + " - " + attr.option.toString()
                                    } else {
                                        attr.option.toString()
                                    }
                                }
                                if (details.on_sale) {
                                    option = "$option [Sale]"
                                }
                                sizeList.add(option)
                            }
                        }

                        mVariations.forEachIndexed { index, s ->
                            mVariationsList.add(s)
                        }
                        mYearAdapter = ArrayAdapter(this, R.layout.spinner_items, sizeList)
                        spAttribute.adapter = this.mYearAdapter

                        spAttribute.onItemSelectedListener = object : OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                it.forEach { its ->
                                    if (mVariationsList[position] == its.id) {
                                        setPriceDetail(its)
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                            }
                        }

                    } else {
                        llAttribute.hide()
                    }
                }

                // Purchasable
                if (!it[0].purchasable) {
                    banner_container.hide()
                } else {
                    banner_container.show()
                }

                // Review
                if (it[0].reviews_allowed) {
                    tvAllReviews.show()
                    llReviews.show()
                } else {
                    llReviews.hide()
                    tvAllReviews.hide()
                }
                showProgress(false)


            }, onApiError = {
                showProgress(false)
                snackBar(it)
            })
        }
    }

    private fun calculateDiscount(salePrice: String?, regularPrice: String?): Float {
        return (100f - (salePrice!!.toFloat() * 100f) / regularPrice!!.toFloat())
    }

    private fun getPriceDetails(details: ProductModelNew): String {
        val option = details.attributes!![0].option

        var price = ""
        price = if (details.on_sale) {
            val discount =
                calculateDiscount(details.sale_price, details.regular_price)
            if (discount > 0.0) {
                details.sale_price.toString() + " [" + String.format("%.2f", discount) + "% Off]"
            } else {
                details.sale_price.toString()
            }
        } else {
            details.regular_price.toString()
        }
        return option + "-" + price.currencyFormat()
    }

    @SuppressLint("SetTextI18n")
    private fun setPriceDetail(its: ProductModelNew) {
        mProductModel = its
        if (its.on_sale) {
            tvPrice.text = its.price.currencyFormat()
            tvSale.show()
            tvItemProductOriginalPrice.applyStrike()
            tvItemProductOriginalPrice.text =
                its.regular_price.currencyFormat()
            upcomingSale.visibility = View.GONE
        } else {
            tvItemProductOriginalPrice.text = ""
            tvPrice.text = its.regular_price.currencyFormat()
            tvSale.hide()
            showUpComingSale(its)
        }

        tvAvailability.text = its.attributes[0].name.toString()

        mYearAdapter!!.notifyDataSetChanged()
    }

    /**
     * Show Upcoming sale details
     *
     */
    private fun showUpComingSale(its: ProductModelNew) {
        if (its.date_on_sale_from != "") {
            upcomingSale.visibility = View.VISIBLE
            tvUpcomingSale.text =
                "Sale Start from " + its.date_on_sale_from + " to " + its.date_on_sale_to + ". Get amazing discounts on the products."
        } else {
            upcomingSale.visibility = View.GONE
        }
    }
}

