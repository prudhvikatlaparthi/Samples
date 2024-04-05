package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class AgentSelfRecharge(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null,
        @SerializedName("makewalletpayment")
        var makewalletpayment: SALWalletPaymentDetails? = null,
        @SerializedName("rmks")
        var remarks: String? = ""
)