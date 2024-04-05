package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPropertyTaxDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("sycotaxid")
        var sycoTaxId: String? = ""
)
