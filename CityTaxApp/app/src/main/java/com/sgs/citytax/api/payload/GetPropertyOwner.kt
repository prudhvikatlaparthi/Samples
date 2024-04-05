package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPropertyOwner(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("propertyid")
        var propertyId:Int?=0
)