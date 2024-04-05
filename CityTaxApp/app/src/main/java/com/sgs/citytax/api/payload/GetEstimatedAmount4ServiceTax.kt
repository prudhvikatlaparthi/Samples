package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetEstimatedAmount4ServiceTax(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("subtypeid")
        var subTypeID: Int? = 0,
        @SerializedName("applicableval")
        var area: Double? = 0.0
)