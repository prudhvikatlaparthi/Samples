package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CRMAgentSummaryDetails

data class AgentSummaryDetailsResponse(
        @SerializedName("Agents")
        var agents: List<CRMAgentSummaryDetails> = arrayListOf()
)