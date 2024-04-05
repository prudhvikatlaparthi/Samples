package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VUADMVehicleOwnership
import com.sgs.citytax.model.VehicleDetails

data class VehicleDetailsSearchOwnerResponse(
        @SerializedName("VehiclesDetails")
        var vehicleDetails:ArrayList<VUADMVehicleOwnership> = arrayListOf()
)