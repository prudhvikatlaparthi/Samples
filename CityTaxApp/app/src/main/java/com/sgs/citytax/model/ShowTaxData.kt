package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ShowTaxData(
        @SerializedName("ShowID")
        var showID: Int? = 0,
        @SerializedName("orgzid")
        var organizationId: Int? = 0,
        @SerializedName("ShowName")
        var showName: String? = "",
        @SerializedName("OperatorTypeID")
        var operatorTypeId: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressId: Int? = 0,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("desc")
        var description: String? = ""
)