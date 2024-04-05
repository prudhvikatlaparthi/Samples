package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class MobiCashPaymentStatus(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("agentacctid")
        var agentAccountID: Int? = 0,
        @SerializedName("custacctid")
        var custAccountID: Int = 0,
        @SerializedName("txid")
        var transactionID: String? = null,
        @SerializedName("mpin")
        var mpin: Int = 0,
        @SerializedName("requestid")
        var requestid: String? = null
)