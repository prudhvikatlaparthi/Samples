package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetMaintenanceTypes(
        @SerializedName("MaintenanceTypeID")
        var maintenancetypeID: Int? = 0,
        @SerializedName("MaintenanceType")
        var maintenanceType: String? = null,
        @SerializedName("act")
        var active: String? = null,
        @SerializedName("DurationFrequency")
        var durationFrequency: Int? = 0,
        @SerializedName("DistanceFrequency")
        var distanceFrequency: Int? = 0

) {
    override fun toString(): String {
        return maintenanceType.toString()
    }
}