package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAgentDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("agntAccountID")
        var agentAccountID: Int? = 0
)