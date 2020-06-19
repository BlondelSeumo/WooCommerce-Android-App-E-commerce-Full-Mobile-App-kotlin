package com.iqonic.woobox.utils.extensions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.WooBoxApp
import com.iqonic.woobox.WooBoxApp.Companion.getAppInstance
import com.iqonic.woobox.WooBoxApp.Companion.noInternetDialog
import com.iqonic.woobox.activity.DashBoardActivity
import com.iqonic.woobox.activity.ProductDetailActivity
import com.iqonic.woobox.activity.ProductDetailActivityNew
import com.iqonic.woobox.models.*
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.Constants.AppBroadcasts.ADDRESS_UPDATE
import com.iqonic.woobox.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.woobox.utils.Constants.AppBroadcasts.ORDER_COUNT_CHANGE
import com.iqonic.woobox.utils.Constants.AppBroadcasts.PROFILE_UPDATE
import com.iqonic.woobox.utils.Constants.AppBroadcasts.WISHLIST_UPDATE
import com.iqonic.woobox.utils.Constants.KeyIntent.DATA
import com.iqonic.woobox.utils.Constants.PLAY_STORE_URL_PREFIX
import com.iqonic.woobox.utils.Constants.SharedPref.CART_DATA
import com.iqonic.woobox.utils.Constants.SharedPref.CATEGORY_DATA
import com.iqonic.woobox.utils.Constants.SharedPref.DEFAULT_CURRENCY
import com.iqonic.woobox.utils.Constants.SharedPref.IS_LOGGED_IN
import com.iqonic.woobox.utils.Constants.SharedPref.IS_SOCIAL_LOGIN
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_ADDRESS
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_CART_COUNT
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_DASHBOARD
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_ORDERS
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_RECENTS
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_USER_ADDRESS
import com.iqonic.woobox.utils.Constants.SharedPref.KEY_WISHLIST_COUNT
import com.iqonic.woobox.utils.Constants.SharedPref.SLIDER_IMAGES_DATA
import com.iqonic.woobox.utils.Constants.SharedPref.THEME_COLOR
import com.iqonic.woobox.utils.Constants.SharedPref.USER_DISPLAY_NAME
import com.iqonic.woobox.utils.Constants.SharedPref.USER_EMAIL
import com.iqonic.woobox.utils.Constants.SharedPref.USER_FIRST_NAME
import com.iqonic.woobox.utils.Constants.SharedPref.USER_ID
import com.iqonic.woobox.utils.Constants.SharedPref.USER_LAST_NAME
import com.iqonic.woobox.utils.Constants.SharedPref.USER_NICE_NAME
import com.iqonic.woobox.utils.Constants.SharedPref.USER_PROFILE
import com.iqonic.woobox.utils.Constants.SharedPref.USER_ROLE
import com.iqonic.woobox.utils.Constants.SharedPref.USER_TOKEN
import com.iqonic.woobox.utils.Constants.SharedPref.USER_USERNAME
import com.iqonic.woobox.utils.Constants.SharedPref.WISHLIST_DATA
import com.iqonic.woobox.utils.SharedPrefUtils
import kotlinx.android.synthetic.main.dialog_no_internet.*
import kotlinx.android.synthetic.main.item_product_new.view.*
import kotlinx.android.synthetic.main.layout_paymentdetail.*
import java.util.concurrent.TimeUnit

fun isLoggedIn(): Boolean = getSharedPrefInstance().getBooleanValue(IS_LOGGED_IN)
fun getUserId(): String = getSharedPrefInstance().getStringValue(USER_ID)
fun getDefaultCurrency(): String = getSharedPrefInstance().getStringValue(DEFAULT_CURRENCY)
fun getThemeColor(): String = getSharedPrefInstance().getStringValue(THEME_COLOR)
fun Context.getUserFullName(): String {
    return when {
        isLoggedIn() -> (getSharedPrefInstance().getStringValue(USER_FIRST_NAME) + " " + getSharedPrefInstance().getStringValue(
                USER_LAST_NAME
        )).toCamelCase()
        else -> getString(R.string.text_guest_user)
    }
}

