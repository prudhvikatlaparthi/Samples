package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CreditBalanceResponse(
        @SerializedName("AgentBalance")
        var balance: Double? = 0.00,
        @SerializedName("Details")
        var transactions: List<CreditBalance>? = arrayListOf()
)