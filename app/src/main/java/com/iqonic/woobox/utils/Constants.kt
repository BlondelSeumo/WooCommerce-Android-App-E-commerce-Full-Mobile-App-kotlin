package com.iqonic.woobox.utils

import java.text.SimpleDateFormat
import java.util.*

object Constants {
    val FULL_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    val DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)
    val DD_MMM_YYYY = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val YYYY_MM_DD = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    const val PLAY_STORE_URL_PREFIX = "https://play.google.com/store/apps/details?id="
    val isAllowedToCreate = true
    const val myPreferences = "MyPreferences"

    object Config {
      /*   const  val DEFAULT_URL = "http://iqonic.design/wp-themes/foodsto_api/"
         const  val BASE_URL= DEFAULT_URL+"wp-json/"
        const val consumerKey = "ck_498bf48f2a3b541898dc3e88f80d7b704347d79d"
        const val consumerSecret = "cs_dee74759c9824663824f5111a44d9eb9db06d57b"
        const val token = "jVzw4QLz1OFAUlm2z8XGciFe"
        const val tokenSecret = "9tjoIx7pyHZJWedBFWR3GbNPc0DRlVmi3Gu7raKqKj4vSxrP"*/
         const  val DEFAULT_URL = "https://iqonic.design/wp-themes/woobox_api/"
         const  val BASE_URL= DEFAULT_URL+"wp-json/"
         const val consumerKey = "ZH0mxMZ63h22"
         const val consumerSecret = "z5RTcjT0NseqCYbfTQXaSwSNC054HLp0SGGhqbMKlpWWtOTH"
         const val token = "gN3kZdeh2Yyh6Ntdvip4Mokf"
         const val tokenSecret = "EhhwL3Ta6GSevnG6aKd4pCdp2X3rosIivzubXmZxSLkByi31"

    }

    object RequestCode {
        const val ADD_ADDRESS = 201
        const val ACCOUNT = 204
        const val SETTINGS = 205
        const val SIGN_IN = 206
    }

    object SharedPref {
        const val KEY_ORDERS = "orders"
        const val KEY_ORDER_COUNT = "order_count"
        const val KEY_RECENTS = "recentProduct"
        const val IS_LOGGED_IN = "isLoggedIn"
        const val USER_ID = "user_id"
        const val USER_DISPLAY_NAME = "user_display_name"
        const val USER_EMAIL = "user_email"
        const val USER_FIRST_NAME = "user_first_name"
        const val USER_LAST_NAME = "user_last_name"
        const val USER_PASSWORD = "user_password"
        const val USER_ROLE = "user_role"
        const val USER_USERNAME = "user_username"
        const val USER_NICE_NAME = "user_nice_name"
        const val USER_TOKEN = "token"
        const val USER_PROFILE = "user_profile"
        const val SWIPE = "swipe"
        const val SHOW_SWIPE = "showswipe"
        const val KEY_USER_ADDRESS = "user_address"
        const val CART_DATA = "cart_data"
        const val WISHLIST_DATA = "wishlist_data"
        const val KEY_WISHLIST_COUNT = "wishlist_count"
        const val KEY_CART_COUNT = "cart_count"
        const val SLIDER_IMAGES_DATA = "slider_images_data"
        const val CATEGORY_DATA = "category_data"
        const val THEME_COLOR = "theme_color"
        const val DEFAULT_CURRENCY = "default_currency"
        const val WHATSAPP = "whatsapp"
        const val FACEBOOK = "facebook"
        const val TWITTER = "twitter"
        const val INSTAGRAM = "instagram"
        const val CONTACT = "contact"
        const val PRIVACY_POLICY = "privacy_policy"
        const val TERM_CONDITION = "term_condition"
        const val COPYRIGHT_TEXT = "copyright_text"
        const val LANGUAGE = "selected_language"
        const val THEME = "selected_theme"
        const val IS_SOCIAL_LOGIN = "is_social_login"
        const val KEY_DASHBOARD = "selected_dashboard"
        const val KEY_ADDRESS = "selected_address"
    }

    object KeyIntent {
        const val PRODUCT_ID: String = "product_id"
        const val CATEGORY_DATA = "category_data"
        const val IS_ADDED_TO_CART = "isAddedToCart"
        const val TITLE = "title"
        const val ADDRESS_ID = "address_id"
        const val KEYID = "key_id"
        const val DATA = "data"
        const val VIEWALLID = "viewallid"
        const val SHOW_PAGINATION = "show_pagination"
    }

    object OrderStatus {
        const val PENDING = "pending"
        const val PROCESSING = "processing"
        const val ONHOLD = "on-hold"
        const val COMPLETED = "completed"
        const val CANCELLED = "cancelled"
        const val REFUNDED = "refunded"
    }

    object ViewAllCode {
        const val RECENTSEARCH = 100
        const val TOPSELLING = 101
        const val NEWEST = 102
        const val FEATURED = 103
        const val CATEGORY_NEWEST = 104
        const val CATEGORY_FEATURED = 105
    }

    object AppBroadcasts {
        const val CART_COUNT_CHANGE = "app.broadcast.setCartCount"
        const val ORDER_COUNT_CHANGE = "app.broadcast.OnOrderCountChanged"
        const val PROFILE_UPDATE = "app.broadcast.OnProfileUpdated"
        const val WISHLIST_UPDATE = "app.broadcast.OnWishListUpdated"
        const val ADDRESS_UPDATE = "app.broadcast.OnAddressUpdated"
    }

    object DateFormatCodes {
        const val YMD_HMS = 0
        const val YMD = 1
    }

    object THEME {
        const val DARK = 1
        const val LIGHT = 2
    }
}
