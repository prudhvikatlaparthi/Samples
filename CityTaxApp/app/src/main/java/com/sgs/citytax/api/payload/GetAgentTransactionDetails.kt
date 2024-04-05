package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAgentTransactionDetails(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("4rmdt")
        var fromDate: String? = null,
        @SerializedName("2dt")
        var toDate: String? = null,
        @SerializedName("acctid")
        var acctid: String? = null,
        @SerializedName("taxtypefilter")
        var taxtypefilter: String? = null,
        @SerializedName("pageindex")
        var pageindex: Int,
        @SerializedName("pagesize")
        val pageSize: Int = 10
)