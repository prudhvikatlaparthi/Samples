package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VehicleTicketHistoryDetails

data class VehicleTicketHistoryResponse(
        @SerializedName("TicketHistory")
        var ticketHistoryDetails:ArrayList<VehicleTicketHistoryDetails> = arrayListOf()
)