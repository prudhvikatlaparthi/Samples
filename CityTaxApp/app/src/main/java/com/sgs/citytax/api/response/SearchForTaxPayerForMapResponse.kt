package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.SearchForTaxPayerForMapItem

class SearchForTaxPayerForMapResponse(
        @SerializedName("TotalRecordsFound")
        var TotalRecordsFound: Int? = null,
        @SerializedName("SearchResults")
        var SearchResults: List<SearchForTaxPayerForMapItem> = arrayListOf(),
        @SerializedName("PageSize")
        var PageSize: Int? = 10,
        @SerializedName("PageIndex")
        var PageIndex: Int? = 1,
        @SerializedName("ExecutionTime")
        var ExecutionTime: Int? = null
)