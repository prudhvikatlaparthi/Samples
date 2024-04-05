package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMPhaseOfElectricity(
        @SerializedName("PhaseOfElectricity")
        var phaseOfElectricity: String? = "",
        @SerializedName("PhaseOfElectricityID")
        var phaseOfElectricityID: Int? = 0,
        @SerializedName("act")
        var active: String? = ""
) {
    override fun toString(): String {
        return phaseOfElectricity.toString()
    }
}