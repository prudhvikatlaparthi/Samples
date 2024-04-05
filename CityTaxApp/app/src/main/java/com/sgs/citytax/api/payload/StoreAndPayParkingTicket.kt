package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class StoreAndPayParkingTicket(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var data: StoreAndParkingTicketData? = null
)
