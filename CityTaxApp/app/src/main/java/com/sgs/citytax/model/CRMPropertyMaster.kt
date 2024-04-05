package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMPropertyMaster(
        @SerializedName("act")
        var active: String? = "Y",
        @SerializedName("Area")
        var area: Int? = 0,
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("UnitCode")
        var unitCode: String? = "",
        @SerializedName("GeoLocationArea")
        var geoLocationArea: String? = "",
        @SerializedName("ParentPropertyID")
        var parentPropertyID: Int? = 0,
        @SerializedName("PropertyCode")
        var propertyCode: String? = "",
        @SerializedName("proprtyid")
        var propertyID: Int? = 0,
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0
)