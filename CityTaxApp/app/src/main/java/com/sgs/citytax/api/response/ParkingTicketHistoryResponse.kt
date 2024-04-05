package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ParkingTicket

data class ParkingTicketHistoryResponse(
        @SerializedName("ParkingTickets")
        var ticket: ParkingTicket? = null
)