fun getUserName(): String = getSharedPrefInstance().getStringValue(USER_USERNAME)
fun getFirstName(): String = getSharedPrefInstance().getStringValue(USER_FIRST_NAME)
fun getLastName(): String = getSharedPrefInstance().getStringValue(USER_LAST_NAME)
fun getUserProfile(): String = getSharedPrefInstance().getStringValue(USER_PROFILE)
fun getEmail(): String = getSharedPrefInstance().getStringValue(USER_EMAIL)
fun getApiToken(): String = getSharedPrefInstance().getStringValue(USER_TOKEN)
fun getCartCount(): String = getSharedPrefInstance().getIntValue(KEY_CART_COUNT, 0).toString()


/**
 * Add shared preference related to user session here
 */
fun clearLoginPref() {
    getSharedPrefInstance().removeKey(IS_LOGGED_IN)
    getSharedPrefInstance().removeKey(USER_ID)
    getSharedPrefInstance().removeKey(USER_DISPLAY_NAME)
    getSharedPrefInstance().removeKey(USER_EMAIL)
    getSharedPrefInstance().removeKey(USER_NICE_NAME)
    getSharedPrefInstance().removeKey(USER_TOKEN)
    getSharedPrefInstance().removeKey(USER_FIRST_NAME)
    getSharedPrefInstance().removeKey(USER_LAST_NAME)
    getSharedPrefInstance().removeKey(USER_PROFILE)
    getSharedPrefInstance().removeKey(USER_ROLE)
    getSharedPrefInstance().removeKey(USER_USERNAME)
    getSharedPrefInstance().removeKey(WISHLIST_DATA)
    getSharedPrefInstance().removeKey(CART_DATA)
    getSharedPrefInstance().removeKey(KEY_RECENTS)
    getSharedPrefInstance().removeKey(KEY_DASHBOARD)
    getSharedPrefInstance().removeKey(KEY_ADDRESS)
    getSharedPrefInstance().removeKey(KEY_USER_ADDRESS)
}

fun getSharedPrefInstance(): SharedPrefUtils {
    return if (WooBoxApp.sharedPrefUtils == null) {
        WooBoxApp.sharedPrefUtils = SharedPrefUtils()
        WooBoxApp.sharedPrefUtils!!
    } else {
        WooBoxApp.sharedPrefUtils!!
    }
}

fun RecyclerView.rvItemAnimation() {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
}

fun Context.openCustomTab(url: String) = CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))

