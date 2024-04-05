package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class BusinessResponse(
        @SerializedName("BussinessDetails")
        var businessOwner: ArrayList<Business>? = arrayListOf()
)