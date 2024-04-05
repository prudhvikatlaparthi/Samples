package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class IsCitizenSycotaxAvailable(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("sycotaxid")
        var sycoTaxID: String? = ""
)