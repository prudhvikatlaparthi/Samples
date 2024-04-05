package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class PropertyTaxNoticeResponse(
        @SerializedName("Results")
        var results: PropertyTaxNoticeListResponse? = null,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords : Int? = null
)