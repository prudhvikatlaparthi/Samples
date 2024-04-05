package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ADMVehicleTypes(
        @SerializedName("vehtyp")
        var vehicleType: String? = null,
        @SerializedName("vehtypcode")
        var vehicleTypeCode: String? = null,
        @SerializedName("act")
        var isActive: String? = null
) {
    override fun toString(): String {
        return vehicleType.toString()
    }
}