package com.iqonic.woobox.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.activity.MyCartActivity
import com.iqonic.woobox.activity.SearchActivity
import com.iqonic.woobox.activity.SignInUpActivity
import com.iqonic.woobox.adapter.RecyclerViewAdapter
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemNewestProductBinding
import com.iqonic.woobox.databinding.ItemViewproductgridBinding
import com.iqonic.woobox.models.FilterProductRequest
import com.iqonic.woobox.models.ProductDataNew
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.models.RequestModel.*
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.Constants.ViewAllCode.CATEGORY_FEATURED
import com.iqonic.woobox.utils.Constants.ViewAllCode.CATEGORY_NEWEST
import com.iqonic.woobox.utils.Constants.ViewAllCode.FEATURED
import com.iqonic.woobox.utils.Constants.ViewAllCode.NEWEST
import com.iqonic.woobox.utils.Constants.ViewAllCode.RECENTSEARCH
import com.iqonic.woobox.utils.HidingScrollListener
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_sort.*
import kotlinx.android.synthetic.main.fragment_newest_product.*
import kotlinx.android.synthetic.main.item_color.view.*
import kotlinx.android.synthetic.main.item_filter_brand.view.*
import kotlinx.android.synthetic.main.item_filter_category.view.*
import kotlinx.android.synthetic.main.item_size.view.*
import kotlinx.android.synthetic.main.layout_colors.*
import kotlinx.android.synthetic.main.layout_filter.*
import kotlinx.android.synthetic.main.layout_nodata.*
import kotlinx.android.synthetic.main.layout_size.*
import kotlinx.android.synthetic.main.menu_cart.view.*

class ViewAllProductFragment : BaseFragment() {

    private var showPagination: Boolean? = true
    //region Variables
    private val mListAdapter = getAdapter()
    private val mGridAdapter = getGridAdapter()
    private var menuCart: View? = null
    private var mId: Int = 0
    private var mCategoryId: Int = -1

    private var mColor = ArrayList<FilterColors>()
    private var mBrand = ArrayList<FilterBrands>()
    private var mSize = ArrayList<FilterSizes>()
    private var mCategory = ArrayList<FilterCategories>()

    private var mSelectedColor: ArrayList<String> = ArrayList()
    private var mSelectedBrand: ArrayList<String> = ArrayList()
    private var mSelectedSize: ArrayList<String> = ArrayList()
    private var mSelectedCategory: ArrayList<Int> = ArrayList()
    private var mSelectedPrice: ArrayList<Int> = ArrayList()

    private var mIsFilterDataLoaded = false
    private lateinit var mProductAttributeResponseMsg: String

    private var mIsLoading = false
    private var countLoadMore = 1
    //endregion

