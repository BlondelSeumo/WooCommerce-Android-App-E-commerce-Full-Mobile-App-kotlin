package com.iqonic.woobox.models

data class ProductAttributeResponse(val brands: List<BrandX>, val categories: List<CategoryX>, val colors: List<ColorX>, val sizes: List<SizeX>)

data class ColorX(val count: Int, val description: String, val filter: String, val name: String, val parent: Int, val slug: String, val taxonomy: String, val term_group: Int, val term_id: Int, val term_taxonomy_id: Int)

data class BrandX(val count: Int, val description: String, val filter: String, val name: String, val parent: Int, val slug: String, val taxonomy: String, val term_group: Int, val term_id: Int, val term_taxonomy_id: Int)

data class SizeX(val count: Int, val description: String, val filter: String, val name: String, val parent: Int, val slug: String, val taxonomy: String, val term_group: Int, val term_id: Int, val term_taxonomy_id: Int)

data class CategoryX(val cat_ID: Int, val cat_name: String, val category_count: Int, val category_description: String, val category_nicename: String, val category_parent: Int, val count: Int, val description: String, val filter: String, val name: String, val parent: Int, val slug: String, val taxonomy: String, val term_group: Int, val term_id: Int, val term_taxonomy_id: Int)