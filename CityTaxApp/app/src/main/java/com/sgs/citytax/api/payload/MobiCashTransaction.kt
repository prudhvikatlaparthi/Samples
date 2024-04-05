package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class MobiCashTransaction(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("agentacctid")
        var agentacctid: Int? = 0,
        @SerializedName("custacctid")
        var custacctid: Int? =null,
        @SerializedName("billamt")
        var billamt: BigDecimal = BigDecimal.ZERO,
        @SerializedName("rmks")
        var remarks: String? = null,
        @SerializedName("walletcode")
        var walletcode: String? = null,
        @SerializedName("mobile")
        var mobile: String? = null,
        @SerializedName("isoutboundtx")
        var isoutboundtx: Boolean? = false
)
