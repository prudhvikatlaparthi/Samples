package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AgentRechargeReceiptDetails

data class AgentRechargeReceiptResponse(
        @SerializedName("Table")
        var agentRechargeReceiptDetails: ArrayList<AgentRechargeReceiptDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)