fun ImageView.loadImageFromUrl(aImageUrl: String, aPlaceHolderImage: Int = R.drawable.placeholder, aErrorImage: Int = R.drawable.placeholder) {
    try {
        if (!aImageUrl.checkIsEmpty()) {
            Glide.with(getAppInstance()).load(aImageUrl).placeholder(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE).error(aErrorImage).into(this)
        } else {
            loadImageFromDrawable(aPlaceHolderImage)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadImageFromDrawable(@DrawableRes aPlaceHolderImage: Int) {
    Glide.with(getAppInstance()).load(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(this)
}

fun shareMyApp(context: Context, subject: String, message: String) {
    try {
        val appUrl = PLAY_STORE_URL_PREFIX + context.packageName
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, subject)
        var leadingText = "\n" + message + "\n\n"
        leadingText += appUrl + "\n\n"
        i.putExtra(Intent.EXTRA_TEXT, leadingText)
        context.startActivity(Intent.createChooser(i, "Share using"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.fontMedium(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_bold))
}

fun Context.fontSemiBold(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_medium))
}

fun Context.fontBold(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_semibold))
}

fun Activity.makeTransparentStatusBar() {
    val window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = this.resources.getColor(R.color.item_background)
}

enum class JsonFileCode {
    NO_INTERNET,
    LOADER
}

fun Activity.openLottieDialog(jsonFileCode: JsonFileCode = JsonFileCode.NO_INTERNET, onLottieClick: () -> Unit)  {
    try{
        val jsonFile: String = when (jsonFileCode) {
            JsonFileCode.NO_INTERNET -> "lottie/no_internet.json"
            JsonFileCode.LOADER -> "lottie/loader.json"
        }

        if (noInternetDialog == null) {
            noInternetDialog = Dialog(this, R.style.FullScreenDialog)
            noInternetDialog?.setContentView(R.layout.dialog_no_internet); noInternetDialog?.setCanceledOnTouchOutside(false); noInternetDialog?.setCancelable(false)
            noInternetDialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            noInternetDialog?.rlLottie?.onClick {
                if (!isNetworkAvailable()) {
                    snackBarError(getAppInstance().getString(R.string.error_no_internet))
                    return@onClick
                }
                noInternetDialog?.dismiss()
                onLottieClick()
            }
        }
        noInternetDialog?.lottieNoInternet?.setAnimation(jsonFile)
        if (!this.isFinishing &&!noInternetDialog!!.isShowing) {
            noInternetDialog?.show()
        }
    }catch (e:Exception){

    }

}

fun Activity.getAlertDialog(aMsgText: String, aTitleText: String = getString(R.string.lbl_dialog_title), aPositiveText: String = getString(R.string.lbl_yes), aNegativeText: String = getString(R.string.lbl_no), onPositiveClick: (dialog: DialogInterface, Int) -> Unit, onNegativeClick: (dialog: DialogInterface, Int) -> Unit): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(aTitleText)
    builder.setMessage(aMsgText)
    builder.setPositiveButton(aPositiveText) { dialog, which ->
        onPositiveClick(dialog, which)
    }
    builder.setNegativeButton(aNegativeText) { dialog, which ->
        onNegativeClick(dialog, which)
    }
    return builder.create()
}

fun Activity.productLayoutParams(): LinearLayout.LayoutParams {
    val width = (getDisplayWidth() / 2.5).toInt()
    val imgHeight = width + (width / 8)
    return LinearLayout.LayoutParams(width, imgHeight)
}

fun startOTPTimer(onTimerTick: (String) -> Unit, onTimerFinished: () -> Unit): CountDownTimer? {
    return object : CountDownTimer(60000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            onTimerTick(String.format("00 : %d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
        }

        override fun onFinish() {
            onTimerFinished()
        }
    }
}

fun Activity.showProductDetail(model: ProductDataNew) {
    launchActivity<ProductDetailActivityNew> {
        putExtra(Constants.KeyIntent.PRODUCT_ID,model.pro_id)
    }
    addToRecentProduct(model)
}

fun Activity.sendCartCountChangeBroadcast() {
    sendBroadcast(CART_COUNT_CHANGE)
}

fun Activity.sendProfileUpdateBroadcast() {
    sendBroadcast(PROFILE_UPDATE)
}

fun Activity.sendWishListBroadcast() {
    sendBroadcast(WISHLIST_UPDATE)
}

fun Activity.sendOrderCountChangeBroadcast() {
    sendBroadcast(ORDER_COUNT_CHANGE)
}

fun Activity.sendBroadcast(action: String) {
    val intent = Intent()
    intent.action = action
    sendBroadcast(intent)
}

fun setExpandableListViewHeight(listView: ExpandableListView, group: Int) {
    val listAdapter = listView.expandableListAdapter as ExpandableListAdapter
    var totalHeight = 0
    var item = 0
    val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.EXACTLY)
    for (i in 0 until listAdapter.groupCount) {
        val groupItem = listAdapter.getGroupView(i, false, null, listView)
        groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)

        totalHeight += groupItem.measuredHeight
        item = groupItem.measuredHeight

        if (((listView.isGroupExpanded(i)) && (i != group)) || ((!listView.isGroupExpanded(i)) && (i == group))) {
            for (j in 0 until listAdapter.getChildrenCount(i)) {
                val listItem = listAdapter.getChildView(
                        i, j, false, null,
                        listView
                )
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight
            }
        }
    }

    val params = listView.layoutParams
    var height =
            totalHeight + (listView.dividerHeight * (listAdapter.groupCount - 1)) + (item / 2)/*+((totalHeight/(listAdapter.groupCount-1)))/2)*/
    if (height < 10)
        height = 200
    params.height = height
    listView.layoutParams = params
    listView.requestLayout()
}

