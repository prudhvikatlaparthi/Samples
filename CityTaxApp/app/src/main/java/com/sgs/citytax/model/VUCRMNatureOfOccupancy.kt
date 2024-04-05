package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMNatureOfOccupancy(
        @SerializedName("OccupancyName")
        var occupancyName: String? = null,
        @SerializedName("OccupancyID")
        var occupancyID: Int = 0,
        @SerializedName("TaxPeriod")
        var taxPeriod: Int = 0,
        @SerializedName("unitcode")
        var unitCode: String? = null

) {
    override fun toString(): String {
        return occupancyName.toString()
    }
}