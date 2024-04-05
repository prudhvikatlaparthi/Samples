package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ParkingTicketDetails
import java.math.BigDecimal

data class ParkingTicketDetailsResponse(
        @SerializedName("ParkingTickets")
        var ticketDetails: ParkingTicketDetails? = null,
        @SerializedName("currentDue")
        var currentDue: Double? = 0.0,
        @SerializedName("NetReceivable")
        var netReceivable: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ReceivedAmount")
        var receivedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("IsPass")
        var isPass: String? = "",
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountId: Int? = 0,
        @SerializedName("ParkingTicketID")
        var parkingTicketId: Int? = 0
)