fun AppBaseActivity.getOrders(page: Int = 1, onApiSuccess: (ArrayList<MyOrderData>) -> Unit) {
    callApi(getRestApis().listAllOrders(getUserId().toInt(), page), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_ORDERS, Gson().toJson(it))
        sendOrderCountChangeBroadcast()
        onApiSuccess(it)
    }, onApiError = {
        snackBarError(it)
    }, onNetworkError = {
        noInternetSnackBar()
    })
}

fun AppBaseActivity.createCustomer(requestModel: RequestModel, onApiSuccess: (LoginData) -> Unit) {
    showProgress(true)
    callApi(getRestApis().createCustomer(id = getUserId(), request = requestModel), onApiSuccess = {
        showProgress(false)
        getSharedPrefInstance().setValue(USER_DISPLAY_NAME, it.first_name + " " + it.last_name)
        getSharedPrefInstance().setValue(USER_EMAIL, it.email)
        getSharedPrefInstance().setValue(USER_FIRST_NAME, it.first_name)
        getSharedPrefInstance().setValue(USER_LAST_NAME, it.last_name)
        onApiSuccess(it)
        sendProfileUpdateBroadcast()
    }, onApiError = {
        showProgress(false)
        snackBarError(it)
    }, onNetworkError = {
        openLottieDialog {
            createCustomer(requestModel, onApiSuccess)
        }
    })
}

fun AppBaseActivity.signIn(email: String, password: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
    val requestModel = RequestModel()
    requestModel.username = email
    requestModel.password = password
    showProgress(true)
    callApi(getRestApis(false).login(request = requestModel), onApiSuccess = {
        saveLoginResponse(it,false, password, onResult, onError)
    }, onApiError = {
        showProgress(false)
        onResult(false)
        onError(it)
    }, onNetworkError = {
        showProgress(false)
        openLottieDialog() {
            signIn(email, password, onResult, onError)
        }
    })
}

fun AppBaseActivity.socialLogin(email:String,accessToken: String, firstName: String, lastName: String, loginType: String, photoURL: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
    val requestModel = RequestModel()
    requestModel.email = email
    requestModel.accessToken = accessToken
    requestModel.firstName = firstName
    requestModel.lastName = lastName
    requestModel.loginType = loginType
    requestModel.photoURL = photoURL
    showProgress(true)
    callApi(getRestApis(false).socialLogin(request = requestModel), onApiSuccess = {
        saveLoginResponse(it,true, accessToken, onResult, onError)
    }, onApiError = {
        showProgress(false)
        onResult(false)
        onError(it)
    }, onNetworkError = {
        showProgress(false)
        openLottieDialog() {
            socialLogin(email,accessToken, firstName, lastName, loginType, photoURL, onResult, onError)
        }
    })
}

