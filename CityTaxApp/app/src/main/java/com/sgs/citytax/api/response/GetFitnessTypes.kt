package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetFitnessTypes(
        @SerializedName("FitnessTypeID")
        var fitnessTypeID: Int? = 0,
        @SerializedName("FitnessType")
        var fitnessType: String? = null,
        @SerializedName("DurationFrequency")
        var durationFrequency: Int? = 0,
        @SerializedName("DistanceFrequency")
        var distanceFrequency: Int? = 0,
        @SerializedName("act")
        var active: String? = null


) {
    override fun toString(): String {
        return fitnessType.toString()
    }
}
