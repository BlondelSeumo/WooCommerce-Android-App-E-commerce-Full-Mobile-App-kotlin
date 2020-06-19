package com.iqonic.woobox.utils.oauthInterceptor

interface TimestampService {
    val timestampInSeconds: String
    val nonce: String
}