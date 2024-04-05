package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.ParkingTicketPayloadData

data class InsertParkingTicket(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var data: ParkingTicketPayloadData? = null,
        @SerializedName("isfromapp")
        var isfromapp: Boolean? = true

)
