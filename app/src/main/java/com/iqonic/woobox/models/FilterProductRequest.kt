package com.iqonic.woobox.models

data class FilterProductRequest(var page: Int? = null, var brand: ArrayList<String>? = null, var category: ArrayList<Int>? = null, var color: ArrayList<String>? = null, var price: ArrayList<Int>? = null, var size: ArrayList<String>? = null)