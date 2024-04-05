package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class StoreAndPaySubscriptionRenewal(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("usrid")
        var userID: String? = null,
        @SerializedName("subscriptionrenewal")
        var subscriptionRenewal: SubscriptionRenewal? = null,
        @SerializedName("custid")
        var customerID: Int? = 0,
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null,
        @SerializedName("makewalletpayment")
        var makewalletpayment: SALWalletPaymentDetails? = null,
        @SerializedName("rmks")
        var remarks: String? = ""

)