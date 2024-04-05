package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CRMAgentTransactionResponse(
        @SerializedName("AgentTransactions")
        var agentTransactionsList: MutableList<CRMAgentTransactionDetail>? = null,
        @SerializedName("TaxType")
        var taxTypes: List<TaxType>? = null,
        @SerializedName("TotalRecordCounts")
        var totalRecordsCount: Int? = null,
        @SerializedName("TotalCollectionAmount")
        var totalCollectionAmount: Double? = null
)

data class TaxType(
        @SerializedName("TaxType")
        var taxType: String? = null
)