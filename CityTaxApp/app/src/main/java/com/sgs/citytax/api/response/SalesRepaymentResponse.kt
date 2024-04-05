package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class SalesRepaymentResponse(
    @SerializedName("TotalRecordsFound")
    var totalRecordsFound: Int? = null,
    @SerializedName("SearchResults")
    var salesListResults: List<SalesRepaymentItem>? = null,
    @SerializedName("PageSize")
    var pageSize: Int? = null,
    @SerializedName("PageIndex")
    var pageIndex: Int? = null
)