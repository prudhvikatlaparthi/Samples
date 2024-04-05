package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetChildPropertyCount4Property(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("proprtyid")
        var propertyId: Int? = 0
)