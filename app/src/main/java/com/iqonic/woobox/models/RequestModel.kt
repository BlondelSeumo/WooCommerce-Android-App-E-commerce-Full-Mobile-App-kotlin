package com.iqonic.woobox.models

class RequestModel {
    /**
     * Request for login
     */
    var username: String? = null
    var password: String? = null

    var user_login: String? = null

    var user_id: String? = null
    /**
     * Request for social login
     */

    var accessToken: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var loginType: String? = null
    var photoURL: String? = null
    /**
     * Request for post product review
     */
    var pro_id: String? = null
    var product_id: String? = null
    var review: String? = null
    var reviewer: String? = null
    var reviewer_email: String? = null
    var rating: String? = null

    /**
     * Request for update order
     */
    var status: String? = null

    /**
     * Request for delete product review
     */
    var force: Boolean? = null

    /**
     * Request for update review
     */
    var id: String? = null


    /**
     * Request for add item to cart
     */

    var cart_id: Int? = null
    var quantity: Int? = null
    var size: String? = null
    var color: String? = null

    /**
     * Request for Create /update cart item
     */
    var first_name: String? = null
    var last_name: String? = null
    var email: String? = null
    var mobile_no: String? = null
    var base64_img: String? = null

    /**
     * Request for create order
     */
    var payment_method_title: String? = null
    var set_paid: Boolean? = null
    var shipping: ArrayList<BillingShippingRequest>? = null
    var line_items: ArrayList<LinItemsRequest>? = null
    var shipping_lines: ShippingLineRequest? = null
    var customer_id: Int? = null

    /**
     * Request for process payment
     */
    var txn_id: String? = null
    var order_id: Int? = null
    var payment_method: String? = null

    /**
     * Search Request
     */
    var page: Int? = null
    var text: String? = null

    var cat_id: Int? = null
    var category: Int? = null

    /**
     * Requests for Address
     */
    var ID: Int? = null

    data class FilterColors(
        var term_id: Int? = null,
        var name: String? = null,
        var slug: String? = null,
        var isSelected: Boolean? = false
    )

    data class FilterBrands(
        var term_id: Int? = null,
        var name: String? = null,
        var isSelected: Boolean? = false
    )

    data class FilterSizes(
        var term_id: Int? = null,
        var name: String? = null,
        var isSelected: Boolean? = false
    )

    data class FilterCategories(
        var term_id: Int? = null,
        var cat_ID: Int? = null,
        var cat_name: String? = null,
        var slug: String? = null,
        var isSelected: Boolean? = false
    )

}
