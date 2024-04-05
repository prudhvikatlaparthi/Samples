package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAgentCommissionBalance(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var accountId:Int?=0
)