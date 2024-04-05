package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetChildAgentSummary(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("pageindex")
        var pageindex: Int,
        @SerializedName("pagesize")
        val pageSize: Int = 10
)