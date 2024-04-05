package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.GetOutstandingWaiveOff

data class GetOutstandingWaiveOffResponse(
        @SerializedName("InitialOutstandingPenalties")
        var outstandingWaiveOff: ArrayList<GetOutstandingWaiveOff> = arrayListOf()
)