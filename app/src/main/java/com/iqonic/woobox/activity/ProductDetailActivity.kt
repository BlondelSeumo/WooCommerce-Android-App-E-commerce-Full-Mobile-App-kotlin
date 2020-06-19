package com.iqonic.woobox.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.adapter.ProductImageAdapter
import com.iqonic.woobox.adapter.RecyclerViewAdapter
import com.iqonic.woobox.databinding.ActivityProductDetailBinding
import com.iqonic.woobox.models.ProductDataNew
import com.iqonic.woobox.models.ProductReviewData
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.woobox.utils.Constants.KeyIntent.DATA
import com.iqonic.woobox.utils.Constants.KeyIntent.PRODUCT_ID
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.dialog_quantity.*
import kotlinx.android.synthetic.main.item_color.view.*
import kotlinx.android.synthetic.main.item_size.view.*
import kotlin.math.pow
import kotlin.math.roundToInt

class ProductDetailActivity : AppBaseActivity() {

    private lateinit var mMainBinding: ActivityProductDetailBinding
    private var mProductModel: ProductDataNew? = null
    private var mProductId = 0
    private var isAddedToCart: Boolean = false
    private val mImages = ArrayList<String>()
    var i: Int = 0
    private var mIsInWishList = false
    private var mColorFlag: Int = -1
    private var mSizeFlag: Int = -1
    private var mQuntity: String = "1"
    private var mIsColorExist: Boolean = false
    private var mIsSizeExist: Boolean = false
    private var mIsFirstTimeSize = true
    private var colorAdapter: RecyclerViewAdapter<String>? = null
    private var sizeAdapter: RecyclerViewAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeTransparentStatusBar()
        if (intent?.extras?.get(DATA) == null && intent?.extras?.get(PRODUCT_ID) == null) {
            toast(R.string.error_something_went_wrong)
            finish()
            return
        }
        BroadcastReceiverExt(this) {
            onAction(CART_COUNT_CHANGE) {
                setCartCountFromPref()
            }

        }

        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        setToolbarWithoutBackButton(mMainBinding.toolbar)
        toolbar_layout.title = ""
        ivBack.onClick {
            onBackPressed()
        }
        /**
         * Fetch Product Detail From Server
         */
        showProgress(true)


