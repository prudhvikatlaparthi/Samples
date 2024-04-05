package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxPayerList(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("fltr")
        var filterCondition: String? = ""

)