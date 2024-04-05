package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VUAgentCashCollectionSummary

data class Results(
        @SerializedName("VU_ACC_CashDeposit", alternate = ["VU_AgentCashCollectionSummary"])
        val vuAgentCashCollectionSummaries: ArrayList<VUAgentCashCollectionSummary>? = arrayListOf()
)