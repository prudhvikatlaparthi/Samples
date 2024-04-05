package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AgentParkingPlace

class AgentParkingPlaces {
    @SerializedName("AgentParkingPlacesList")
    var parkingPlaces: List<AgentParkingPlace>? = arrayListOf()
}
