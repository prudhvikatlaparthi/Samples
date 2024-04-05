package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TicketHistory

data class DriverTicketHistory(
        @SerializedName("DriverTicketHistory")
        var ticketHistory: ArrayList<TicketHistory>? = arrayListOf()
)