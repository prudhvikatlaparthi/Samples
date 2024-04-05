package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetAdjustmentTypeResponse(
    @SerializedName("Results")
    var results: INVAdjustmentTypesResponse? = null,
    @SerializedName("TotalSearchedRecords")
    var totalSearchedRecords: Int = 0
)