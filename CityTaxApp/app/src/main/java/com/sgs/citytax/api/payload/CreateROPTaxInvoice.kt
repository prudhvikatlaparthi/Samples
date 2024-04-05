package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class CreateROPTaxInvoice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("vouchno")
        var voucherNo: Int = 0,
        @SerializedName("cash")
        var cashAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null
)
