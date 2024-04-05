package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class BusinessTransactionHistory(
        @SerializedName("Results")
        var transactions: ArrayList<BusinessTransaction>? = arrayListOf(),
        @SerializedName("PageSize")
        var pageSize: Int? = 10,
        @SerializedName("PageIndex")
        var pageIndex: Int? = 1,
        @SerializedName("TotalRecords")
        var totalRecords: Int? = 0
)