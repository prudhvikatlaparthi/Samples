package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.HotelDetailsTable

data class HotelDetailsListResponse(
        @SerializedName("Results")
        var hotelDetailsTable: HotelDetailsTable? = null
)