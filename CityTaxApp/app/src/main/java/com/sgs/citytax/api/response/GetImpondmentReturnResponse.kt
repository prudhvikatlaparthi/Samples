package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetImpondmentReturnResponse(
        @SerializedName("Results")
        var results: GetImpondmentReturnList? = null,
        @SerializedName("TotalRecords")
        var totalRecords: Int = 0
)