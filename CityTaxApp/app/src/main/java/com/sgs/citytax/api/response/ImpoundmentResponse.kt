package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ImpoundmentResponse(
        @SerializedName("ImpoundmentID")
        var impoundmentID: Int? = 0,
        @SerializedName("InvoiceID")
        var invoiceID: Int? = 0
)
