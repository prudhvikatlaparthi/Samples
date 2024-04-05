package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAssetDurationDistancePrice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("assetid")
        var assetId:Int?=0,
        @SerializedName("assetcatid")
        var assetCategoryId:Int?=0,
        @SerializedName("assetrenttypeid")
        var assetRentTypId:Int?=0,
        @SerializedName("tenureperiod")
        var tenurePeriod :Int?=null
)