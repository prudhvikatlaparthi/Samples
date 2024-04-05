package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMElectricityConsumption(
        @SerializedName("ElectricityConsumption")
        var electricityConsumption: String? = "",
        @SerializedName("ElectricityConsumptionID")
        var electricityConsumptionID: Int? = 0,
        @SerializedName("act")
        var active: String? = ""
) {
    override fun toString(): String {
        return electricityConsumption.toString()
    }
}