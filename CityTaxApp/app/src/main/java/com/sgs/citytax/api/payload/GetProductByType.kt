package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetProductByType(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("securitysal")
        var securitysal: String = "N"
)