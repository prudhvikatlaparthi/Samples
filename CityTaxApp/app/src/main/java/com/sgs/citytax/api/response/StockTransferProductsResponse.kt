package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class StockTransferProductsResponse(
    @SerializedName("ProductList")
    var productList : List<SalesProductData>? = arrayListOf()
)
