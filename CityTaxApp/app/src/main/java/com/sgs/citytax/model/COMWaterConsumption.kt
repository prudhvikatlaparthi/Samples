package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMWaterConsumption(
        @SerializedName("WaterConsumption")
        var waterConsumption: String? = "",
        @SerializedName("WaterConsumptionID")
        var waterConsumptionID: Int? = 0,
        @SerializedName("act")
        var active: String? = ""
) {
    override fun toString(): String {
        return waterConsumption.toString()
    }
}