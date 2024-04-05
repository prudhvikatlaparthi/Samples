package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class BusinessDueSummaryResults(
        @SerializedName("BusinessDueSummary", alternate = ["summary"])
        val businessDueSummary: ArrayList<BusinessDueSummary> = arrayListOf()
)