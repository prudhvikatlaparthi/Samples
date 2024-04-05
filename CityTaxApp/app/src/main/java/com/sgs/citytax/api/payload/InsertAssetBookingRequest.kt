package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class InsertAssetBookingRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("bookingreq")
        var assetBookingRequestHeader: AssetBookingRequestHeader? = null,
        @SerializedName("bookingreqlines")
        var assetBookingRequestLines: ArrayList<AssetBookingRequestLine>? = arrayListOf()
)