package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class PenalitySearchFilter(
        @SerializedName("Query")
        var query: String? = "",
        @SerializedName("FilterColumns")
        var filterColumns: List<Any>? = arrayListOf(),
        @SerializedName("TableDetails")
        var tableDetails: TableDetails? = null,
        @SerializedName("PageIndex")
        var pageIndex: Int? = 0,
        @SerializedName("PageSize")
        var pageSize: Int? = 0
)