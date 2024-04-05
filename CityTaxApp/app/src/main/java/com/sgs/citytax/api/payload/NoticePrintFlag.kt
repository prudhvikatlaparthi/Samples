package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class NoticePrintFlag(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("taxinvoiceid")
        var taxInvoiceId: Int? = null,
        @SerializedName("prodcode")
        var prodcode: String? = ""
)