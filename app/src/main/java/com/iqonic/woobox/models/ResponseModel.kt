package com.iqonic.woobox.models

import java.io.Serializable

data class DashboardResponse(
    val testimonials: ArrayList<Testimonials> = ArrayList(),
    val banner_1: Banner1? = null,
    val banner_2: Banner2? = null,
    val banner_3: Banner3? = null,
    val currency_symbol: CurrencySymbol = CurrencySymbol()
    ,
    val deal_product: ArrayList<ProductDataNew> = ArrayList(),
    val featured: ArrayList<ProductDataNew> = ArrayList(),
    val newest: ArrayList<ProductDataNew> = ArrayList(),
    val offer: ArrayList<ProductDataNew> = ArrayList(),
    val suggested_product: ArrayList<ProductDataNew> = ArrayList(),
    val theme_color: String = "",
    val total_order: Int = 0,
    val you_may_like: ArrayList<ProductDataNew> = ArrayList(),
    val social_link: SocialLink? = null
) {
    data class Banner1(
        val desc: String = "",
        val image: String = "",
        val thumb: String = "",
        val url: String = ""
    )

    data class Banner2(
        val desc: String = "",
        val image: String = "",
        val thumb: String = "",
        val url: String = ""
    )

    data class Banner3(
        val desc: String = "",
        val image: String = "",
        val thumb: String = "",
        val url: String = ""
    )

    data class CurrencySymbol(val currency: String = "", val currency_symbol: String = "")
    data class SocialLink(
        val whatsapp: String = "",
        val facebook: String = "",
        val twitter: String = "",
        val instagram: String = "",
        val contact: String = "",
        val privacy_policy: String = "",
        val term_condition: String = "",
        val copyright_text: String = ""
    )
}

data class LoginResponse(
    val user_id: String? = null,
    val token: String,
    val user_display_name: String,
    val user_email: String,
    val user_nicename: String,
    val message: String,
    val user_role: List<String>? = null,
    val avatar: String,
    val profile_image: String
)

data class LoginData(
    val avatar_url: String,
    val billing: Billing,
    val date_created: String,
    val date_created_gmt: String,
    val date_modified: String,
    val date_modified_gmt: String,
    val email: String,
    val first_name: String,
    val id: Int,
    val is_paying_customer: Boolean,
    val last_name: String,
    val role: String,
    val shipping: Shipping,
    val username: String
) : BaseResponse()

data class Shipping(
    val address_1: String,
    val address_2: String,
    val city: String,
    val company: String,
    val country: String,
    val first_name: String,
    val last_name: String,
    val postcode: String,
    val state: String
)

data class Billing(
    val address_1: String,
    val address_2: String,
    val city: String,
    val company: String,
    val country: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val phone: String,
    val postcode: String,
    val state: String
)

data class SliderImagesResponse(val image: String, val url: String)

data class CartResponse(
    val cart_id: String,
    val pro_id: String,
    val name: String,
    val sku: String,
    val stock: String,
    val stock_quantity: Int? = null,
    val price: String,
    val regular_price: String,
    val sale_price: String,
    val thumbnail: String,
    val full: String? = null,
    var quantity: String,
    val created_at: String,
    val size: String,
    val color: String
) : BaseResponse()

data class UpdateCartResponse(val message: String, val quantity: Int)

data class WishListData(
    val date_added: String,
    val in_stock: Boolean,
    val item_id: Int,
    val stock_quantity: Int? = null,
    val meta: Meta,
    var pro_id: Int,
    var name: String? = null,
    var sale_price: String? = null,
    var price: String? = null,
    var full: String? = null,
    var regular_price: String? = null,
    var user_id: String? = null
)

data class Meta(
    var average_rating: String? = null,
    var colors: ArrayList<String>? = null,
    var image: String? = null,
    var name: String? = null,
    var regular_price: String? = null,
    var sale_price: String? = null,
    var size: ArrayList<String>? = null
)

data class ProductReviewData(
    val date_created: String = "",
    val date_created_gmt: String = "",
    val id: Int = 0,
    val product_id: Int = 0,
    val rating: Int = 0,
    val review: String = "",
    val name: String = "",
    val email: String = "",
    val verified: Boolean = false
)

data class Brand(
    var brand_name: String? = null,
    var brand_color: Int? = null,
    var brand_id: Int? = null,
    var isSelected: Boolean? = false
) : Serializable

data class DeletedReviewData(val deleted: Boolean, val previous: ProductReviewData) : Serializable

data class SubCategory(
    var subcategory_name: String? = null,
    var isSelected: Boolean? = false,
    var category: Int? = null
) : Serializable

data class Discount(var discount: String? = null, var isSelected: Boolean? = false) : Serializable

