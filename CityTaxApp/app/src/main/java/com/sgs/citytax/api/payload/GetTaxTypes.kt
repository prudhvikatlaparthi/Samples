package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxTypes(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("customerid")
        var customerID: String? = null
)