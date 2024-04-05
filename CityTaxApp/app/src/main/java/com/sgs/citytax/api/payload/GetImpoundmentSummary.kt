package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetImpoundmentSummary (
        var context: SecurityContext = SecurityContext(),
        @SerializedName("impoundmentid")
        var impoundmentid: Int? = 0,
        @SerializedName("retlnID")
        var returnLineID: Int? = null
)