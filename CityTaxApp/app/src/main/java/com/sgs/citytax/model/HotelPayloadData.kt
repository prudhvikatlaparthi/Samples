package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class HotelPayloadData(
        @SerializedName("HotelID")
        var hotelID: Int? = 0,
        @com.google.gson.annotations.SerializedName("orgzid")
        var organisationID: Int? = 0,
        @SerializedName("HotelName")
        var hotelName: String? = "",
        @SerializedName("StarID")
        var starId: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressId: Int? = 0,
        @SerializedName("NoOfRoom")
        var noOfRooms: Int? = 0,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("act")
        var active: String? = "Y",
        @SerializedName("desc")
        var description: String? = ""
)