package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CRMAgents

data class AgentResponse(
        @SerializedName("Results")
        var agents: List<CRMAgents> = arrayListOf()
)