package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPropertyComfortLevels(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("ElectricityConsumptionID")
        var electricityConsumptionID: Int? = 0,
        @SerializedName("WaterConsumptionID")
        var waterConsumptionID: Int? = 0,
        @SerializedName("PhaseOfElectricityID")
        var phaseOfElectricityID: Int? = 0
)