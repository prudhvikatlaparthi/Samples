package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.LastParkingTicketDetails
import com.sgs.citytax.model.OverstayChargeDetails

data class LastParkingAndOverStayChargeResponse(
        @SerializedName("LastParkingTicket")
        var lastParkingTicketDetails: LastParkingTicketDetails? = null,
        @SerializedName("OverstayCharge")
        var overStayChargeDetails: ArrayList<OverstayChargeDetails> = arrayListOf()
)