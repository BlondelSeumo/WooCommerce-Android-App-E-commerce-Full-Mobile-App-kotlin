package com.iqonic.woobox.fragments.home

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.iqonic.woobox.R
import com.iqonic.woobox.activity.*
import com.iqonic.woobox.adapter.HomeSliderAdapter
import com.iqonic.woobox.adapter.RecyclerViewAdapter
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemCategory2Binding
import com.iqonic.woobox.fragments.BaseFragment
import com.iqonic.woobox.models.*
import com.iqonic.woobox.utils.Constants.KeyIntent.DATA
import com.iqonic.woobox.utils.Constants.KeyIntent.TITLE
import com.iqonic.woobox.utils.Constants.KeyIntent.VIEWALLID
import com.iqonic.woobox.utils.Constants.SharedPref.CATEGORY_DATA
import com.iqonic.woobox.utils.Constants.SharedPref.CONTACT
import com.iqonic.woobox.utils.Constants.SharedPref.COPYRIGHT_TEXT
import com.iqonic.woobox.utils.Constants.SharedPref.DEFAULT_CURRENCY
import com.iqonic.woobox.utils.Constants.SharedPref.FACEBOOK
import com.iqonic.woobox.utils.Constants.SharedPref.INSTAGRAM
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_ORDER_COUNT
import com.iqonic.woobox.utils.Constants.SharedPref.PRIVACY_POLICY
import com.iqonic.woobox.utils.Constants.SharedPref.SLIDER_IMAGES_DATA
import com.iqonic.woobox.utils.Constants.SharedPref.TERM_CONDITION
import com.iqonic.woobox.utils.Constants.SharedPref.THEME_COLOR
import com.iqonic.woobox.utils.Constants.SharedPref.TWITTER
import com.iqonic.woobox.utils.Constants.SharedPref.WHATSAPP
import com.iqonic.woobox.utils.Constants.ViewAllCode.FEATURED
import com.iqonic.woobox.utils.Constants.ViewAllCode.NEWEST
import com.iqonic.woobox.utils.Constants.ViewAllCode.RECENTSEARCH
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home_2.*
import kotlinx.android.synthetic.main.fragment_home_2.dots
import kotlinx.android.synthetic.main.fragment_home_2.homeSlider
import kotlinx.android.synthetic.main.fragment_home_2.ivBanner1
import kotlinx.android.synthetic.main.fragment_home_2.ivBanner2
import kotlinx.android.synthetic.main.fragment_home_2.ivBanner3
import kotlinx.android.synthetic.main.fragment_home_2.rcvCategory
import kotlinx.android.synthetic.main.fragment_home_2.rcvDealProducts
import kotlinx.android.synthetic.main.fragment_home_2.rcvFeaturedProducts
import kotlinx.android.synthetic.main.fragment_home_2.rcvNewestProduct
import kotlinx.android.synthetic.main.fragment_home_2.rcvOfferProducts
import kotlinx.android.synthetic.main.fragment_home_2.rcvRecentSearch
import kotlinx.android.synthetic.main.fragment_home_2.rcvSuggestedProducts
import kotlinx.android.synthetic.main.fragment_home_2.rcvTestimonials
import kotlinx.android.synthetic.main.fragment_home_2.rcvYouMayLikeProducts
import kotlinx.android.synthetic.main.fragment_home_2.refreshLayout
import kotlinx.android.synthetic.main.fragment_home_2.rlDeal
import kotlinx.android.synthetic.main.fragment_home_2.rlFeatured
import kotlinx.android.synthetic.main.fragment_home_2.rlNewestProduct
import kotlinx.android.synthetic.main.fragment_home_2.rlOffer
import kotlinx.android.synthetic.main.fragment_home_2.rlRecentSearch
import kotlinx.android.synthetic.main.fragment_home_2.rlSuggested
import kotlinx.android.synthetic.main.fragment_home_2.rlTestimonials
import kotlinx.android.synthetic.main.fragment_home_2.rlYouMayLike
import kotlinx.android.synthetic.main.fragment_home_2.rl_head
import kotlinx.android.synthetic.main.fragment_home_2.viewDeal
import kotlinx.android.synthetic.main.fragment_home_2.viewFeatured
import kotlinx.android.synthetic.main.fragment_home_2.viewNewest
import kotlinx.android.synthetic.main.fragment_home_2.viewOffer
import kotlinx.android.synthetic.main.fragment_home_2.viewRecentSearch
import kotlinx.android.synthetic.main.fragment_home_2.viewSuggested
import kotlinx.android.synthetic.main.fragment_home_2.viewYouMayLike
import kotlinx.android.synthetic.main.item_product_2.view.*
import kotlinx.android.synthetic.main.item_testimonial.view.*


