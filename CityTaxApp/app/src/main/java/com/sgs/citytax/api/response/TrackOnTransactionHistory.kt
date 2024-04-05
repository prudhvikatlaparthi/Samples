package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class TrackOnTransactionHistory(
        @SerializedName("Results")
        var results: GetData? = null,
        @SerializedName("PageSize")
        var pageSize: Int? = 10,
        @SerializedName("PageIndex")
        var pageIndex: Int? = 1,
        @SerializedName("TotalRecords")
        var totalRecords: Int? = 0,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int? = null
)

data class GetData(
        @SerializedName("Data")
        var transactions: List<TrackOnTransaction>? = null
)