fun AppBaseActivity.saveLoginResponse(it: LoginResponse,isSocialLogin:Boolean, password: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
    if (it.user_role?.isNotEmpty()!!) {
        if (it.user_role[0] == "administrator") {
            showProgress(false)
            onError("Admin is not allowed")
        } else {
            if (it.user_id != null) {
                getSharedPrefInstance().setValue(USER_ID, it.user_id)
            }
            getSharedPrefInstance().setValue(USER_DISPLAY_NAME, it.user_display_name)
            getSharedPrefInstance().setValue(USER_EMAIL, it.user_email)
            getSharedPrefInstance().setValue(USER_NICE_NAME, it.user_nicename)
            getSharedPrefInstance().setValue(USER_TOKEN, it.token)
            if (it.profile_image.isNotEmpty()){
                getSharedPrefInstance().setValue(USER_PROFILE, it.profile_image)
            }
            getSharedPrefInstance().setValue(IS_SOCIAL_LOGIN,isSocialLogin)
            getSharedPrefInstance().setValue(Constants.SharedPref.USER_PASSWORD, password)

            callApi(getRestApis().retrieveCustomer(), onApiSuccess = { response ->
                showProgress(false)
                getSharedPrefInstance().setValue(Constants.SharedPref.SHOW_SWIPE, true)
                getSharedPrefInstance().setValue(USER_FIRST_NAME, response.first_name)
                getSharedPrefInstance().setValue(USER_LAST_NAME, response.last_name)
                getSharedPrefInstance().setValue(USER_ROLE, response.role)
                getSharedPrefInstance().setValue(USER_USERNAME, response.username)
                getSharedPrefInstance().setValue(IS_LOGGED_IN, true)
                onResult(true)
            }, onApiError = {
                showProgress(false)
                onResult(false)
                onError(it)
            }, onNetworkError = {
                showProgress(false)
                noInternetSnackBar()
            })
        }
    }
}

fun AppBaseActivity.processPayment(requestModel: RequestModel, isContainRedirectUrl: Boolean = true) {
    showProgress(true)
    callApi(getRestApis().processPayment(requestModel), onApiSuccess = {
        showProgress(false)
        if (it.data != null) {
            callApi(getRestApis(false).clearCartItems(), onApiSuccess = {
                fetchAndStoreCartData()
                if (!isContainRedirectUrl) launchActivityWithNewTask<DashBoardActivity>()
            })
            if (isContainRedirectUrl) openCustomTab(it.data.redirect)
        }
    }, onApiError = {
        snackBarError(it)
        showProgress(false)
    }, onNetworkError = {
        showProgress(false)
        openLottieDialog() {
            processPayment(requestModel)
        }
    })
}

fun recentProduct(): ArrayList<ProductDataNew> {
    val string = getSharedPrefInstance().getStringValue(KEY_RECENTS, "")
    if (string.isEmpty()) {
        return ArrayList()
    }
    return Gson().fromJson(string, object : TypeToken<ArrayList<ProductDataNew>>() {}.type)
}

fun addToRecentProduct(product: ProductDataNew) {
    val list = recentProduct()
    val pos = getPositionIfExist(list, product)
    if (pos != -1) {
        list.removeAt(pos)
    }
    list.add(product)
    getSharedPrefInstance().setValue(KEY_RECENTS, Gson().toJson(list))
}

fun getPositionIfExist(list: ArrayList<ProductDataNew>, product: ProductDataNew): Int {
    list.forEachIndexed { i: Int, productModel: ProductDataNew ->
        if (product.pro_id == productModel.pro_id) {
            return i
        }
    }
    return -1
}

fun getAddressList(): ArrayList<Address> {
    val string = getSharedPrefInstance().getStringValue(KEY_USER_ADDRESS, "")
    if (string.isEmpty()) {
        return ArrayList()
    }
    return Gson().fromJson(string, object : TypeToken<ArrayList<Address>>() {}.type)
}

fun getCartPositionIfExist(list: ArrayList<CartResponse>, product: CartResponse): Int {
    list.forEachIndexed { i: Int, productModel: CartResponse ->
        if (product.pro_id == productModel.pro_id) {
            return i
        }
    }
    return -1
}

fun AppBaseActivity.updateItem(product: CartResponse) {
    val list = getCartListFromPref()
    val pos = getCartPositionIfExist(list, product)
    if (pos != -1) {
        list.set(pos, product)
    }
    getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(list))
}


