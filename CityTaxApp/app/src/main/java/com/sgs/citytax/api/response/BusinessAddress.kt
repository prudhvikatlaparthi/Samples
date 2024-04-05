package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.GeoAddress

data class BusinessAddress(
        @SerializedName("VU_COM_GeoAddresses")
        var businessOwner: ArrayList<GeoAddress> = arrayListOf()
)
