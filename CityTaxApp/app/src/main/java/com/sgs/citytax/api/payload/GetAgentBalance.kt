package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAgentBalance(
        @SerializedName("acctid")
        var accountID: Int = 0,
        var context: SecurityContext = SecurityContext()
)