fun AppBaseActivity.getCartTotal(): Int? {
    val list = getCartListFromPref()
    var count = 0
    for (i in 0 until list.size) {
        if (list[i].sale_price.isNotEmpty()) {
            count += list[i].sale_price.toInt() * list[i].quantity.toInt()
        } else {
            if (list[i].price.isNotEmpty()) {
                count += list[i].price.toFloat().toInt() * list[i].quantity.toInt()
            }
        }
    }
    tvOffer.text = getString(R.string.text_offer_not_available)
    tvShippingCharge.text = getString(R.string.lbl_free)
    tvTotalAmount.text = count.toString().currencyFormat()
    return count
}

fun getCartTotalAmount(): Int? {
    val list = getCartListFromPref()
    var count = 0
    for (i in 0 until list.size) {
        if (list[i].sale_price.isNotEmpty()) {
            count += list[i].sale_price.toInt() * list[i].quantity.toInt()
        } else {
            if (list[i].price.isNotEmpty()) {
                count += list[i].price.toFloat().toInt() * list[i].quantity.toInt()
            }
        }
    }
    return count
}

fun isExistInCart(product: ProductDataNew, selectedColor: Boolean = false): Boolean {
    if (getSharedPrefInstance().getStringValue(CART_DATA) == "") {
        return false
    }
    val cartList = Gson().fromJson<ArrayList<CartResponse>>(getSharedPrefInstance().getStringValue(CART_DATA), object : TypeToken<ArrayList<CartResponse>>() {}.type)
    if (cartList != null && cartList.size > 0) {
        cartList.forEachIndexed { i: Int, model: CartResponse ->
            if (product.pro_id == model.pro_id.toInt()) {
                if (selectedColor) {
                    product.size = model.size
                    product.color = model.color
                }
                return true
            }
        }
    }
    return false
}