        if (intent?.extras?.get(PRODUCT_ID) != null && intent?.extras?.get(PRODUCT_ID) != null) {
            mProductId = intent?.extras?.getInt(PRODUCT_ID)!!

            showProgress(true)
            val requestModel = RequestModel(); requestModel.pro_id = mProductId.toString()
            callApi(getRestApis().listSingleProducts(requestModel), onApiSuccess = {
                showProgress(false)
                mProductModel = it
                setDetails(it)
            }, onApiError = {
                showProgress(false)
                snackBarError(it)
                runDelayed(1000) { finish() }
            }, onNetworkError = {
                showProgress(false)
                noInternetSnackBar()
                runDelayed(1000) { finish() }
            })
        } else if (intent?.extras?.getSerializable(DATA) != null) {
            mProductModel = intent.getSerializableExtra(DATA) as ProductDataNew
           /*
            setDetails(mProductModel!!)*/
            val requestModel = RequestModel(); requestModel.pro_id = mProductModel?.pro_id.toString()
            callApi(getRestApis().listSingleProducts(requestModel), onApiSuccess = {
                showProgress(false)
                mProductModel = it
                setDetails(it)
            }, onApiError = {
                showProgress(false)
                snackBarError(it)
                runDelayed(1000) { finish() }
            }, onNetworkError = {
                showProgress(false)
                noInternetSnackBar()
                runDelayed(1000) { finish() }
            })
        } else {
            toast(R.string.error_something_went_wrong)
            finish()
        }
        setCartCountFromPref()
        BroadcastReceiverExt(this) {
           onAction(CART_COUNT_CHANGE) { setCartCountFromPref() }
        }
        rlCart.onClick {
            if (isLoggedIn()){
                launchActivity<MyCartActivity>()
            }else{
                launchActivity<SignInUpActivity>()
            }
        }


    }


    private fun setCartCountFromPref() {
        if (isLoggedIn()){
            val count = getCartCount()
            if ( !count.checkIsEmpty() && !count.equals("0", false)) {
                tvNotificationCount.text = count
                tvNotificationCount.show()
            } else {
                tvNotificationCount.hide()
            }
        }

    }

    private fun setDetails(mProductModel: ProductDataNew) {
        mMainBinding.model = mProductModel

        listProductReviews()

        when {
            mProductModel.sale_price!!.isNotEmpty() -> tvPrice.text =
                mProductModel.sale_price.currencyFormat()
            else -> tvPrice.text = mProductModel.price.toString().currencyFormat()
        }
        tvItemProductOriginalPrice.text = mProductModel.regular_price?.currencyFormat()
        tvItemProductOriginalPrice.applyStrike()
        if (mProductModel.regular_price != null && mProductModel.regular_price.isNotEmpty()) {
            val mrp = mProductModel.regular_price.toDouble()
            var discountPrice:Double = when {
                mProductModel.sale_price.isNotEmpty() -> mProductModel.sale_price.toDouble()
                else -> mProductModel.price!!.toDouble()
            }
            if (mrp!=0.0){
                val sub = mrp - discountPrice
                val discount = round(((sub * 100) / mrp),2)
                if (discount == 0.0) {
                    tvItemProductDiscount.hide()
                } else {
                    tvItemProductDiscount.show()
                    tvItemProductDiscount.text = "${discount.toInt()}% Off"
                }
            }else{
                tvItemProductDiscount.hide()
            }

        } else {
            tvItemProductDiscount.hide()
        }
        toolbar_layout.setCollapsedTitleTypeface(fontSemiBold())
        toolbar_layout.setExpandedTitleTypeface(fontSemiBold())
        toolbar_layout.title = mProductModel.name

        intHeaderView()
        checkWishListAndCart()
         if (mProductModel.stock_status=="instock"){
            if (mProductModel.manage_stock!!){
                if ( mProductModel.stock_quantity==null || mProductModel.stock_quantity<1){
                    tvAvailability.text = getString(R.string.lbl_out_stock)
                    btnOutOfStock.show()
                    btnAddCard.hide()
                }else{
                    setStock(mProductModel,mProductModel.stock_quantity)
                }

            }else{
                setStock(mProductModel,null)
            }
        }else{
            tvAvailability.text = getString(R.string.lbl_out_stock)
            btnOutOfStock.show()
            btnAddCard.hide()
        }



        ivFavourite.onClick { onFavouriteClick() }
        llReviews.onClick {
            launchActivity<ReviewsActivity> {
                putExtra(DATA, mProductModel)
            }
        }

        tvAllReviews.onClick {
            launchActivity<ReviewsActivity> {
                putExtra(DATA, mProductModel)
            }
        }
    }
 private fun setStock(
        mProductModel: ProductDataNew,
        stockQuantity: Int?
    ) {
        tvAvailability.text = getString(R.string.lbl_in_stock)
        btnAddCard.onClick {
            if (isLoggedIn()) {
                if (isExistInCart(mProductModel)) removeCartItem() else addItemToCart()
            } else {
                launchActivity<SignInUpActivity>()
            }
        }
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_quantity)

        val size = if (stockQuantity==null ||  stockQuantity >= 5) 5 else stockQuantity
        val list: ArrayList<String> = ArrayList()
        for (i in 1..size) {
            list.add(i.toString())
        }
        dialog.listQuantity.adapter = ArrayAdapter<String>(this, R.layout.item_quantity, R.id.tvQuantity, list)
        dialog.listQuantity.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                mQuntity = list[position]
                tvSelectedQuantity.text = list[position]
                dialog.dismiss()
            }
        tvSelectedQuantity.onClick {
            dialog.show()
        }
    }

    fun round(value: Double, places: Int): Double {
        var double = value
        val factor = 10.0.pow(places.toDouble()).toLong()
        double *= factor
        val tmp = double.roundToInt()
        return tmp.toDouble() / factor
    }
    private fun onFavouriteClick() {
        if (isExistInWishList(mProductModel!!)) {
            changeFavIcon(R.drawable.ic_heart, R.color.gray_80); ivFavourite.isClickable = false

            val requestModel = RequestModel(); requestModel.pro_id =
                mProductModel?.pro_id.toString()
            removeFromWishList(requestModel) {
                ivFavourite.isClickable = true
                if (it) changeFavIcon(
                    R.drawable.ic_heart,
                    R.color.gray_80
                ) else changeFavIcon(
                    R.drawable.ic_heart_fill,
                    R.color.favourite_background,
                    R.color.colorPrimary
                )
            }
        } else {
            if (isLoggedIn()) {
                changeFavIcon(
                    R.drawable.ic_heart_fill,
                    R.color.favourite_background,
                    R.color.colorPrimary
                ); ivFavourite.isClickable = false

                val requestModel = RequestModel(); requestModel.pro_id =
                    mProductModel?.pro_id.toString()
                addToWishList(requestModel) {
                    ivFavourite.isClickable = true
                    if (it) changeFavIcon(
                        R.drawable.ic_heart_fill,
                        R.color.favourite_background,
                        R.color.colorPrimary
                    ) else changeFavIcon(R.drawable.ic_heart, R.color.gray_80)
                }
            } else {
                launchActivity<SignInUpActivity>()
            }
        }
    }

    private fun intHeaderView() {
        mProductModel?.gallery?.forEach { mImages.add(it!!) }
        val imageAdapter = ProductImageAdapter(mImages)
        productViewPager.adapter = imageAdapter
        dots.attachViewPager(productViewPager)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        setDescription()
        setMoreInfo()
        tvItemProductRating.rating = mProductModel?.average_rating?.toFloat()!!
        tvItemProductOriginalPrice.applyStrike()
    }

    private fun setMoreInfo() {
        if (!mProductModel?.length?.checkIsEmpty()!!) {
            llMoreInfo.show()
            tvLength.text = String.format("%s %s", mProductModel?.length, "cm")
        } else {
            llLength.hide()
            tvLength.text = ""
        }
        if (!mProductModel?.height?.checkIsEmpty()!!) {
            llMoreInfo.show()
            tvHeight.text = String.format("%s %s", mProductModel?.height, "cm")
        } else {
            llHeight.hide()
            tvHeight.text = ""
        }
        if (!mProductModel?.width?.checkIsEmpty()!!) {
            llMoreInfo.show()
            tvWidth.text = String.format("%s %s", mProductModel?.width, "cm")
        } else {
            llWidth.hide()
            tvWidth.text = ""
        }

    }

    private fun setDescription() {
        bindData()
    }

    private fun bindData() {
        txtDescription.setText(mProductModel?.description?.getHtmlString().toString())

        if (mProductModel?.color != null && mProductModel?.color?.isNotEmpty()!!) {
            val colorList = ArrayList<String>()
            val colors = mProductModel?.color?.split(",")
            mIsColorExist = colors != null && colors.isNotEmpty()

            colors?.forEachIndexed { _, s ->
                colorList.add(s.trim())
            }
            colorAdapter =
                RecyclerViewAdapter(R.layout.item_color, onBind = { view, item, position ->
                    if (item.isNotEmpty()) {
                        try {
                            if (item.contains("#")) {
                                view.viewColor.show()
                                view.tvColor.hide()
                                view.viewColor.setStrokedBackground(
                                    Color.parseColor(item.trim()),
                                    strokeColor = color(R.color.white)
                                )
                                view.ivColorChecked.setStrokedBackground(
                                    Color.parseColor(item.trim()),
                                    strokeColor = color(R.color.white)
                                )
                                when (position) {
                                    mColorFlag -> {
                                        view.viewColor.hide()
                                        view.ivColorChecked.show()
                                    }
                                    else -> {
                                        view.viewColor.show()
                                        view.ivColorChecked.hide()
                                    }
                                }
                            } else {
                                view.viewColor.hide()
                                view.tvColor.show()
                                view.tvColor.text = item
                                view.tvColor.apply {
                                    when (position) {
                                        mColorFlag -> {
                                            setTextColor(color(R.color.commonColorWhite))
                                            setStrokedBackground(color(R.color.colorPrimary))
                                        }
                                        else -> {
                                            setTextColor(color(R.color.textColorPrimary))
                                            setStrokedBackground(
                                                0,
                                                strokeColor = color(R.color.view_color)
                                            )
                                        }
                                    }

                                }

                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            colorAdapter?.onItemClick = { pos, view, item ->
                mColorFlag = pos
                colorAdapter?.notifyDataSetChanged()
                getSelectedColors()
            }
            colorAdapter?.addItems(colorList)
            rvColors.setHorizontalLayout()
            rvColors.adapter = colorAdapter
        } else {
            tvColors.hide()
            rvColors.hide()
        }

        if (mProductModel?.size != null && mProductModel?.size?.isNotEmpty()!!) {

            val sizeList = ArrayList<String>()
            val sizes = mProductModel?.size?.split(",")
            mIsSizeExist = sizes != null && sizes.isNotEmpty()
            sizes?.forEachIndexed { i, s -> sizeList.add(s.trim()) }

            sizeAdapter = RecyclerViewAdapter(R.layout.item_size, onBind = { view, item, position ->
                if (item.isNotEmpty()) {
                    view.llSize.show()
                    view.ivSizeChecked.text = item
                    if (mIsFirstTimeSize && mProductModel?.size == item) {
                        mIsFirstTimeSize = false
                        mSizeFlag = position
                    }
                    view.ivSizeChecked.apply {
                        when (position) {
                            mSizeFlag -> {
                                setTextColor(color(R.color.commonColorWhite))
                                setStrokedBackground(color(R.color.colorPrimary))
                            }
                            else -> {
                                setTextColor(color(R.color.textColorPrimary))
                                setStrokedBackground(
                                    0,
                                    strokeColor = color(R.color.view_color)
                                )
                            }
                        }

                    }
                } else {
                    view.llSize.hide()
                }
            })
            sizeAdapter?.onItemClick = { pos, view, item ->
                mSizeFlag = pos
                sizeAdapter?.notifyDataSetChanged()
                getSelectedSize()
            }

            sizeAdapter?.addItems(sizeList)
            rvSize.setHorizontalLayout()
            rvSize.adapter = sizeAdapter
        } else {
            tvSize.hide()
            rvSize.hide()
        }
    }

    private fun getSelectedColors(): String? {
        return   when (mColorFlag) {
            -1 -> null
            else ->  colorAdapter?.getModel()!![mColorFlag]
        }
    }

    private fun getSelectedSize(): String? {
        return   when (mSizeFlag) {
            -1 -> null
            else ->  sizeAdapter?.getModel()!![mSizeFlag]
        }
    }

    private fun changeFavIcon(
        drawable: Int,
        backgroundColor: Int,
        iconTint: Int = R.color.textColorSecondary
    ) {
        ivFavourite.setImageResource(drawable)
        ivFavourite.applyColorFilter(color(iconTint))
        ivFavourite.changeBackgroundTint(color(backgroundColor))
    }

    private fun addItemToCart() {
        if (mProductModel?.stock_quantity != null && mProductModel?.stock_quantity!! < 1) {
            toast(R.string.msg_sold_out); return
        }
        val requestModel = RequestModel(); requestModel.pro_id =
            mProductModel?.pro_id.toString(); requestModel.quantity = mQuntity.toInt()
        requestModel.color = "";requestModel.size = ""
        if (mIsColorExist) {
            val selectedColors = getSelectedColors()
            if (selectedColors != null) {
                requestModel.color = selectedColors
            } else {
                snackBar(getString(R.string.msg_selectcolorsize))
                return
            }
        }
        if (mIsSizeExist) {
            val selectedSize = getSelectedSize()
            if (selectedSize != null) {
                requestModel.size = selectedSize
            } else {
                snackBar(getString(R.string.msg_selectcolorsize))
                return
            }
        }
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
        isAddedToCart = true
    }

    private fun removeCartItem() {
        getAlertDialog(getString(R.string.msg_confirmation), onPositiveClick = { dialog, i ->
            val requestModel = RequestModel()
            requestModel.pro_id = mProductModel?.pro_id.toString()

            showProgress(true)
            callApi(getRestApis(false).removeCartItem(request = requestModel), onApiSuccess = {
                showProgress(false)
                isAddedToCart = false
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

    private fun listProductReviews() {
        callApi(getRestApis().listProductReviews(mProductModel?.pro_id!!), onApiSuccess = {
            val mReview: ArrayList<String> = ArrayList()
            it.forEach { review ->
                if (!mReview.contains(review.email)) {
                    mReview.add(review.email)
                }
            }
            setRating(it)
        }, onApiError = {
            snackBarError(it)
        }, onNetworkError = {
            openLottieDialog() {
                listProductReviews()
            }
        })
    }

    private fun setRating(data: List<ProductReviewData>) {
        if (data.isEmpty()) {
            tvItemProductRating.rating = 0f
            return
        }
        var fiveStar = 0
        var fourStar = 0
        var threeStar = 0
        var twoStar = 0
        var oneStar = 0
        for (reviewModel in data) {
            when (reviewModel.rating) {
                5 -> fiveStar++
                4 -> fourStar++
                3 -> threeStar++
                2 -> twoStar++
                1 -> oneStar++
            }
        }
        if (fiveStar == 0 && fourStar == 0 && threeStar == 0 && twoStar == 0 && oneStar == 0) {
            return
        }
        val mAvgRating =
            (5 * fiveStar + 4 * fourStar + 3 * threeStar + 2 * twoStar + 1 * oneStar) / (fiveStar + fourStar + threeStar + twoStar + oneStar)
        tvItemProductRating.rating = mAvgRating.toFloat()
    }

    override fun onResume() {
        checkWishListAndCart()
        super.onResume()
        if (!isAdShown){
            showInterstitialAd()
        }
    }

    private fun checkWishListAndCart() {
        if (mProductModel != null) {
            isAddedToCart = isExistInCart(mProductModel!!)
            mIsInWishList = isExistInWishList(mProductModel!!)

            if (isAddedToCart) btnAddCard.text =
                getString(R.string.lbl_remove_cart) else btnAddCard.text = getString(R.string.lbl_add_to_cart)
            if (mIsInWishList) changeFavIcon(
                R.drawable.ic_heart_fill,
                R.color.favourite_background,
                R.color.colorPrimary
            ) else changeFavIcon(R.drawable.ic_heart, R.color.gray_80)
        }
    }
}
