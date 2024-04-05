package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class TaxNoticePrintingDetails(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("invoiceid")
        var invoiceId: Int? = 0,
        @SerializedName("advreceivedid")
        var advanceReceivedId: Int? = 0,
        @SerializedName("recptcode")
        var receiptCode: String? = "",
        @SerializedName("sono")
        var salesOrderNo: Int? = null
)