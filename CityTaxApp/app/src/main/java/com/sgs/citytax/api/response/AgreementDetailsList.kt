package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AgreementDetailsList(
        @SerializedName("TotalRecordsFound")
        val totalRecordsFound : Int = 0,
        @SerializedName("SearchResults")
        val agreementResults: List<AgreementResultsList> ? = null,
        @SerializedName("PageSize")
        val pageSize : Int? = 0,
        @SerializedName("PageIndex")
        val pageIndex: Int? = 0
)