fun getCartListFromPref(): ArrayList<CartResponse> {
    if (getSharedPrefInstance().getStringValue(CART_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<CartResponse>>(getSharedPrefInstance().getStringValue(CART_DATA), object : TypeToken<ArrayList<CartResponse>>() {}.type)
}

fun getWishListFromPref(): ArrayList<WishListData> {
    if (getSharedPrefInstance().getStringValue(WISHLIST_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<WishListData>>(getSharedPrefInstance().getStringValue(WISHLIST_DATA), object : TypeToken<ArrayList<WishListData>>() {}.type)
}

fun isExistInWishList(product: ProductDataNew): Boolean {
    if (getSharedPrefInstance().getStringValue(WISHLIST_DATA) == "") {
        return false
    }
    val wishList = Gson().fromJson<ArrayList<WishListData>>(getSharedPrefInstance().getStringValue(WISHLIST_DATA), object : TypeToken<ArrayList<WishListData>>() {}.type)
    if (wishList != null && wishList.size > 0) {
        wishList.forEachIndexed { i: Int, model: WishListData ->
            if (product.pro_id == model.pro_id) {
                return true
            }
        }
    }
    return false
}

fun Activity.fetchAndStoreCartData() {
    callApi(getRestApis(false).getCart(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_CART_COUNT, it.size); getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(it)); sendCartCountChangeBroadcast()
    }, onApiError = {
        if (it == "no product available") {
            getSharedPrefInstance().setValue(KEY_CART_COUNT, 0); getSharedPrefInstance().setValue(CART_DATA, Gson().toJson(ArrayList<CartResponse>()))
            sendCartCountChangeBroadcast()
        } else {
           // snackBarError(it)
        }
    })
}

fun Activity.fetchAndStoreWishListData() {
    callApi(getRestApis(false).getWishList(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_WISHLIST_COUNT, it.size); getSharedPrefInstance().setValue(WISHLIST_DATA, Gson().toJson(it)); sendWishListBroadcast()
    }, onApiError = {
        if (it == "no product available") {
            getSharedPrefInstance().setValue(KEY_WISHLIST_COUNT, 0); getSharedPrefInstance().setValue(WISHLIST_DATA, Gson().toJson(ArrayList<WishListData>()))
            sendWishListBroadcast()
        } else {
           // snackBarError(it)
        }
    })
}

fun Activity.addToWishList(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).addWishList(request = requestModel), onApiSuccess = {
        fetchAndStoreWishListData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreWishListData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}

fun Activity.removeFromWishList(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).removeWishList(request = requestModel), onApiSuccess = {
        fetchAndStoreWishListData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreWishListData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}

fun getSlideImagesFromPref(): ArrayList<SliderImagesResponse> {
    if (getSharedPrefInstance().getStringValue(SLIDER_IMAGES_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<SliderImagesResponse>>(getSharedPrefInstance().getStringValue(SLIDER_IMAGES_DATA), object : TypeToken<ArrayList<SliderImagesResponse>>() {}.type)
}

fun getCategoryDataFromPref(): ArrayList<CategoryData> {
    if (getSharedPrefInstance().getStringValue(CATEGORY_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<CategoryData>>(getSharedPrefInstance().getStringValue(CATEGORY_DATA), object : TypeToken<ArrayList<CategoryData>>() {}.type)
}

fun setProductItem(view: View, item: ProductDataNew) {
    view.tvProductName.text = item.name
    if (item.sale_price!!.isNotEmpty()) {
        view.tvDiscountPrice.text = item.sale_price.currencyFormat()
    } else {
        view.tvDiscountPrice.text = item.price?.currencyFormat()
    }
    view.ratingBar.rating = item.average_rating!!.toFloat()
    view.tvOriginalPrice.text = item.regular_price?.currencyFormat()
    view.tvOriginalPrice.applyStrike()
    if (item.full != null) view.ivProduct.loadImageFromUrl(item.full)
}
fun Activity.fetchAndStoreAddressData() {
    callApi(getRestApis(false).getAddress(), onApiSuccess = {
        getSharedPrefInstance().setValue(KEY_USER_ADDRESS, Gson().toJson(it))
        sendBroadcast(ADDRESS_UPDATE)
        Log.e("response",Gson().toJson(it))
    }, onApiError = {

    }, onNetworkError = {
        noInternetSnackBar()
    })
}
fun Activity.addAddress(address: Address, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).addUpdateAddress(address), onApiSuccess = {
        fetchAndStoreAddressData()
        onSuccess(true)
    }, onApiError = {
        snackBarError(it); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}
fun Activity.removeAddress(requestModel: RequestModel, onSuccess: (Boolean) -> Unit) {
    callApi(getRestApis(false).deleteAddress(request = requestModel), onApiSuccess = {
        fetchAndStoreAddressData(); onSuccess(true)
    }, onApiError = {
        snackBarError(it); fetchAndStoreAddressData(); onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar(); onSuccess(false)
    })
}
fun Activity.changePassword(requestModel: RequestModel, onSuccess: (Boolean) -> Unit){
    callApi(getRestApis().changePassword(getUserId().toInt(), requestModel), onApiSuccess = {
        snackBar(getString(R.string.msg_successpwd));onSuccess(true)
    }, onApiError = {
        snackBarError(it);onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar();onSuccess(false)
    })
}

fun Activity.saveProfileImage(requestModel: RequestModel, onSuccess: (Boolean) -> Unit){
    callApi(getRestApis().saveProfileImage( requestModel), onApiSuccess = {
        Log.e("res",it.profile_image)
        getSharedPrefInstance().setValue(USER_PROFILE, it.profile_image)
        onSuccess(true)
    }, onApiError = {
        snackBarError(it);onSuccess(false)
    }, onNetworkError = {
        noInternetSnackBar();onSuccess(false)
    })
}