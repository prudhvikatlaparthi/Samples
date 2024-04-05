package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class IsPropertySycoTaxAvailable(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("sycotaxid")
        var sycoTaxID: String? = ""
)