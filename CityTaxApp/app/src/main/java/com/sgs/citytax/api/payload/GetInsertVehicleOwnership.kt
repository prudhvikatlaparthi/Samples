package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.VehicleMaster
import com.sgs.citytax.model.VehicleOwnership

data class GetInsertVehicleOwnership(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var vehicleOwnership: VehicleOwnership
)
