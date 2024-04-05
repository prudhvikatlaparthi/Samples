package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class StoreAgentDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("agent")
        var agent: Agent = Agent()
)