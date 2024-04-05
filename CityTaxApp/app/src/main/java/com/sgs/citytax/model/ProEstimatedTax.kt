package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ProEstimatedTax(
        @SerializedName("acctid")
        var acctid: Int? = null,
        @SerializedName("ComfortLevelID")
        var comfortLevelID: Int? = null,
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = null,
        @SerializedName("LandUseTypeID")
        var landUseTypeID: Int? = null,
        @SerializedName("SectorID")
        var sectorID: Int? = null,
        @SerializedName("EstimatedRentAmount")
        var estimatedRentAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AgeOfProperty")
        var ageOfProperty: Int? = null,
        @SerializedName("strtdt")
        var strtdt: String = "",
        @SerializedName("ctyid")
        var ctyid: Int? = null,
        @SerializedName("EstimatedLandArea")
        var estimatedLandArea: BigDecimal? = BigDecimal.ZERO
)