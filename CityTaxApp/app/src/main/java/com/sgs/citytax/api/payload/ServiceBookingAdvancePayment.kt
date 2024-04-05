package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class ServiceBookingAdvancePayment(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("advrecdid")
        var advanceReceivedId: Int? = 0,
        @SerializedName("ReceiptCode")
        var receiptCode: String? = null,
)