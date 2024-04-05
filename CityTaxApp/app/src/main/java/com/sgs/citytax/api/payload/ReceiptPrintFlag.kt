package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class ReceiptPrintFlag(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("advrecdid")
        var advrecdId: Int? = null
)