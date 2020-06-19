package com.iqonic.woobox.utils.extensions

import android.util.Log
import com.google.gson.Gson
import com.iqonic.woobox.BuildConfig
import com.iqonic.woobox.R
import com.iqonic.woobox.WooBoxApp.Companion.getAppInstance
import com.iqonic.woobox.network.RestApis
import com.iqonic.woobox.network.RetrofitClientFactory
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getRestApis(useSignature: Boolean = true): RestApis {
    return RetrofitClientFactory().getRetroFitClient(useSignature).create(RestApis::class.java)
}

fun <T> callApi(call: Call<T>, onApiSuccess: (T) -> Unit = {}, onApiError: (aError: String) -> Unit = {}, onNetworkError: () -> Unit = {}) {
    Log.d("api_calling", call.request().url().toString() + " " + bodyToString(call.request()))
    call.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        onApiSuccess(body)
                        logData(call.request(), Gson().toJson(body), response.raw().receivedResponseAtMillis() - response.raw().sentRequestAtMillis())
                    } else {
                        onApiError("Please try again later.")
                        logData(call.request(), "Response body is null", response.raw().receivedResponseAtMillis() - response.raw().sentRequestAtMillis(), true)
                    }
                }
                else -> {
                    val string = getJsonMsg(response.errorBody()!!)
                    onApiError(string)
                    logData(call.request(), string, response.raw().receivedResponseAtMillis() - response.raw().sentRequestAtMillis(), isError = true)
                }
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            if (!isNetworkAvailable()) {
                onNetworkError()
                logData(call.request(), getAppInstance().resources.getString(R.string.error_no_internet), isError = true)
            } else {
                if (!BuildConfig.DEBUG) {
                    onApiError("Please try again later.")
                } else {
                    if (t.message!=null){
                    onApiError(t.message!!)}
                }
                if (t.message!=null){
                logData(call.request(), t.message!!, isError = true)}
            }
        }
    })
}

fun bodyToString(request: Request?): String {
    try {
        val buffer = Buffer()
        if (request!!.body() != null) {
            request.body()!!.writeTo(buffer)
        } else
            return ""
        return buffer.readUtf8()
    } catch (e: Exception) {
        return "Request Body is Null"
    }
}

fun logData(request: Request, response: String, time: Long = 0L, isError: Boolean = false) {
    try {
        Log.d("api_headers", Gson().toJson(request.headers()))
        Log.d("api_response_arrived in", (time / 1000L).toString() + " second")
        Log.d("api_url", request.url().toString())
        Log.d("api_request", bodyToString(request))
        if (isError) {
            Log.e("api_response", response)
        } else {
            Log.d("api_response", response)
        }
        Log.d("api_", "------------------")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getJsonMsg(errorBody: ResponseBody): String {
    try {
        val jsonObject = JSONObject(errorBody.string().getHtmlString().toString())
        Log.d("api_", jsonObject.toString())
        return if (jsonObject.has("message")) {
            (jsonObject.getString("message"))
        } else {
            getAppInstance().getString(R.string.error_something_went_wrong)
        }
    } catch (e: JSONException) {
        if (BuildConfig.DEBUG) {
            return e.toString()
        }
        e.printStackTrace()
    }
    return getAppInstance().getString(R.string.error_something_went_wrong)
}

fun getErrorMessageByHttpCode(aHttpCode: Int): String {
    return when (aHttpCode) {
        400 -> "Bad Request"
        401 -> "Unauthorized"
        404 -> "URL Not Found"
        407 -> "Proxy Authentication Required"
        408 -> "Request Timeout"
        413 -> "Payload Too Large"
        414 -> "URI Too Long"
        440 -> "Session expire"
        504, 598, 599 -> "Server timeout"
        500 -> "Internal Server Error"
        else -> "Something went wrong"
    }
}
