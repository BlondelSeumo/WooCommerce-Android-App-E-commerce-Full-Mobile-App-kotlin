package com.iqonic.woobox.network

import com.iqonic.woobox.models.*
import com.iqonic.woobox.utils.extensions.getApiToken
import com.iqonic.woobox.utils.extensions.getUserId
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RestApis {

    @POST("woobox-api/api/v1/woocommerce/get-dashboard")
    fun dashboard(@Body request: RequestModel): Call<DashboardResponse>

    @POST("jwt-auth/v1/token")
    fun login(@Body request: RequestModel): Call<LoginResponse>

    @POST("woobox-api/api/v1/customer/social_login")
    fun socialLogin(@Body request: RequestModel): Call<LoginResponse>

    @HTTP(method = "POST", path = "wc/v3/customers/{id}", hasBody = true)
    fun createCustomer(@Path("id") id: String, @Body request: RequestModel): Call<LoginData>

    @GET("wc/v3/customers/{id}")
    fun retrieveCustomer(@Path("id") id: String = getUserId()): Call<LoginData>

    @GET("woobox-api/api/v1/woocommerce/get-featured-product")
    fun listFeaturedProducts(): Call<ArrayList<ProductDataNew>>

    @POST("woobox-api/api/v1/woocommerce/get-product")
    fun listAllProducts(@Body request: RequestModel = RequestModel()): Call<ArrayList<ProductDataNew>>

    @POST("woobox-api/api/v1/woocommerce/get-single-product")
    fun listSingleProducts(@Body request: RequestModel): Call<ProductDataNew>

    @POST("woobox-api/api/v1/woocommerce/get-product")
    fun filterProduct(@Header("Content-Type") contentType: String = "application/json", @Body request: FilterProductRequest?): Call<ArrayList<ProductDataNew>>

    @POST("woobox-api/api/v1/woocommerce/get-featured-product")
    fun getFeaturedProducts(@Body request: FilterProductRequest, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ArrayList<ProductDataNew>>

    @GET("woobox-api/api/v1/woocommerce/get-product-attribute/")
    fun getProductAttributes(@Header("token") token: String = getApiToken()): Call<ProductAttributeResponse>

    @GET("woobox-api/api/v1/slider/get-slider/")
    fun getSliderImages(@Header("token") token: String = getApiToken()): Call<ArrayList<SliderImagesResponse>>

    @GET("wc/v1/products/{id}/reviews")
    fun listProductReviews(@Path("id") id: Int): Call<ArrayList<ProductReviewData>>

    @POST("wc/v3/products/reviews")
    fun createProductReview(@Body request: RequestModel): Call<ProductReviewData>

    @HTTP(method = "DELETE", path = "wc/v3/products/reviews/{id}", hasBody = true)
    fun deleteProductReview(@Path("id") id: Int, @Body request: RequestModel): Call<DeletedReviewData>

    @HTTP(method = "PUT", path = "wc/v3/products/reviews/{id}", hasBody = true)
    fun updateProductReview(@Path("id") id: Int, @Body request: RequestModel): Call<ProductReviewData>

    @POST("wc/v3/orders")
    fun createOrder(@Body request: RequestModel): Call<MyOrderData>

    @PUT("wc/v3/orders/{id}")
    fun updateOrder(@Path("id") id: Int): Call<MyOrderData>

    @GET("wc/v3/orders")
    fun listAllOrders(@Query("customer") customer: Int, @Query("page") page: Int): Call<ArrayList<MyOrderData>>

    @GET("wc/v1/orders/{order_id}/shipment-trackings")
    fun getOrderTracking(@Path("order_id") id: Int): Call<ArrayList<OrderTrack>>

    @GET("woobox-api/api/v1/payment/get-active-payment-gateway")
    fun paymentGateways(): Call<ArrayList<Payment>>

    @POST("wc/v2/process_payment")
    fun processPayment(@Body request: RequestModel): Call<PaymentResponse>

    @POST("woobox-api/api/v1/woocommerce/place-order")
    fun processOtherPayment(@Body request: RequestModel): Call<ArrayList<PaymentResponse>>

    @POST("woobox-api/api/v1/woocommerce/get-checkout-url")
    fun getCheckoutUrl(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<CheckoutUrlResponse>

    @POST("wp/v2/users/{id}")
    fun changePassword(@Path("id") id: Int, @Body request: RequestModel): Call<ResponseBody>

    @POST("wp/v2/users/lostpassword")
    fun forgetPassword(@Body request: RequestModel): Call<BaseResponse>

    @POST("woobox-api/api/v1/cart/add-cart/")
    fun addItemToCart(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<BaseResponse>

    @GET("woobox-api/api/v1/cart/get-cart/")
    fun getCart(@Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ArrayList<CartResponse>>

    @POST("woobox-api/api/v1/cart/delete-cart/")
    fun removeCartItem(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<BaseResponse>

    @POST("woobox-api/api/v1/cart/clear-cart/")
    fun clearCartItems(@Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<BaseResponse>

    @POST("woobox-api/api/v1/cart/update-cart/")
    fun updateItemInCart(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<UpdateCartResponse>

    @POST("woobox-api/api/v1/wishlist/add-wishlist/")
    fun addWishList(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<BaseResponse>

    @GET("woobox-api/api/v1/wishlist/get-wishlist/")
    fun getWishList(@Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ArrayList<WishListData>>

    @POST("woobox-api/api/v1/wishlist/delete-wishlist/")
    fun removeWishList(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<BaseResponse>

    @POST("woobox-api/api/v1/woocommerce/get-search-product")
    fun searchProducts(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ArrayList<ProductDataNew>>

    @POST("woobox-api/api/v1/woocommerce/get-offer-product")
    fun getOfferProducts(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ArrayList<ProductDataNew>>

    @GET("woobox-api/api/v1/woocommerce/get-category")
    fun getProductCategories(): Call<ArrayList<CategoryData>>

    @POST("woobox-api/api/v1/woocommerce/get-sub-category")
    fun getSubCategories(@Body request: RequestModel, @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ArrayList<CategoryData>>

    @GET("woobox-api/api/v1/blog/get-blog")
    fun getBlogs( @Query("page") page: Int): Call<ArrayList<Blog>>

    @GET("woobox-api/api/v1/customer/get-address")
    fun getAddress(@Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ArrayList<Address>>

    @POST("woobox-api/api/v1/customer/delete-address")
    fun deleteAddress(@Body request: RequestModel, @Header("Content-Type")type:String="application/json",@Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ResponseBody>


    @POST("woobox-api/api/v1/customer/add-address")
    fun addUpdateAddress(@Body request: Address, @Header("Content-Type")type:String="application/json", @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ResponseBody>

    @POST("woobox-api/api/v1/customer/save-profile-image")
    fun saveProfileImage(@Body request: RequestModel, @Header("Content-Type")type:String="application/json", @Header("token") token: String = getApiToken(), @Header("id") id: String = getUserId()): Call<ProfileImage>

    @GET("woobox-api/api/v1/woocommerce/get-product-details")
    fun listSingleProduct(@Query("product_id") product_id: Int): Call<ArrayList<ProductModelNew>>

}
