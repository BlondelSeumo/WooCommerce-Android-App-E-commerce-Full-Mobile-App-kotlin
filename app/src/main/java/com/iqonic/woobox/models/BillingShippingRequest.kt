package com.iqonic.woobox.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BillingShippingRequest {
    @SerializedName("first_name")
    @Expose
    var first_name: String? = null

    @SerializedName("last_name")
    @Expose
    var last_name: String? = null

    @SerializedName("address_1")
    @Expose
    var address_1: String? = null

    @SerializedName("address_2")
    @Expose
    var address_2: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("postcode")
    @Expose
    var postcode: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null
}

class ShippingLineRequest {

    @SerializedName("variation_id")
    @Expose
    var method_id: String? = null

    @SerializedName("method_title")
    @Expose
    var method_title: String? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

}

class LinItemsRequest {
    @SerializedName("product_id")
    @Expose
    var product_id: Int? = null

    @SerializedName("quantity")
    @Expose
    var quantity: Int? = null

    @SerializedName("variation_id")
    @Expose
    var variation_id: Int? = null
}