    companion object {
        fun getNewInstance(
            id: Int,
            mCategoryId: Int,
            showPagination: Boolean = true
        ): ViewAllProductFragment {
            val fragment = ViewAllProductFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.KeyIntent.VIEWALLID, id)
            bundle.putSerializable(Constants.KeyIntent.KEYID, mCategoryId)
            bundle.putSerializable(Constants.KeyIntent.SHOW_PAGINATION, showPagination)

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_newest_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mId = arguments?.getInt(Constants.KeyIntent.VIEWALLID)!!
        mCategoryId = arguments?.getInt(Constants.KeyIntent.KEYID)!!
        showPagination = arguments?.getBoolean(Constants.KeyIntent.SHOW_PAGINATION)
        mProductAttributeResponseMsg = getString(R.string.lbl_please_wait)
        setClickEventListener()
        rvNewestProduct.apply {
            adapter = mGridAdapter
            rvItemAnimation()
            if (showPagination!!) {
                setOnScrollListener(object : HidingScrollListener(activity) {
                    override fun onMoved(distance: Int) {
                        rlTop.translationY = -distance.toFloat()
                    }

                    override fun onShow() {
                        rlTop.animate().translationY(0f).setInterpolator(DecelerateInterpolator(2f))
                            .start()
                    }

                    override fun onHide() {
                        rlTop.animate().translationY((-rlTop.height).toFloat())
                            .setInterpolator(AccelerateInterpolator(2f)).start()
                    }
                })
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val countItem = recyclerView.layoutManager?.itemCount

                        var lastVisiblePosition = 0
                        if (recyclerView.layoutManager is LinearLayoutManager) {
                            lastVisiblePosition =
                                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        } else if (recyclerView.layoutManager is GridLayoutManager) {
                            lastVisiblePosition =
                                (recyclerView.layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
                        }

                        if (lastVisiblePosition != 0 && !mIsLoading && countItem?.minus(1) == lastVisiblePosition) {
                            mIsLoading = true

                            countLoadMore = countLoadMore.plus(1)
                            when (mId) {
                                FEATURED -> {
                                    listFeaturedProducts(countLoadMore)
                                }
                                NEWEST -> {
                                    listAllProducts(mId, countLoadMore)
                                }
                            }
                        }
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                    }
                })

            } else {
                rlTop.hide()
            }
        }

        when (mId) {
            RECENTSEARCH -> {
                mListAdapter.addItems(getRecentItems())
                mGridAdapter.addItems(getRecentItems())
            }
            CATEGORY_FEATURED -> {
                getSubCategoryProducts()
            }
            CATEGORY_NEWEST -> {
                getSubCategoryProducts()
            }
            FEATURED -> {
                listFeaturedProducts()
            }
            NEWEST -> {
                listAllProducts(mId)
            }
            else -> {
                listAllProducts(mId)
            }
        }

        getProductAttributes()
        ivGrid.performClick()
    }

    private fun getProductAttributes() {
        mProductAttributeResponseMsg = getString(R.string.lbl_please_wait)
        callApi(getRestApis(false).getProductAttributes(), onApiSuccess = {
            mIsFilterDataLoaded = true

            it.colors.forEachIndexed { index, color ->
                mColor.add(
                    FilterColors(
                        color.term_id,
                        color.name,
                        color.slug,
                        false
                    )
                )
            }
            it.brands.forEachIndexed { index, brand ->
                mBrand.add(
                    FilterBrands(
                        brand.term_id,
                        brand.name,
                        false
                    )
                )
            }
            it.sizes.forEachIndexed { index, size ->
                mSize.add(
                    FilterSizes(
                        size.term_id,
                        size.name,
                        false
                    )
                )
            }
            it.categories.forEachIndexed { index, category ->
                mCategory.add(
                    FilterCategories(
                        category.term_id,
                        category.cat_ID,
                        category.cat_name,
                        category.slug,
                        false
                    )
                )
            }
        }, onApiError = {
            activity?.snackBarError(it)
            mIsFilterDataLoaded = false
            mProductAttributeResponseMsg = getString(R.string.lbl_try_later)
        }, onNetworkError = {
            mIsFilterDataLoaded = false
            mProductAttributeResponseMsg = getString(R.string.lbl_try_later)
            activity?.noInternetSnackBar()
        })
    }

    private fun openFilterBottomSheet() {
        if (activity != null) {
            val filterDialog =
                BottomSheetDialog(activity!!); filterDialog.setContentView(R.layout.layout_filter)

            val priceArray = arrayOf(
                "0".currencyFormat(),
                "100".currencyFormat(),
                "200".currencyFormat(),
                "300".currencyFormat(),
                "400".currencyFormat(),
                "500".currencyFormat(),
                "600".currencyFormat(),
                "700".currencyFormat(),
                "800".currencyFormat(),
                "900".currencyFormat(),
                "1000".currencyFormat()
            )
            val priceArray2 =
                arrayOf("0", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000")

            filterDialog.rangebar1.tickTopLabels = priceArray

            if (mSelectedPrice.size == 2) {
                filterDialog.rangebar1.setRangePinsByValue(
                    priceArray2.indexOf(mSelectedPrice[0].toString()).toFloat(),
                    priceArray2.indexOf(mSelectedPrice[1].toString()).toFloat()
                )
            }

            //region Adapters
            val categoryAdapter = RecyclerViewAdapter<FilterCategories>(
                R.layout.item_filter_category,
                onBind = { view, item, position ->
                    view.tvSubCategory.text = item.cat_name?.getHtmlString()

                    if (item.isSelected != null && item.isSelected!!) {
                        view.tvSubCategory.setTextColor(activity?.color(R.color.white)!!)
                        view.tvSubCategory.setStrokedBackground(activity?.color(R.color.colorPrimary)!!)
                    } else {
                        view.tvSubCategory.setTextColor(activity?.color(R.color.colorPrimary)!!)
                        view.tvSubCategory.setStrokedBackground(
                            activity?.color(R.color.white)!!,
                            activity?.color(R.color.colorPrimary)!!
                        )
                    }
                })
            categoryAdapter.onItemClick = { pos, _, item ->
                item.isSelected = !(item.isSelected!!)
                categoryAdapter.notifyItemChanged(pos)
            }

            val brandAdapter = RecyclerViewAdapter<FilterBrands>(
                R.layout.item_filter_brand,
                onBind = { view, item, position ->
                    view.tvBrandName.text = item.name
                    if (item.isSelected != null && item.isSelected!!) {
                        view.tvBrandName.setTextColor(activity!!.color(R.color.colorPrimary))
                        view.ivSelect.setImageResource(R.drawable.ic_check)
                        view.ivSelect.setColorFilter(activity!!.color(R.color.colorAccent))
                        view.ivSelect.setStrokedBackground(
                            activity!!.color(R.color.colorAccent),
                            activity!!.color(R.color.colorAccent),
                            0.4f
                        )
                    } else {
                        view.tvBrandName.setTextColor(activity!!.color(R.color.textColorSecondary))
                        view.ivSelect.setImageResource(0)
                        view.ivSelect.setStrokedBackground(activity!!.color(R.color.checkbox_color))
                    }
                })
            brandAdapter.onItemClick = { pos, _, item ->
                item.isSelected = !(item.isSelected!!)
                brandAdapter.notifyItemChanged(pos)
            }
            val sizeAdapter = RecyclerViewAdapter<FilterSizes>(
                R.layout.item_size,
                onBind = { view, item, position ->
                    view.ivSizeChecked.text = item.name
                    view.ivSizeChecked.apply {
                        when {
                            item.isSelected!! -> {
                                setTextColor(activity!!.color(R.color.commonColorWhite))
                                setStrokedBackground(activity!!.color(R.color.colorPrimary))
                            }
                            else -> {
                                setTextColor(activity!!.color(R.color.textColorPrimary))
                                setStrokedBackground(
                                    0,
                                    strokeColor = (activity!!.color(R.color.view_color))
                                )
                            }
                        }

                    }

                })
            sizeAdapter.onItemClick = { pos, _, item ->
                item.isSelected = !(item.isSelected!!)
                sizeAdapter.notifyItemChanged(pos)
            }

            val colorAdapter = RecyclerViewAdapter<FilterColors>(
                R.layout.item_color,
                onBind = { view, item, position ->

                    try {
                        if (item.name!!.contains("#")) {
                            view.viewColor.show()
                            view.tvColor.hide()
                            view.viewColor.setStrokedBackground(
                                Color.parseColor(item.name!!),
                                strokeColor = activity!!.color(R.color.white)
                            )
                            view.ivColorChecked.setStrokedBackground(
                                Color.parseColor(item.name!!),
                                strokeColor = activity!!.color(R.color.white)
                            )
                            when {
                                item.isSelected!! -> {
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
                            view.tvColor.text = item.name
                            view.tvColor.apply {
                                when {
                                    item.isSelected!! -> {
                                        setTextColor(activity!!.color(R.color.commonColorWhite))
                                        setStrokedBackground(activity!!.color(R.color.colorPrimary))
                                    }
                                    else -> {
                                        setTextColor(activity!!.color(R.color.textColorPrimary))
                                        setStrokedBackground(
                                            0,
                                            strokeColor = activity!!.color(R.color.view_color)
                                        )
                                    }
                                }

                            }

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            colorAdapter.onItemClick = { pos, _, item ->
                item.isSelected = !(item.isSelected!!)
                colorAdapter.notifyItemChanged(pos)
            }
            //endregion

            //region RecyclerViews
            filterDialog.rcvSubCategories.apply {
                layoutManager = ChipsLayoutManager.newBuilder(activity)
                    .setOrientation(ChipsLayoutManager.HORIZONTAL)
                    .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT).withLastRow(false).build()
                itemAnimator = null; adapter = categoryAdapter
            }
            categoryAdapter.addItems(mCategory); categoryAdapter.setModelSize(5)
            filterDialog.rcvBrands.apply {
                setVerticalLayout(); adapter = brandAdapter
            }; brandAdapter.addItems(mBrand)
            filterDialog.rcvSize.apply {
                setHorizontalLayout(); adapter = sizeAdapter
            }; sizeAdapter.addItems(mSize)
            filterDialog.rcvColors.apply {
                setHorizontalLayout(); adapter = colorAdapter
            }; colorAdapter.addItems(mColor)
            //endregion

            //region Clicks
            filterDialog.tvApply.onClick {
                mSelectedBrand.clear(); mSelectedCategory.clear(); mSelectedSize.clear(); mSelectedColor.clear(); mSelectedPrice.clear()

                mBrand.forEach { filterBrands ->
                    if (filterBrands.isSelected!!) mSelectedBrand.add(
                        filterBrands.name!!
                    )
                }
                mCategory.forEach { filterCategories ->
                    if (filterCategories.isSelected!!) mSelectedCategory.add(
                        filterCategories.cat_ID!!
                    )
                }
                mSize.forEach { filterSizes ->
                    if (filterSizes.isSelected!!) mSelectedSize.add(
                        filterSizes.name!!
                    )
                }
                mColor.forEach { filterColors ->
                    if (filterColors.isSelected!!) mSelectedColor.add(
                        filterColors.slug!!
                    )
                }

                mSelectedPrice.add(priceArray2[filterDialog.rangebar1.leftPinValue.toInt()].toInt())
                mSelectedPrice.add(priceArray2[filterDialog.rangebar1.rightPinValue.toInt()].toInt())

                val mFilterProductRequest = FilterProductRequest()

                if (mSelectedColor.isNotEmpty()) mFilterProductRequest.color = mSelectedColor
                if (mSelectedBrand.isNotEmpty()) mFilterProductRequest.brand = mSelectedBrand
                if (mSelectedSize.isNotEmpty()) mFilterProductRequest.size = mSelectedSize
                if (mSelectedCategory.isNotEmpty()) mFilterProductRequest.category =
                    mSelectedCategory
                if (mSelectedPrice.isNotEmpty()) mFilterProductRequest.price = mSelectedPrice

                filterProduct(mFilterProductRequest)
                filterDialog.dismiss()
            }
            filterDialog.tvReset.onClick {
                mColor.forEach { it.isSelected = false }
                mBrand.forEach { it.isSelected = false }
                mSize.forEach { it.isSelected = false }
                mCategory.forEach { it.isSelected = false }
                mSelectedPrice.clear()
                filterProduct(FilterProductRequest())
                filterDialog.dismiss()
            }
            filterDialog.tvSelectAll.onClick {
                mBrand.forEach { it.isSelected = true }
                brandAdapter.notifyDataSetChanged()
            }

            filterDialog.tvShowMore.onClick {
                if (categoryAdapter.size == 5) {
                    /*expand*/ categoryAdapter.setModelSize(mCategory.size); filterDialog.tvShowMore.text =
                        context.getString(R.string.lbl_less)
                } else {
                    /*collapse*/ categoryAdapter.setModelSize(5); filterDialog.tvShowMore.text =
                        context.getString(R.string.lbl_more)
                }
            }
            filterDialog.ivClose.onClick { filterDialog.dismiss() }
            //endregion

            filterDialog.show()
        }
    }

    private fun filterProduct(aFilterProductRequest: FilterProductRequest) {
        progressBar.show(); rlNoData.hide(); progressBar.animate()

        if (mId == FEATURED || mId == CATEGORY_FEATURED) {
            callApi(getRestApis(false).getFeaturedProducts(aFilterProductRequest), onApiSuccess = {
                if (activity == null) return@callApi
                setProducts(it)
            }, onApiError = {
                if (activity == null) return@callApi
                noProductAvailable(it)
            }, onNetworkError = {
                if (activity == null) return@callApi
                progressBar.hide()
                activity?.noInternetSnackBar()
            })
        } else {
            callApi(getRestApis().filterProduct(request = aFilterProductRequest), onApiSuccess = {
                if (activity == null) return@callApi
                setProducts(it)
            }, onApiError = {
                if (activity == null) return@callApi
                noProductAvailable(it)
            }, onNetworkError = {
                if (activity == null) return@callApi
                progressBar.hide()
                activity?.noInternetSnackBar()
            })
        }
    }

    private fun setProducts(it: ArrayList<ProductDataNew>) {
        progressBar.hide()
        progressBar.animate()

        mListAdapter.addItems(it)
        mGridAdapter.addItems(it)

        if (mListAdapter.getModel().isEmpty() && mGridAdapter.getModel().isEmpty()) {
            rlNoData.show(); rvNewestProduct.hide()
        } else {
            rlNoData.hide(); rvNewestProduct.show()
        }
    }

    private fun noProductAvailable(it: String) {
        progressBar.hide()
        if (it == "Sorry! No Product Available") {
            rlNoData.show()
            rvNewestProduct.hide()
            mListAdapter.clearData()
            mGridAdapter.clearData()
            when (mId) {
                FEATURED -> {
                    mListAdapter.notifyDataSetChanged()
                    mGridAdapter.notifyDataSetChanged()
                }
                NEWEST -> {
                    mListAdapter.notifyDataSetChanged()
                    mGridAdapter.notifyDataSetChanged()
                }
            }
        } else {
            activity?.snackBarError(it)
        }
    }

    fun setCartCount() {
        val count = getCartCount(); menuCart?.tvNotificationCount?.text = count
        if (count.checkIsEmpty()) menuCart?.tvNotificationCount?.hide() else menuCart?.tvNotificationCount?.show()
    }

    private fun getSubCategoryProducts() {
        mSelectedCategory.add(mCategoryId)

        val mFilterProductRequest = FilterProductRequest()
        if (mSelectedCategory.isNotEmpty()) mFilterProductRequest.category = mSelectedCategory

        filterProduct(mFilterProductRequest)
    }

    private fun getAdapter(): BaseRecyclerAdapter<ProductDataNew, ItemNewestProductBinding> {

        return object : BaseRecyclerAdapter<ProductDataNew, ItemNewestProductBinding>() {

            override fun onItemClick(
                view: View,
                model: ProductDataNew,
                position: Int,
                dataBinding: ItemNewestProductBinding
            ) {
                when (view.id) {
                    R.id.ivDislike -> {
                        if (isLoggedIn()) {
                            dataBinding.ivDislike.hide()
                            dataBinding.ivlike.show()

                            val requestModel = RequestModel(); requestModel.pro_id =
                                model.pro_id.toString()
                            activity?.addToWishList(requestModel) {
                                if (activity == null) return@addToWishList
                                if (it) {
                                    dataBinding.ivDislike.hide(); dataBinding.ivlike.show()
                                } else {
                                    dataBinding.ivDislike.show(); dataBinding.ivlike.hide()
                                }
                            }
                        } else {
                            activity?.launchActivity<SignInUpActivity>()
                        }
                    }
                    R.id.ivlike -> {
                        dataBinding.ivDislike.show()
                        dataBinding.ivlike.hide()

                        val requestModel = RequestModel(); requestModel.pro_id =
                            model.pro_id.toString()
                        activity?.removeFromWishList(requestModel) {
                            if (activity == null) return@removeFromWishList
                            if (it) {
                                dataBinding.ivDislike.show(); dataBinding.ivlike.hide()
                            } else {
                                dataBinding.ivDislike.hide(); dataBinding.ivlike.show()
                            }
                        }
                    }
                    R.id.listProductRaw -> {
                        (activity as AppBaseActivity).showProductDetail(model)
                    }
                }
            }

            override val layoutResId: Int = R.layout.item_newest_product

            override fun onBindData(
                model: ProductDataNew,
                position: Int,
                dataBinding: ItemNewestProductBinding
            ) {
                if (model.full != null) dataBinding.ivProduct.loadImageFromUrl(model.full) else dataBinding.ivProduct.setImageResource(
                    0
                )
                if (model.sale_price!!.isNotEmpty()) {
                    dataBinding.tvProductPrice.text = model.sale_price.currencyFormat()
                } else {
                    dataBinding.tvProductPrice.text = model.price.toString().currencyFormat()
                }

                dataBinding.tvProductActualPrice.text = model.regular_price?.currencyFormat()
                dataBinding.tvProductActualPrice.applyStrike()

                if (!isExistInWishList(model)) {
                    dataBinding.ivDislike.show()
                    dataBinding.ivlike.hide()
                } else {
                    dataBinding.ivDislike.hide()
                    dataBinding.ivlike.show()
                }
            }

            override fun onItemLongClick(view: View, model: ProductDataNew, position: Int) {}
        }
    }

    private fun getGridAdapter(): BaseRecyclerAdapter<ProductDataNew, ItemViewproductgridBinding> {

        return object : BaseRecyclerAdapter<ProductDataNew, ItemViewproductgridBinding>() {

            override val layoutResId: Int = R.layout.item_viewproductgrid

            override fun onBindData(
                model: ProductDataNew,
                position: Int,
                dataBinding: ItemViewproductgridBinding
            ) {

                if (model.sale_price!!.isNotEmpty())
                    dataBinding.tvDiscountPrice.text = model.sale_price.currencyFormat()
                else
                    dataBinding.tvDiscountPrice.text = model.price.toString().currencyFormat()

                dataBinding.tvOriginalPrice.text = model.regular_price?.currencyFormat()
                dataBinding.tvOriginalPrice.applyStrike()
                dataBinding.ratingBar.rating = model.average_rating!!.toFloat()
                if (model.full != null) dataBinding.ivProduct.loadImageFromUrl(model.full) else dataBinding.ivProduct.setImageResource(
                    0
                )

                if (!isExistInWishList(model)) {
                    dislikeProductImage(dataBinding.ivFavourite)
                } else {
                    likeProductImage(dataBinding.ivFavourite)
                }
            }

            override fun onItemClick(
                view: View,
                model: ProductDataNew,
                position: Int,
                dataBinding: ItemViewproductgridBinding
            ) {
                when (view.id) {
                    R.id.ivFavourite -> {
                        if (isExistInWishList(model)) {
                            dislikeProductImage(dataBinding.ivFavourite)

                            val requestModel = RequestModel(); requestModel.pro_id =
                                model.pro_id.toString()
                            activity?.removeFromWishList(requestModel) {
                                if (activity == null) return@removeFromWishList
                                if (it) {
                                    hideProgress()
                                    dislikeProductImage(dataBinding.ivFavourite)
                                } else {
                                    hideProgress()
                                    likeProductImage(dataBinding.ivFavourite)
                                }
                            }
                        } else {
                            if (isLoggedIn()) {
                                likeProductImage(dataBinding.ivFavourite)

                                val requestModel = RequestModel()
                                requestModel.pro_id = model.pro_id.toString()
                                activity?.addToWishList(requestModel) {
                                    if (activity == null) return@addToWishList
                                    if (it) {
                                        hideProgress()
                                        likeProductImage(dataBinding.ivFavourite)
                                    } else {
                                        hideProgress()
                                        dislikeProductImage(dataBinding.ivFavourite)
                                    }
                                }
                            } else {
                                activity?.launchActivity<SignInUpActivity>()
                            }
                        }
                    }
                    R.id.gridProduct -> (activity as AppBaseActivity).showProductDetail(model)
                }
            }

            override fun onItemLongClick(view: View, model: ProductDataNew, position: Int) {}
        }
    }

    private fun dislikeProductImage(ivFavourite: ImageView) {
        ivFavourite.setImageResource(R.drawable.ic_heart)
        ivFavourite.applyColorFilter(activity!!.color(R.color.textColorSecondary))
        ivFavourite.setStrokedBackground(activity!!.color(R.color.gray_80))
    }

    private fun likeProductImage(ivFavourite: ImageView) {
        ivFavourite.setImageResource(R.drawable.ic_heart_fill)
        ivFavourite.applyColorFilter(activity!!.color(R.color.colorPrimary))
        ivFavourite.setStrokedBackground(activity!!.color(R.color.favourite_background))
    }

    private fun setClickEventListener() {
        ivGrid.onClick {
            setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
            ivList.setColorFilter(activity!!.color(R.color.textColorSecondary))
            rvNewestProduct.apply {
                layoutManager = GridLayoutManager(activity, 2)
                setHasFixedSize(true)
                rvNewestProduct.adapter = mGridAdapter
                rvNewestProduct.rvItemAnimation()
            }

        }
        ivList.onClick {
            setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
            ivGrid.setColorFilter(ContextCompat.getColor(context, R.color.textColorSecondary))
            rvNewestProduct.apply {
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)
                rvNewestProduct.adapter = mListAdapter
                rvNewestProduct.rvItemAnimation()
            }
        }
        ivSort.onClick {
            val sortDialog =
                BottomSheetDialog(activity!!); sortDialog.setContentView(R.layout.dialog_sort)
            sortDialog.txtHighPrice.onClick {
                mListAdapter.getModel().sortByDescending { it.price }
                mListAdapter.notifyDataSetChanged()
                mGridAdapter.getModel().sortByDescending { it.price }
                mGridAdapter.notifyDataSetChanged()
                sortDialog.dismiss()
            }
            sortDialog.txtLowPrice.onClick {
                mListAdapter.getModel().sortBy { it.price }
                mListAdapter.notifyDataSetChanged()
                mGridAdapter.getModel().sortBy { it.price }
                mGridAdapter.notifyDataSetChanged()
                sortDialog.dismiss()
            }
            sortDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
        val menuItem = menu.findItem(R.id.action_filter)
        val menuWishItem = menu.findItem(R.id.action_cart)
        if (mId != RECENTSEARCH) {
            menuItem.isVisible = true
        }
        menuWishItem.isVisible = true
        menuCart = menuWishItem.actionView
        menuCart?.ivCart?.setColorFilter(activity!!.color(R.color.textColorPrimary))
        menuWishItem.actionView.onClick {
            activity?.launchActivity<MyCartActivity> { }
        }
        setCartCount()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                activity?.launchActivity<SearchActivity>()
                true
            }
            R.id.action_filter -> {
                if (mIsFilterDataLoaded) {
                    openFilterBottomSheet()
                } else {
                    toast(mProductAttributeResponseMsg)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun listFeaturedProducts(countLoadMore: Int = 1) {
        showProgress()
        val requestModel = FilterProductRequest(); requestModel.page = countLoadMore
        callApi(getRestApis(false).getFeaturedProducts(requestModel), onApiSuccess = {
            if (activity == null) return@callApi
            hideProgress()
            if (it.isEmpty()) {
                rvNewestProduct.hide()
            } else {
                mIsLoading = it.size != 10
                if (mListAdapter.itemCount == 0 && mGridAdapter.itemCount == 0) {
                    ivGrid.performClick()
                }
                mListAdapter.addMoreItems(it)
                mGridAdapter.addMoreItems(it)
                if (mListAdapter.getModel().isEmpty() && mGridAdapter.getModel().isEmpty()) {
                    rlNoData.show()
                    rvNewestProduct.hide()
                } else {
                    rlNoData.hide()
                    rvNewestProduct.show()
                }
            }
        }, onApiError = {
            if (activity == null) return@callApi
            hideProgress()
            snackBar(it)
        }, onNetworkError = {
            if (activity == null) return@callApi
            hideProgress()
            activity?.noInternetSnackBar()
        })
    }

    private fun listAllProducts(mId: Int, countLoadMore: Int = 1) {
        progressBar.show()
        val requestModel = RequestModel(); requestModel.page = countLoadMore
        callApi(getRestApis().listAllProducts(requestModel), onApiSuccess = {
            if (activity == null) return@callApi
            progressBar.hide()
            when (mId) {
                NEWEST -> {
                    mIsLoading = it.size != 10
                    if (mListAdapter.itemCount == 0 && mGridAdapter.itemCount == 0) {
                        ivGrid.performClick()
                    }
                    mListAdapter.addMoreItems(it)
                    mGridAdapter.addMoreItems(it)
                    if (it.isNotEmpty()) {
                        rvNewestProduct.show(); rlNoData.hide()
                    } else {
                        rlNoData.show(); rvNewestProduct.hide()
                    }
                }
            }
        }, onApiError = {
            if (activity == null) return@callApi
            progressBar.hide()
        }, onNetworkError = {
            if (activity == null) return@callApi
            progressBar.hide()
        })
    }

    private fun getRecentItems(): ArrayList<ProductDataNew> {
        val list = recentProduct()
        list.reverse()
        return list
    }

}