package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName

data class InventoryProductsDetailsResponse(
    @SerializedName("ProductList")
    val productList: List<InventoryProductsDetails>? = null
)