data class Category(
    var category_name: String? = null,
    var category_img: Int? = null,
    var category_color: Int? = null
) : Serializable

data class CategoryData(
    val description: String,
    val term_id: Int,
    val id: Int,
    val cat_ID: Int,
    val image: String? = null,
    val menu_order: Int,
    var name: String,
    val parent: Int,
    val category_count: Int,
    val slug: String,
    val subcategory: ArrayList<Int>? = null,
    var isSelected: Boolean
) : BaseResponse(), Serializable

data class MyOrderData(
    val billing: Billing = Billing(),
    val cart_hash: String = "",
    val cart_tax: String = "",
    val coupon_lines: List<Any> = listOf(),
    val created_via: String = "",
    val currency: String = "",
    val customer_id: Int = 0,
    val customer_ip_address: String = "",
    val customer_note: String = "",
    val customer_user_agent: String = "",
    val date_completed: String? = "",
    val date_completed_gmt: String = "",
    val date_created: String = "",
    val date_created_gmt: String = "",
    val date_modified: String = "",
    val date_modified_gmt: String = "",
    val date_paid: String = "",
    val date_paid_gmt: String = "",
    val discount_tax: Double = 0.00,
    val discount_total: Double = 0.00,
    val fee_lines: List<Any> = listOf(),
    val id: Int = 0,
    val line_items: ArrayList<LineItem> = ArrayList(),
    val number: String = "",
    val order_key: String = "",
    val parent_id: Int = 0,
    val payment_method: String = "",
    val payment_method_title: String = "",
    val prices_include_tax: Boolean = false,
    val refunds: List<Refund> = listOf(),
    val shipping: Shipping = Shipping(),
    val shipping_lines: List<ShippingLine> = listOf(),
    val shipping_tax: String = "",
    val shipping_total: Double = 0.0,
    val status: String = "",
    val tax_lines: List<Any> = listOf(),
    val total: Double = 0.00,
    val total_tax: Double = 0.00,
    val transaction_id: String = "",
    val version: String = ""
) : Serializable {
    data class Refund(val id: Int = 0, val refund: String = "", val total: String = "") :
        Serializable

    data class Billing(
        val address_1: String = "",
        val address_2: String = "",
        val city: String = "",
        val company: String = "",
        val country: String = "",
        val email: String = "",
        val first_name: String = "",
        val last_name: String = "",
        val phone: String = "",
        val postcode: String = "",
        val state: String = ""
    ) : Serializable

    data class LineItem(
        val id: Int = 0,
        val meta_data: List<Any> = listOf(),
        val name: String = "",
        val price: Double = 0.0,
        val product_id: Int = 0,
        val quantity: Int = 0,
        val sku: String = "",
        val subtotal: String = "",
        val subtotal_tax: String = "",
        val tax_class: String = "",
        val taxes: List<Any> = listOf(),
        val total: Double = 0.0,
        val total_tax: String = "",
        val variation_id: Int = 0
    ) : Serializable

    data class ShippingLine(
        val id: Int = 0,
        val meta_data: List<MetaData> = listOf(),
        val method_id: String = "",
        val method_title: String = "",
        val taxes: List<Any> = listOf(),
        val total: String = "",
        val total_tax: String = ""
    ) : Serializable {
        data class MetaData(val id: Int = 0, val key: String = "", val value: String = "") :
            Serializable
    }

    data class Shipping(
        val address_1: String = "",
        val address_2: String = "",
        val city: String = "",
        val company: String = "",
        val country: String = "",
        val first_name: String = "",
        val last_name: String = "",
        val postcode: String = "",
        val state: String = ""
    ) : Serializable
}

open class Reward(
    var reward_img: Int? = null,
    var reward_value: String? = null,
    var reward: Int? = null
)

data class OrderTrack(
    val date_shipped: String,
    val tracking_id: String,
    val tracking_link: String,
    val tracking_number: String,
    val tracking_provider: String
)

data class Payment(
    val id: String = "",
    val method_description: String = "",
    val method_title: String = ""
)

data class Settings(val instructions: Instructions = Instructions(), val title: Title = Title())

data class Title(
    val default: String = "",
    val description: String = "",
    val id: String = "",
    val label: String = "",
    val placeholder: String = "",
    val tip: String = "",
    val type: String = "",
    val value: String = ""
)

data class Instructions(
    val default: String = "",
    val description: String = "",
    val id: String = "",
    val label: String = "",
    val placeholder: String = "",
    val tip: String = "",
    val type: String = "",
    val value: String = ""
)

data class PaymentResponse(
    val `data`: PaymentData? = null,
    val code: Int = 0,
    val message: String = ""
)

data class PaymentData(val redirect: String = "", val result: String = "")




