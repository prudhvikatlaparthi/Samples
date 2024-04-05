package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class InsertROPDetails(
        @SerializedName("RightOfPlaceID")
        var rightOfPlaceID: String? = null,
        @SerializedName("orgzid")
        var organizationID: String? = null,
        @SerializedName("OccupancyID")
        var occupancyID: String? = null,
        @SerializedName("MarketID")
        var marketID: String? = null,
        @SerializedName("TaxableMatter")
        var taxableMatter: String? = null,
        @SerializedName("taxPeriod")
        var taxPeriod: String? = null,
        @SerializedName("Active")
        var active: String? = null,
        @SerializedName("Description")
        var description: String? = null,
        @SerializedName("Length")
        var length: String? = null,
        @SerializedName("Height")
        var height: String? = null,
        @SerializedName("Width")
        var width: String? = null,
        @SerializedName("strtdt")
        var startDate: String? = null

)
