package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ParkingInOutsData(
        @SerializedName("ParkingInOutID")
        var parkinIntOuID: Int? = 0,
        @SerializedName("ParkingTicketID")
        var parkingTicketID: Int? = 0,
        @SerializedName("InTime")
        var inTime: String? = null,
        @SerializedName("OutTime")
        var outTime: String? = ""
)