data class Color(
    var color_name: Int? = null,
    var isSelected: Boolean? = false,
    var color: String? = null,
    var id: Int? = null
) : Serializable

data class Size(
    var size_name: String? = null,
    var isSelected: Boolean? = false,
    var size: String? = null,
    var id: Int? = null
) : Serializable

data class ProductModel(
    var attributes: List<Attributes>? = null,
    var average_rating: String? = null,
    var size: String? = null,
    var color: String? = null,
    var backordered: Boolean? = null,
    var backorders: String? = null,
    var backorders_allowed: Boolean? = null,
    var button_text: String? = null,
    var catalog_visibility: String? = null,
    var categories: List<Categories>? = null,
    var cross_sell_ids: List<Any>? = null,
    var date_created: String? = null,
    var date_created_gmt: String? = null,
    var date_modified: String? = null,
    var date_modified_gmt: String? = null,
    var date_on_sale_from: Any? = null,
    var date_on_sale_from_gmt: Any? = null,
    var date_on_sale_to: Any? = null,
    var date_on_sale_to_gmt: Any? = null,
    var default_attributes: List<Any>? = null,
    var description: String? = null,
    var dimensions: Dimensions? = null,
    var download_expiry: Int? = null,
    var download_limit: Int? = null,
    var downloadable: Boolean? = null,
    var downloads: List<Any>? = null,
    var external_url: String? = null,
    var featured: Boolean? = null,
    var grouped_products: List<Any>? = null,
    var id: Int? = null,
    var images: List<ImagesData>? = null,
    var manage_stock: Boolean? = null,
    var menu_order: Int? = null,
    var meta_data: List<Any>? = null,
    var name: String? = null,
    var on_sale: Boolean? = null,
    var parent_id: Int? = null,
    var permalink: String? = null,
    var price: Int? = null,
    var price_html: String? = null,
    var purchasable: Boolean? = null,
    var purchase_note: String? = null,
    var rating_count: Int? = null,
    var regular_price: String? = null,
    var related_ids: List<Int>? = null,
    var reviews_allowed: Boolean? = null,
    var sale_price: String? = null,
    var shipping_class: String? = null,
    var shipping_class_id: Int? = null,
    var shipping_required: Boolean? = null,
    var shipping_taxable: Boolean? = null,
    var short_description: String? = null,
    var sku: String? = null,
    var slug: String? = null,
    var sold_individually: Boolean? = null,
    var status: String? = null,
    var stock_quantity: Any? = null,
    var stock_status: String? = null,
    var tags: List<Any>? = null,
    var tax_class: String? = null,
    var tax_status: String? = null,
    var total_sales: Int? = null,
    var type: String? = null,
    var upsell_ids: List<Any>? = null,
    var variations: List<Any>? = null,
    var virtual: Boolean? = null,
    var weight: String? = null,
    var image: String? = null
) : BaseResponse(), Serializable

data class Attributes(
    var id: Int? = null,
    var name: String? = null,
    var options: ArrayList<String>? = null,
    var position: Double? = null,
    var variation: Boolean? = null,
    var visible: Boolean? = null
) : Serializable

data class Categories(val id: Int, val name: String, val slug: String) : Serializable

data class Dimensions(
    var height: String? = null,
    var length: String? = null,
    var width: String? = null
) : Serializable

data class ImagesData(var id: String? = null, var src: String? = null, var name: String? = null) :
    Serializable

data class ProductDataNew(
    val average_rating: String? = null,
    val backorders: String? = null,
    val num_pages: Int? = null,
    val brand: String? = null,
    val catalog_visibility: String? = null,
    val categories: List<Int?>? = null,
    var color: String? = null,
    val cross_sell_ids: List<Any?>? = null,
    val date_created: DateCreated? = null,
    val date_modified: DateModified? = null,
    val description: String? = null,
    val dimensions: String? = null,
    val featured: Boolean? = null,
    val full: String? = null,
    val gallery: List<String?>? = null,
    val get_purchase_note: String? = null,
    val height: String? = null,
    val length: String? = null,
    val manage_stock: Boolean? = null,
    val name: String? = null,
    val parent_id: Int? = null,
    val permalink: String? = null,
    val price: String? = null,
    val pro_id: Int? = null,
    val regular_price: String? = null,
    val review_count: Int? = null,
    val reviews_allowed: Boolean? = null,
    val sale_price: String? = null,
    val shipping_class_id: Int? = null,
    val short_description: String? = null,
    var size: String? = null,
    val sku: String? = null,
    val slug: String? = null,
    val sold_individually: Boolean? = null,
    val srno: Int? = null,
    val status: String? = null,
    val stock_quantity: Int? = null,
    val stock_status: String? = null,
    val tax_class: String? = null,
    val tax_status: String? = null,
    val thumbnail: String? = null,
    val type: String? = null,
    val upsell_ids: List<Any?>? = null,
    val virtual: Boolean? = null,
    val weight: String? = null,
    val width: String? = null
) : Serializable