class HomeFragment2 : BaseFragment() {

    //region Variables
    private var imgLayoutParams: LinearLayout.LayoutParams? = null
    private var mCategoryAdapter: BaseRecyclerAdapter<CategoryData, ItemCategory2Binding>? = null
    private var mFeaturedProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mNewArrivalProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mOfferProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mYouMayLikeProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mDealProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mSuggestedProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mRecentProductAdapter: RecyclerViewAdapter<ProductDataNew>? = null
    private var mTestimonialsAdapter: RecyclerViewAdapter<Testimonials>? = null

    var onNetworkRetry: (() -> Unit)? = null
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        imgLayoutParams = activity?.productLayoutParams()
        mCategoryAdapter = activity!!.getCategory2Adapter()

        rcvCategory.apply {
            layoutManager = GridLayoutManager(activity, 2, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = mCategoryAdapter
            rvItemAnimation()
        }
        rcvRecentSearch.setHorizontalLayout(); rcvNewestProduct.setHorizontalLayout(); rcvFeaturedProducts.setHorizontalLayout()
        rcvDealProducts.setHorizontalLayout(); rcvYouMayLikeProducts.setHorizontalLayout(); rcvOfferProducts.setHorizontalLayout(); rcvSuggestedProducts.setHorizontalLayout()
        rcvTestimonials.setHorizontalLayout()

        setClickEventListener()

        setupRecentProductAdapter(); setupNewArrivalProductAdapter(); setupFeaturedProductAdapter();setTestimonialAdapter()

        setupOfferProductAdapter(); setupSuggestedProductAdapter(); setupYouMayLikeProductAdapter(); setupDealProductAdapter()

        loadApis()
        refreshLayout.setOnRefreshListener {
            loadApis()
            refreshLayout.isRefreshing = false
        }
    }


    //region APIs
    private fun loadApis() {
        if (isNetworkAvailable()) {
            listAllProducts(); listAllProductCategories(); getSliders();listFeaturedProducts()
        } else {
            listAllProductCategories(); getSliders()
            activity?.openLottieDialog { loadApis(); onNetworkRetry?.invoke() }
        }
    }

    private fun getSliders() {
        val images = getSlideImagesFromPref()
        val sliderImagesAdapter = HomeSliderAdapter(activity!!, images)
        homeSlider.adapter = sliderImagesAdapter
        dots.attachViewPager(homeSlider)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        if (images.isNotEmpty()) {
            rl_head.show()
        } else {
            rl_head.hide()
        }

        callApi(getRestApis(false).getSliderImages(), onApiSuccess = { res ->
            if (activity == null) return@callApi
            getSharedPrefInstance().setValue(SLIDER_IMAGES_DATA, Gson().toJson(res))
            images.clear()
            images.addAll(getSlideImagesFromPref())
            dots.attachViewPager(homeSlider)
            dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
            sliderImagesAdapter.notifyDataSetChanged()
            if (images.isNotEmpty()) {
                rl_head.show()
            } else {
                rl_head.hide()
            }
        }, onApiError = {
            if (activity == null) return@callApi
            rl_head.hide()
        }, onNetworkError = {
            if (activity == null) return@callApi
            activity?.noInternetSnackBar()
            rl_head.hide()
        })
    }

