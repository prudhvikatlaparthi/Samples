package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPropertyPlans(
        val context:SecurityContext = SecurityContext(),
        @SerializedName("id")
        var propertyID:Int?=0
)