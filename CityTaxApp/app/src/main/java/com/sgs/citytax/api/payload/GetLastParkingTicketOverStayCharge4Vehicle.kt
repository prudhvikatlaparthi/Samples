package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetLastParkingTicketOverStayCharge4Vehicle(
        val context : SecurityContext = SecurityContext(),
        @SerializedName("fltr")
        var filterString: String? = "",
        @SerializedName("prkplcid")
        var parkingPlaceId:Int?=0
)