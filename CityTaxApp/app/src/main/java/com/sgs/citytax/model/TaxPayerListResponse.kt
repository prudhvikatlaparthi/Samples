package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class TaxPayerListResponse(
        @SerializedName("SearchResults")
        var searchResults: List<TaxPayerList>? = null,
        @SerializedName("TotalRecordsFound")
        var totalRecordsFound: Int? = null
)