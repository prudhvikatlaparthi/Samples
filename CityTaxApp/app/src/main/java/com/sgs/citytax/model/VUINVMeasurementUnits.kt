package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUINVMeasurementUnits(
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("unitcode")
        var unitCode: String? = "",
        @SerializedName("BaseUnit")
        var baseUnit: String? = "",
        @SerializedName("factor")
        var factor: Double? = 0.00
) {
    override fun toString(): String {
        return unit.toString()
    }
}