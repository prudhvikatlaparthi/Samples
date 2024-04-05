package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCOMExistingProperties(
        @SerializedName("proprtyid")
        var propertyID: Int? = 0,
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("PropertyCode")
        var propertyCode: String? = "",
        @SerializedName("unitcode")
        var unitCode: String? = "",
        @SerializedName("area")
        var area: Int? = 0,
        @SerializedName("GeoLocationArea")
        var geoLocationArea: String? = "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("act")
        var isActive: String? = "",
        @SerializedName("GeoAddress")
        var geoAddress: String? = "",
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0,
        @SerializedName("ParentPropertyName")
        var parentPropertyName: String? = "",
        @SerializedName("ParentPropertyID")
        var parentPropertyID: Int? = 0
) {
    override fun toString(): String {
        return propertyName.toString()
    }
}