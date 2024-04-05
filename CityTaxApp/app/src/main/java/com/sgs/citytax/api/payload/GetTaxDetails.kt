package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerId: Int? = null,
        @SerializedName("licnsno")
        var licensesNumber: String? = ""
)
