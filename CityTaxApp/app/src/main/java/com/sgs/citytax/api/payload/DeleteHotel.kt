package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteHotel(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("id")
        var hotelID: Int? = 0
)