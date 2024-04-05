package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class SALWalletPaymentDetails(
        @SerializedName("MobileNo")
        var mobileNo: String? = "",
        @SerializedName("AgentAccountID")
        var agentAccountID: Int? =null,
        @SerializedName("OrgID")
        var orgId: Int? = 0,
        @SerializedName("amt")
        var amount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("OTP")
        var otp: Int? = 0,
        @SerializedName("refno")
        var referenceNo: String? = "",
        @SerializedName("TransactionID")
        var transactionId: String? = ""
)