package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class Payload4SalesTax(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var payment4SalesTax: Payment4SalesTax? = null
)