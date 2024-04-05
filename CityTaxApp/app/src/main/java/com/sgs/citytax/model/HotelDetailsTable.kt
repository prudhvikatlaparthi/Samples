package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class HotelDetailsTable(
        @SerializedName("HotelDetails")
        var hotelDetails:ArrayList<HotelDetails> = arrayListOf()
)