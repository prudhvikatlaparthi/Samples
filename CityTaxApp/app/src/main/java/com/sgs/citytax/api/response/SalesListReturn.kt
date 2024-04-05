package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class SalesListReturn(
    @SerializedName("TotalRecordsFound")
    var totalRecordsFound: Int? = 0,
    @SerializedName("SalesAmount")
    var salesAmount: Double? = null,
    @SerializedName("SearchResults")
    var salesListResults: ArrayList<SalesListResults>? = arrayListOf(),
    @SerializedName("PageSize")
    var pageSize: Int? = 0,
    @SerializedName("PageIndex")
    var pageIndex: Int? = 0
)