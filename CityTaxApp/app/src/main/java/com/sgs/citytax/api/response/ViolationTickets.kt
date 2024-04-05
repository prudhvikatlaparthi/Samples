package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.ViolationDetail

data class ViolationTickets(
        @SerializedName("ViolationDetails")
        var violationDetails: List<ViolationDetail>? = arrayListOf()
)