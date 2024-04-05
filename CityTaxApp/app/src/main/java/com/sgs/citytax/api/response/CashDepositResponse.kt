package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CashDepositResponse(
        @SerializedName("Results")
        var results: Results? = null,
        @SerializedName("TotalRecordCounts")
        var totalRecordsCount: Int? = null
)