data class Testimonials(
    val name: String? = null,
    val message: String? = null,
    val designation: String? = null,
    val company: String? = null,
    val image: String? = null
) : Serializable

data class DateCreated(
    val date: String? = null,
    val timezone: String? = null,
    val timezone_type: Int? = null
) : Serializable

data class DateModified(
    val date: String? = null,
    val timezone: String? = null,
    val timezone_type: Int? = null
) : Serializable

data class CheckoutUrlResponse(val checkout_url: String = "")

data class Blog(
    val image: String? = null,
    val title: String? = null,
    val description: String? = null,
    val publish_date: String? = null
) : Serializable

data class Address(
    var ID: Int? = null,
    var address_1: String = "",
    var address_2: String = "",
    var city: String = "",
    var company: String = "",
    var country: String = "",
    var created_at: String? = null,
    var first_name: String = "",
    var last_name: String = "",
    var postcode: String = "",
    var state: String = "",
    var user_id: String? = null,
    var fullAddress: String? = null,
    var contact: String = ""
) : Serializable {
    fun getAddress(): String {
        if (fullAddress != null) {
            return fullAddress!!
        }
        fullAddress = ""
        if (address_1.isNotEmpty()) {
            fullAddress = address_1
        }
        if (address_2.isNotEmpty()) {
            fullAddress += "\n" + address_2
        }
        if (city.isNotEmpty() && state.isNotEmpty()) {
            fullAddress += "\n$city,$state"
        }
        if (postcode.isNotEmpty() && state.isNotEmpty()) {
            fullAddress += "\n$state -$postcode"
        }
        return fullAddress!!
    }
}

data class ProfileImage(
    val code: Int = 0,
    val message: String = "",
    val profile_image: String = ""
)

data class ProductModelNew(
    val attributes: List<Attribute> = listOf(),
    val average_rating: String = "",
    val backordered: Boolean = false,
    val backorders: String = "",
    val backorders_allowed: Boolean = false,
    val button_text: String = "",
    val catalog_visibility: String = "",
    val categories: List<ProductCategory> = listOf(),
    val cross_sell_ids: List<Any> = listOf(),
    val date_created: String = "",
    val date_modified: String = "",
    val date_on_sale_from: String = "",
    val date_on_sale_to: String = "",
    val default_attributes: List<Any> = listOf(),
    val description: String = "",
    val dimensions: Dimensions = Dimensions(),
    val download_expiry: Int = 0,
    val download_limit: Int = 0,
    val download_type: String = "",
    val downloadable: Boolean = false,
    val downloads: List<Any> = listOf(),
    val external_url: String = "",
    val featured: Boolean = false,
    val grouped_products: List<Any> = listOf(),
    val id: Int = 0,
    val images: List<Image> = listOf(),
    val in_stock: Boolean = false,
    val manage_stock: Boolean = false,
    val menu_order: Int = 0,
    val name: String = "",
    val on_sale: Boolean = false,
    val parent_id: Int = 0,
    val permalink: String = "",
    val price: String = "",
    val price_html: String = "",
    val purchasable: Boolean = false,
    val purchase_note: String = "",
    val rating_count: Int = 0,
    val regular_price: String = "",
    val related_ids: List<Any> = listOf(),
    val reviews_allowed: Boolean = false,
    val sale_price: String = "",
    val shipping_class: String = "",
    val shipping_class_id: Int = 0,
    val shipping_required: Boolean = false,
    val shipping_taxable: Boolean = false,
    val short_description: String = "",
    val sku: String = "",
    val slug: String = "",
    val sold_individually: Boolean = false,
    val status: String = "",
    val stock_quantity: Int? = 0,
    val tags: List<Any> = listOf(),
    val tax_class: String = "",
    val tax_status: String = "",
    val total_sales: String = "",
    val type: String = "",
    val variations: List<Int> = listOf(),
    val virtual: Boolean = false,
    val weight: String = ""
)

data class Attribute(
    val id: Int = 0,
    val name: String = "",
    val option: String = "",
    var options: List<String>? = null

):Serializable

data class Image(
    val alt: String = "",
    val date_created: String = "",
    val date_modified: String = "",
    val id: Int = 0,
    val name: String = "",
    val position: Int = 0,
    val src: String = ""
):Serializable

data class ProductCategory(
    val id: Int = 0,
    val name: String = "",
    val slug: String = ""
):Serializable