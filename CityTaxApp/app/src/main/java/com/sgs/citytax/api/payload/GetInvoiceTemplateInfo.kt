package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetInvoiceTemplateInfo(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("invoiceid")
        var invoiceId: Int? = null,
        @SerializedName("advreceivedid")
        var advanceReceivedId: Int? = null,
        @SerializedName("recptcode")
        var receiptcode: String? = null
)