package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetFormSpecs4AssetPrecheckData(
        val context:SecurityContext = SecurityContext(),
        @SerializedName("assetrentid")
        var assetRentId:Int?=0,
        @SerializedName("assetcatid")
        var assetCategoryId:Int?=0
)