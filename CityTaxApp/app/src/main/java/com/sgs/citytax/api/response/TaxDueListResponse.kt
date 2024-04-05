package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaxPayerList

data class TaxDueListResponse(
    @SerializedName("SearchResults")
    var searchResults: List<TaxDueList>? = null,
    @SerializedName("TotalRecordsFound")
    var totalRecordsFound: Int? = null
)


