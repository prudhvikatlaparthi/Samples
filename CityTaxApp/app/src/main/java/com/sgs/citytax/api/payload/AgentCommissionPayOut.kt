package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class AgentCommissionPayOut(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("pageindex")
        var pageindex: Int? = 0,
        @SerializedName("pagesize")
        var pagesize: Int? = 0
)