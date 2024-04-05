package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class AdvanceSearchFilterTask(
        @SerializedName("FilterColumns")
        var filterColumns: List<Any>? = arrayListOf(),
        @SerializedName("DateFilter")
        var dateFilter: List<Any>? = arrayListOf(),
        @SerializedName("PageIndex")
        var pageIndex: Int? = 0,
        @SerializedName("PageSize")
        var pageSize: Int? = 0,
        @SerializedName("TableDetails")
        var tableDetails: TableDetails? = null,
        @SerializedName("SortBy")
        var sortBy: List<Any>? = null
)