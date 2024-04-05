package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetParkingTicketsByVehicleNo(
        val context :SecurityContext = SecurityContext(),
        @SerializedName("fltr")
        var vehicleNo:String?="",
        @SerializedName("prkplcid")
        var parkingPlaceId:Int?=0,
        @SerializedName("inout")
        var inOut:String?=""
)