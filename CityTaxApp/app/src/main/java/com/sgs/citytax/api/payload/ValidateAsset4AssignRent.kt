package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class ValidateAsset4AssignRent(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("assetno")
        var assetNo:String?="",
        @SerializedName("bookingqty")
        var bookingQuantity:Int?=0,
        @SerializedName("bklineid")
        var bookingLineId:Int?=0
)