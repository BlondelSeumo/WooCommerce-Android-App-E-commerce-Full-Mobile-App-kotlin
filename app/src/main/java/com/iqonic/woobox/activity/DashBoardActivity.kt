package com.iqonic.woobox.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.WooBoxApp
import com.iqonic.woobox.fragments.MyCartFragment
import com.iqonic.woobox.fragments.ProfileFragment
import com.iqonic.woobox.fragments.WishListFragment
import com.iqonic.woobox.fragments.home.HomeFragment
import com.iqonic.woobox.fragments.home.HomeFragment2
import com.iqonic.woobox.models.CategoryData
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.woobox.utils.Constants.AppBroadcasts.ORDER_COUNT_CHANGE
import com.iqonic.woobox.utils.Constants.AppBroadcasts.PROFILE_UPDATE
import com.iqonic.woobox.utils.Constants.AppBroadcasts.WISHLIST_UPDATE
import com.iqonic.woobox.utils.Constants.KeyIntent.DATA
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_ORDER_COUNT
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_WISHLIST_COUNT
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.item_navigation_category.view.*
import kotlinx.android.synthetic.main.layout_sidebar.*
import kotlinx.android.synthetic.main.menu_cart.*
import kotlinx.android.synthetic.main.toolbar.*

class DashBoardActivity : AppBaseActivity() {

    private var selectedDashboard: Int=0
    //region Variables
    private var count: String = ""
    private lateinit var mHomeFragment :Fragment
    private val mWishListFragment = WishListFragment()
    private val mCartFragment = MyCartFragment()
    private val mProfileFragment = ProfileFragment()
    var selectedFragment: Fragment? = null
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        if (supportFragmentManager.findFragmentById(R.id.container) != null) {
            supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentById(R.id.container)!!).commit()
        }
        selectedDashboard= getSharedPrefInstance().getIntValue(Constants.SharedPref.KEY_DASHBOARD,0)
        if (selectedDashboard==0){
            mHomeFragment=HomeFragment()
        }else if (selectedDashboard==1){
            mHomeFragment=HomeFragment2()
        }

