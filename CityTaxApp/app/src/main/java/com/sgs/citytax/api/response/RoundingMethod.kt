package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class RoundingMethod(
        /*{"COM_RoundingMethods":[{"RoundingMethod":"Round to Full Amount","Default":"Y","RoundingMethodID":1002,"RoundingPlace":0}*/
        @SerializedName("RoundingMethodID")
        var roundingMethodID: Int,
        @SerializedName("RoundingMethod")
        var roundingMethod: String,
        @SerializedName("RoundingPlace")
        var roundingPlace: Int
)