package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatDateTimeInMillisecond
import java.math.BigDecimal
import java.util.*

data class PaymentByWallet(
        @SerializedName("pmtmodecode")
        var paymentModeCode: String? = null,
        @SerializedName("txndt")
        var transactionDate: String = formatDateTimeInMillisecond(Date()),
        @SerializedName("amt")
        var amount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("wlltcode")
        var walletCode: String? = null,
        @SerializedName("TransactionID")
        var transactionID: Int = 0,
        @SerializedName("MobiTransactionID")
        var mobiTransactionID: String? = ""
)