//        mHomeFragment=HomeFragment()

        setToolbar(toolbar); setUpDrawerToggle(); loadHomeFragment(); setListener()

        if (isLoggedIn()) {
            loadApis()
            setWishCount(); setCartCountFromPref()
            llInfo.show()
            tvLogout.text = getString(R.string.lbl_logout)
            tvLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0)
        } else {
            tvLogout.text = getString(R.string.lbl_sign_in)
            tvLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_login, 0, 0, 0)
        }
        BroadcastReceiverExt(this) {
            onAction(CART_COUNT_CHANGE) {
                setCartCountFromPref()
                if (mCartFragment.isAdded) mCartFragment.invalidateCartLayout(getCartListFromPref())
            }
            onAction(ORDER_COUNT_CHANGE) { setOrderCount() }
            onAction(PROFILE_UPDATE) { setUserInfo() }
            onAction(WISHLIST_UPDATE) { setWishCount() }
        }
        setUserInfo(); tvVersionCode.text = String.format("%S %S", "V", getAppVersionName())
    }

    override fun onResume() {
        super.onResume()
        if (selectedDashboard!= getSharedPrefInstance().getIntValue(Constants.SharedPref.KEY_DASHBOARD,0)){
            recreate()
        }
    }

    private fun loadApis() {
        if (isNetworkAvailable()) {
            getOrderData(); fetchAndStoreCartData(); fetchAndStoreWishListData(); fetchAndStoreAddressData()
        }
    }

    //region Clicks
    private fun setListener() {
        civProfile.onClick {
            if (isLoggedIn()) {
                launchActivity<EditProfileActivity>()
            } else {
                launchActivity<SignInUpActivity>()
            }
            closeDrawer()
        }
        llHome.onClick {
            closeDrawer()
            enable(ivHome)
            loadFragment(mHomeFragment)
            title = getString(R.string.home)
        }

        llWishList.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivWishList)
            loadFragment(mWishListFragment)
            title = getString(R.string.lbl_wish_list)
        }
        llWishlistData.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            loadWishListFragment()
        }

        llCart.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivCart)
            tvNotificationCount.hide()
            loadFragment(mCartFragment)
            if (mCartFragment.isAdded) {
                mCartFragment.invalidateCartLayout(getCartListFromPref())
            }
            title = getString(R.string.cart)
        }

        llProfile.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>(); return@onClick
            }
            closeDrawer()
            enable(ivProfile)
            loadFragment(mProfileFragment)
            title = getString(R.string.profile)
        }
        tvAccount.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>()
            } else {
                launchActivity<AccountActivity>(Constants.RequestCode.ACCOUNT)
            }
            closeDrawer()
        }
        tvSettings.onClick {
            launchActivity<SettingActivity>(requestCode = Constants.RequestCode.SETTINGS)
            closeDrawer()
        }
        tvBlog.onClick {
            launchActivity<BlogActivity>()
            closeDrawer()
        }
        tvHelp.onClick { launchActivity<HelpActivity>(); closeDrawer() }
        tvFaq.onClick { launchActivity<FAQActivity>(); closeDrawer() }
        tvContactus.onClick { launchActivity<ContactUsActivity>(); closeDrawer() }
        tvAbout.onClick { launchActivity<AboutActivity>(); closeDrawer() }
        ivCloseDrawer.onClick { closeDrawer() }
        tvLogout.onClick {
            if (isLoggedIn()) {
                val dialog = getAlertDialog(
                    getString(R.string.lbl_logout_confirmation),
                    onPositiveClick = { _, _ ->
                        clearLoginPref()
                        launchActivityWithNewTask<DashBoardActivity>()
                        //recreate()
                    },
                    onNegativeClick = { dialog, _ ->
                        dialog.dismiss()
                    })
                dialog.show()
                closeDrawer()
            } else {
                launchActivity<SignInUpActivity>()
            }
        }
        llOrders.onClick {
            if (isLoggedIn()) {
                launchActivity<OrderActivity>()
            } else {
                launchActivity<SignInUpActivity>()
            }
            closeDrawer()
        }
        tvShareApp.onClick { closeDrawer(); shareMyApp(this@DashBoardActivity, "", "") }
        tvLblOffer.onClick { closeDrawer(); launchActivity<OfferActivity>() }
    }
    //endregion

    //region Set Data
    private fun showCartCount() {
        if (isLoggedIn() && !count.checkIsEmpty() && !count.equals("0", false)) {
            tvNotificationCount.show()
        } else {
            tvNotificationCount.hide()
        }
    }

    private fun setOrderCount() {
        tvOrderCount.text =
            getSharedPrefInstance().getIntValue(KEY_ORDER_COUNT, 0).toDecimalFormat()
    }

    private fun getOrderData() {
        getOrders { setOrderCount() }
    }

    private fun setCartCountFromPref() {
        count = getCartCount()
        tvNotificationCount.text = count
        showCartCount()
        if (mCartFragment.isVisible) tvNotificationCount.hide()
    }

    private fun setUserInfo() {
        txtDisplayName.text = getUserFullName()
        changeProfile()
    }

    private fun setWishCount() {
        tvWishListCount.text =
            getSharedPrefInstance().getIntValue(KEY_WISHLIST_COUNT, 0).toDecimalFormat()
        if (mWishListFragment.isAdded) mWishListFragment.wishListItemChange()
    }

    //endregion

    //region Fragment Setups
    private fun loadWishListFragment() {
        enable(ivWishList)
        loadFragment(mWishListFragment)
        title = getString(R.string.lbl_wish_list)
    }

    fun setDrawerCategory(it: ArrayList<CategoryData>) {
        rvCategory.create(
            it.size,
            R.layout.item_navigation_category,
            it,
            getVerticalLayout(false),
            onBindView = { item, position ->
                tvCategory.text = item.name.getHtmlString()
                if (item.image != null) {
                    ivCat.loadImageFromUrl(
                        item.image,
                        aPlaceHolderImage = R.drawable.cat_placeholder
                    )
                }
            },
            itemClick = { item, position ->
                closeDrawer()
                launchActivity<SubCategoryActivity> { putExtra(DATA, item) }
            })
        rvCategory.isNestedScrollingEnabled = false
    }

    private fun loadFragment(aFragment: Fragment) {
        if (selectedFragment != null) {
            if (selectedFragment == aFragment) return
            hideFragment(selectedFragment!!)
        }
        if (aFragment.isAdded) {
            showFragment(aFragment)
        } else {

            addFragment(aFragment, R.id.container)
        }
        selectedFragment = aFragment
    }

    fun loadHomeFragment() {
        enable(ivHome)
        //if (!mHomeFragment.isAdded) loadFragment(mHomeFragment) else showFragment(mHomeFragment)
        loadFragment(mHomeFragment)
        title = getString(R.string.home)
        if (mHomeFragment is HomeFragment){
            (mHomeFragment as HomeFragment).onNetworkRetry = { loadApis() }
        }else if (mHomeFragment is HomeFragment2){
            (mHomeFragment as HomeFragment2).onNetworkRetry = { loadApis() }
        }

    }
    //endregion

    //region Common
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            !mHomeFragment.isVisible -> loadHomeFragment()
            else -> super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCode.ACCOUNT) {
            loadWishListFragment()
        }
    }

    private fun enable(aImageView: ImageView?) {
        disableAll()
        showCartCount()
        aImageView?.background = getDrawable(R.drawable.bg_circle_primary_light)
        aImageView?.applyColorFilter(color(R.color.colorPrimary))
    }

    private fun disableAll() {
        disable(ivHome)
        disable(ivWishList)
        disable(ivCart)
        disable(ivProfile)
    }

    private fun disable(aImageView: ImageView?) {
        aImageView?.background = null
        aImageView?.applyColorFilter(color(R.color.textColorSecondary))
    }

    private fun setUpDrawerToggle() {
        val toggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                if (WooBoxApp.language == "ar") {
                    main.translationX = -slideOffset * drawerView.width
                } else {
                    main.translationX = slideOffset * drawerView.width
                }
                (drawerLayout).bringChildToFront(drawerView)
                (drawerLayout).requestLayout()
            }
        }
        toggle.setToolbarNavigationClickListener {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_drawer,
                theme
            )
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(llLeftDrawer)) runDelayed(50) {
            drawerLayout.closeDrawer(
                llLeftDrawer
            )
        }
    }

    fun changeProfile() {
        if (isLoggedIn()) {
            civProfile.loadImageFromUrl(getUserProfile(), aPlaceHolderImage = R.drawable.ic_profile)
        }
    }


    //endregion
}