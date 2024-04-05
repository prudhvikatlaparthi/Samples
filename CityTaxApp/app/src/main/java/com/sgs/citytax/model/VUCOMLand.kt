package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.util.getString

data class VUCOMLand(
        @SerializedName("proprtyid")
        var proprtyid: Int? = null,
        @SerializedName("PropertyCode")
        var propertyCode: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("GeoAddressID")
        var geoAddressID: String? = "",
        @SerializedName("SurveyNo")
        var surveyNo: String? = "",
        @SerializedName("regno")
        var registrationNo: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("PropertySycotaxID")
        var propertySycotaxID: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("ParentPropertyID")
        var parentPropertyID: String? = "",
        @SerializedName("LandPropertyID")
        var landPropertyID: String? = "",
        @SerializedName("GeoLocationArea")
        var geoLocationArea: String? = "",
        @SerializedName("PropertySplitCode")
        var propertySplitCode: String? = ""
) {
    override fun toString(): String {
        if (propertyName == getString(R.string.select)) {
            return "${propertyName.toString()} \n"
        }
        if (propertyName != null) {
            return "${propertySycotaxID.toString()} \n${propertyName.toString()}\n"
        } else {
            return "${propertySycotaxID.toString()} \n"
        }
    }
}