    private fun listAllProducts() {
        showProgress()
        val requestModel = RequestModel()
        if (isLoggedIn()) requestModel.user_id = getUserId()

        callApi(getRestApis(false).dashboard(requestModel), onApiSuccess = {
            if (activity == null) return@callApi
            hideProgress()

            getSharedPrefInstance().apply {
                removeKey(WHATSAPP)
                removeKey(FACEBOOK)
                removeKey(TWITTER)
                removeKey(INSTAGRAM)
                removeKey(CONTACT)
                removeKey(PRIVACY_POLICY)
                removeKey(TERM_CONDITION)
                removeKey(COPYRIGHT_TEXT)
                setValue(DEFAULT_CURRENCY, it.currency_symbol.currency_symbol)
                setValue(KEY_ORDER_COUNT, it.total_order)
                setValue(THEME_COLOR, it.theme_color)
                setValue(WHATSAPP, it.social_link?.whatsapp)
                setValue(FACEBOOK, it.social_link?.facebook)
                setValue(TWITTER, it.social_link?.twitter)
                setValue(INSTAGRAM, it.social_link?.instagram)
                setValue(CONTACT, it.social_link?.contact)
                setValue(PRIVACY_POLICY, it.social_link?.privacy_policy)
                setValue(TERM_CONDITION, it.social_link?.term_condition)
                setValue(COPYRIGHT_TEXT, it.social_link?.copyright_text)
            }

            if (it.newest.isEmpty()) {
                rlNewestProduct.hide()
                rcvNewestProduct.hide()
            } else {
                rlNewestProduct.show()
                rcvNewestProduct.show()
                mNewArrivalProductAdapter?.addItems(it.newest)
            }
            if (it.featured.isEmpty()) {
                rlFeatured.hide()
                rcvFeaturedProducts.hide()
            } else {
                rlFeatured.show()
                rcvFeaturedProducts.show()
                mFeaturedProductAdapter?.addItems(it.featured)
            }
            if (it.testimonials.isEmpty()) {
                rlTestimonials.hide()
                rcvTestimonials.hide()
            } else {
                rlTestimonials.show()
                rcvTestimonials.show()
                mTestimonialsAdapter?.addItems(it.testimonials)
            }

            if (it.deal_product.isEmpty()) {
                rlDeal.hide()
                rcvDealProducts.hide()
            } else {
                rlDeal.show()
                rcvDealProducts.show()
                mDealProductAdapter?.addItems(it.deal_product)
            }
            if (it.you_may_like.isEmpty()) {
                rlYouMayLike.hide()
                rcvYouMayLikeProducts.hide()
            } else {
                rlYouMayLike.show()
                rcvYouMayLikeProducts.show()
                mYouMayLikeProductAdapter?.addItems(it.newest)
            }
            if (it.offer.isEmpty()) {
                rlOffer.hide()
                rcvOfferProducts.hide()
            } else {
                rlOffer.show()
                rcvOfferProducts.show()
                mOfferProductAdapter?.addItems(it.offer)
            }
            if (it.suggested_product.isEmpty()) {
                rlSuggested.hide()
                rcvSuggestedProducts.hide()
            } else {
                rlSuggested.show()
                rcvSuggestedProducts.show()
                mSuggestedProductAdapter?.addItems(it.suggested_product)
            }

            if (it.banner_1 != null && it.banner_1.url.isNotEmpty()) {
                ivBanner1.show(); ivBanner1.loadImageFromUrl(it.banner_1.image); ivBanner1.onClick {
                    activity?.openCustomTab(
                        it.banner_1.url
                    )
                }
            } else {
                ivBanner1.hide()
            }
            if (it.banner_2 != null && it.banner_2.url.isNotEmpty()) {
                ivBanner2.show(); ivBanner2.loadImageFromUrl(it.banner_2.image); ivBanner2.onClick {
                    activity?.openCustomTab(
                        it.banner_2.url
                    )
                }
            } else {
                ivBanner2.hide()
            }
            if (it.banner_3 != null && it.banner_3.url.isNotEmpty()) {
                ivBanner3.show(); ivBanner3.loadImageFromUrl(it.banner_3.image); ivBanner3.onClick {
                    activity?.openCustomTab(
                        it.banner_3.url
                    )
                }
            } else {
                ivBanner3.hide()
            }
        }, onApiError = {
            //toast(it)
        }, onNetworkError = {
            toast(R.string.error_no_internet)
        })
    }

