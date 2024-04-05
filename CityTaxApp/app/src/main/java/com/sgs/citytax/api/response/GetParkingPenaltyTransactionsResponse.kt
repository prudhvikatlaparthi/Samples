package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ParkingPenalties

data class GetParkingPenaltyTransactionsResponse(
        @SerializedName("Table")
        var penalties: ArrayList<ParkingPenalties> = arrayListOf()
)
