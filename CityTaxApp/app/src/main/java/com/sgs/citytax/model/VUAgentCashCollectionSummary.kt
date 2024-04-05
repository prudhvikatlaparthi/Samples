package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VUAgentCashCollectionSummary(
        @SerializedName("AccountID")
        var accountID: Int? = 0,
        @SerializedName("AgentID")
        var agentId: Int? = 0,
        @SerializedName("AgentCode")
        var agentCode: String? = "",
        @SerializedName("AgentName")
        var agentName: String? = "",
        @SerializedName("AgentType")
        var agentType: String? = "",
        @SerializedName("CapAmount")
        var capAmount: Double? = 0.0,
        @SerializedName("CollectionAmount")
        var collectionAmount: Double? = 0.0,
        @SerializedName("DepositAmount")
        var depositAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CashInHand")
        var cashInHand: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("StatusCode")
        var statusCode: String? = "",
        @SerializedName("Status")
        var status: String? = "",
        @SerializedName("DepositDate")
        var depositDate: String? = "",
        @SerializedName("ProcessedDate")
        var processedDate: String? = "",
        @SerializedName("RequestDate")
        var requestDate: String? = ""
)