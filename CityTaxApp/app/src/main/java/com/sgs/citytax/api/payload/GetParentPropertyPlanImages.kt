package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetParentPropertyPlanImages(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("propid")
        var propertyId:Int?=0,
        @SerializedName("NeedCount")
        var NeedCount:Boolean?=false
)