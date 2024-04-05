package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class ValidateAsset4Return(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("assetno")
        var assetNo:String?="",
        @SerializedName("bookingqty")
        var bookingQuantity:Int = 1
)