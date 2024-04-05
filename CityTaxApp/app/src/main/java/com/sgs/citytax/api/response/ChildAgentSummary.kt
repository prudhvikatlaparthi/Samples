package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ChildAgentSummary(
        @SerializedName("AgentID")
        var agentID: Int? = 0,
        @SerializedName("AgentCode")
        var agentCode: String? = null,
        @SerializedName("AgentName")
        var agentName: String? = "",
        @SerializedName("AgentType")
        var agentType: String? = "",
        @SerializedName("CapAmount")
        var cashLimit: Double? = 0.0,
        @SerializedName("CollectionAmount")
        var cashCollected: Double? = 0.0,
        @SerializedName("dpstamt")
        var depositAmount: Double? = 0.0,
        @SerializedName("CashInHand")
        var cashInHand: Double? = 0.0
)