    private fun listFeaturedProducts() {
        showProgress()
        val requestModel = FilterProductRequest()
        callApi(getRestApis(false).getFeaturedProducts(requestModel), onApiSuccess = {
            if (activity == null) return@callApi
            hideProgress()
            if (it.isEmpty()) {
                rlFeatured.hide()
                rcvFeaturedProducts.hide()
            } else {
                rlFeatured.show()
                rcvFeaturedProducts.show()
                mFeaturedProductAdapter?.addItems(it)
                mFeaturedProductAdapter?.setModelSize(5)
            }
        }, onApiError = {
            if (activity == null) return@callApi
            hideProgress()
        }, onNetworkError = {
            if (activity == null) return@callApi
            hideProgress()
            activity?.noInternetSnackBar()
        })
    }

    private fun listAllProductCategories() {
        val categories = getCategoryDataFromPref()
        if (categories.isNotEmpty()) {
            rlCategory.show()
            mCategoryAdapter?.addItems(categories)
            if (activity != null) (activity as DashBoardActivity).setDrawerCategory(categories)
        } else {
            rlCategory.hide()
        }

        callApi(getRestApis(false).getProductCategories(), onApiSuccess = {
            if (activity == null) return@callApi
            getSharedPrefInstance().setValue(CATEGORY_DATA, Gson().toJson(it))
            mCategoryAdapter?.addItems(it)
            if (it.isNotEmpty()) {
                rlCategory.show()
            } else {
                rlCategory.hide()
            }

            if (activity != null) (activity as DashBoardActivity).setDrawerCategory(it)
        }, onApiError = {
            if (activity == null) return@callApi
        }, onNetworkError = {
            if (activity == null) return@callApi
            activity?.noInternetSnackBar()
        })
    }
    //endregion

    //region RecyclerViews and Adapters
    private fun setupRecentProductAdapter() {
        mRecentProductAdapter =
            RecyclerViewAdapter(R.layout.item_recent_product_small, onBind = { view, item, _ ->
                if (item.full != null) view.ivProduct.loadImageFromUrl(item.full)
            })
        rcvRecentSearch.adapter = mRecentProductAdapter

        mRecentProductAdapter?.onItemClick = { pos, view, item ->
            activity?.launchActivity<ProductDetailActivityNew> { putExtra(DATA, item) }
        }

        mRecentProductAdapter?.addItems(getRecentItems())
        mRecentProductAdapter?.setModelSize(5)

        if (mRecentProductAdapter != null && mRecentProductAdapter!!.itemCount <= 0) rlRecentSearch.hide() else rlRecentSearch.show()
    }

