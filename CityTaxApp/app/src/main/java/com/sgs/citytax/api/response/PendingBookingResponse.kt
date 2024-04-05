package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PendingBookingsList

data class PendingBookingResponse(
        @SerializedName("PendingAssetBookingReq")
        var pendingBookings:ArrayList<PendingBookingsList> = arrayListOf()
)