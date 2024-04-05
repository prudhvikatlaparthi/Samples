package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class CancelTaxNotice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("invoiceid")
        var invoiceID: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = ""
)