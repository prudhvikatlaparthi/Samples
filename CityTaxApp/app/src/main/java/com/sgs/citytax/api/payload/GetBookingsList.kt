package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetBookingsList(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("bkreqid")
        var bookingRequestID: Int? = 0,
        @SerializedName("bkreqlineid")
        var bookingRequestLineId:Int?=0,
        @SerializedName("isupdateSrch")
        var isAssetBookingUpdate: Boolean? = null
)