    private fun setupNewArrivalProductAdapter() {
        mNewArrivalProductAdapter = RecyclerViewAdapter(
            R.layout.item_product_2,
            onBind = { view, item, _ -> setProductItem2(view, item) })
        rcvNewestProduct.adapter = mNewArrivalProductAdapter

        mNewArrivalProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
            rlRecentSearch.show()
        }
    }

    private fun setupFeaturedProductAdapter() {
        mFeaturedProductAdapter = RecyclerViewAdapter(
            R.layout.item_product_2,
            onBind = { view, item, _ -> setProductItem2(view, item) })
        rcvFeaturedProducts.adapter = mFeaturedProductAdapter

        mFeaturedProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
            rlRecentSearch.show()
        }
    }

    private fun setupOfferProductAdapter() {
        mOfferProductAdapter = RecyclerViewAdapter(
            R.layout.item_product_2,
            onBind = { view, item, _ -> setProductItem2(view, item) })
        rcvOfferProducts.adapter = mOfferProductAdapter

        mOfferProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
            rlRecentSearch.show()
        }
    }

    private fun setupSuggestedProductAdapter() {
        mSuggestedProductAdapter = RecyclerViewAdapter(
            R.layout.item_product_2,
            onBind = { view, item, _ -> setProductItem2(view, item) })
        rcvSuggestedProducts.adapter = mSuggestedProductAdapter

        mSuggestedProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
            rlRecentSearch.show()
        }
    }

    private fun setupYouMayLikeProductAdapter() {
        mYouMayLikeProductAdapter = RecyclerViewAdapter(
            R.layout.item_product_2,
            onBind = { view, item, _ -> setProductItem2(view, item) })
        rcvYouMayLikeProducts.adapter = mYouMayLikeProductAdapter

        mYouMayLikeProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
            rlRecentSearch.show()
        }
    }

    private fun setupDealProductAdapter() {
        mDealProductAdapter = RecyclerViewAdapter(
            R.layout.item_product_2,
            onBind = { view, item, _ -> setProductItem2(view, item) })
        rcvDealProducts.adapter = mDealProductAdapter

        mDealProductAdapter?.onItemClick = { pos, view, item ->
            activity?.showProductDetail(item)
            mRecentProductAdapter?.addItems(getRecentItems())
            rlRecentSearch.show()
        }
    }

    private fun setTestimonialAdapter() {
        mTestimonialsAdapter =
            RecyclerViewAdapter(R.layout.item_testimonial, onBind = { view, item, _ ->
                view.ivAuthor.loadImageFromUrl(item.image!!)
                view.tvName.text = item.name
                view.tvDesignation.text = ""
                view.tvDesignation.text = item.designation
                if (item.company != null && item.company.isNotEmpty()) {
                    view.tvDesignation.append(", " + item.company)
                }
                view.tvDescription.text = "\"" + item.message + "\""
            })
        rcvTestimonials.adapter = mTestimonialsAdapter
    }

    //endregion

    //region Common
    private fun getRecentItems(): ArrayList<ProductDataNew> {
        val list = recentProduct(); list.reverse(); return list
    }

    private fun setClickEventListener() {
        viewRecentSearch.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(TITLE, getString(R.string.lbl_recent_search))
                putExtra(VIEWALLID, RECENTSEARCH)
            }
        }

        viewFeatured.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(TITLE, getString(R.string.lbl_Featured))
                putExtra(VIEWALLID, FEATURED)
            }
        }

        viewNewest.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(TITLE, getString(R.string.lbl_newest_product))
                putExtra(VIEWALLID, NEWEST)
            }
        }

        viewOffer.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(TITLE, getString(R.string.lbl_offers))
                putExtra(VIEWALLID, NEWEST)
            }
        }

        viewDeal.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(TITLE, getString(R.string.lbl_deal))
                putExtra(VIEWALLID, NEWEST)
            }
        }

        viewYouMayLike.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(TITLE, getString(R.string.lbl_you_may_like))
                putExtra(VIEWALLID, NEWEST)
            }
        }

        viewSuggested.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(TITLE, getString(R.string.lbl_suggested))
                putExtra(VIEWALLID, NEWEST)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                activity?.launchActivity<SearchActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion
    private fun setProductItem2(view: View, item: ProductDataNew) {
        view.tvProductName.text = item.name
        if (item.sale_price!!.isNotEmpty()) {
            view.tvDiscountPrice.text = item.sale_price.currencyFormat()
        } else {
            view.tvDiscountPrice.text = item.price?.currencyFormat()
        }
        view.tvOriginalPrice.text = item.regular_price?.currencyFormat()
        view.tvOriginalPrice.applyStrike()
        if (item.full != null) view.ivProduct.loadImageFromUrl(item.full)
    }
}