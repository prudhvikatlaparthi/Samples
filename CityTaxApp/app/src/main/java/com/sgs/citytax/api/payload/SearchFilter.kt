package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class SearchFilter(
        @SerializedName("Query")
        var query: String? = "",
        @SerializedName("TableDetails")
        var tableDetails: TableDetails? = null,
        @SerializedName("FilterColumns")
        var filterColumns: List<Any>? = arrayListOf(),
        @SerializedName("IncludeInactive")
        var includeInactive: String? = "",
        @SerializedName("Sorty_By")
        var sortBy: String? = "",
        @SerializedName("Sort_Column")
        var sortColumn: String? = "",
        @SerializedName("PageIndex")
        var pageIndex: Int? = 0,
        @SerializedName("PageSize")
        var pageSize: Int? = 0
)