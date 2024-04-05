package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class PaymentByCash4AssetBooking(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerID: Int? = 0,
        @SerializedName("cashamt")
        var cashAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = ""
)