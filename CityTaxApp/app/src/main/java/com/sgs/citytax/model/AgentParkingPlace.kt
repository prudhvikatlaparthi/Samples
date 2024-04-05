package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AgentParkingPlace(
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("parking")
        var parking: String? = ""
) {
    override fun toString(): String {
        return parking ?: ""
    }
}