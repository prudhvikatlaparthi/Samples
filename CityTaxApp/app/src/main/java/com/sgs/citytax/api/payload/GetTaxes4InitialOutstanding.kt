package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTaxes4InitialOutstanding(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("id")
        var propertyID: Int? = null
)