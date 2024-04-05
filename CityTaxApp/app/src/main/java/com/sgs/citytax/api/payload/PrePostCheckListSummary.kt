package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class PrePostCheckListSummary(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("assetrentid")
        var assetRentId:Int?=0
)