package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class PaymentByWallet4AssetBooking(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerId: Int = 0,
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null,
        @SerializedName("makewalletpayment")
        var makewalletpayment: SALWalletPaymentDetails? = null,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = ""
)
