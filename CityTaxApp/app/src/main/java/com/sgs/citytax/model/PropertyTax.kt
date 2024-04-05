package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.util.formatWithPrecisionCustomDecimals

data class PropertyTax(
        @SerializedName("proprtyid")
        var propertyId: Int? = 0,
        @SerializedName("PropertyTypeID")
        var propertyTypeId: Int? = 0,
        @SerializedName("PropertySycotaxID")
        var sycotaxID: String? = "",
        @SerializedName("regno")
        var registrationNumber: String? = "",
        @SerializedName("RegistrationDate")
        var regDate: String? = "",
        @SerializedName("ConstructedDate")
        var constructedDate: String? = "",
        @SerializedName("SurveyNo")
        var surveyNumber: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertyType")
        var propertyType: String? = "",
        @SerializedName("PropertyCode")
        var propertyCode: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("ElectricityConsumption")
        var electricityConsumption: String? = "",
        @SerializedName("WaterConsumption")
        var waterConsumption: String? = "",
        @SerializedName("PhaseOfElectricity")
        var phaseOfElectricity: String? = "",
        @SerializedName("ComfortLevel")
        var comfortLevel: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("NoOfFloors")
        var noOfFloors: Int? = 0,
        @SerializedName("TotalBuiltUpArea")
        var totalBuiltUpArea: Double? = 0.0,
        @SerializedName("OpenSpace")
        var openSpace: Int? = 0,
        @SerializedName("AreaType")
        var areaType: String? = "",
        @SerializedName("LandUseType")
        var landUseType: String? = "",
        @SerializedName("Length")
        var Length: Double? = null,
        @SerializedName("wdth")
        var wdth: Double? = null,
        @SerializedName("area")
        var area: Double? = null,
        @SerializedName("GeoLocationArea")
        var geoLocationArea: String? = "",
        @SerializedName("ParentGeoLocationArea")
        var parentGeoLocationArea: String? = "",
        @SerializedName("LandGeoLocationArea")
        var landGeoLocationArea: String? = "",
        @SerializedName("EstimatedRentAmount")
        var EstimatedRentAmount: Double? = null,
        @SerializedName("MonthlyRentAmount")
        var MonthlyRentAmount: Double? = null,
        @SerializedName("OnboardedBy")
        var onboardedBy: String? = "",
        @SerializedName("IsApartment")
        var isApartment: String? = "",
        @SerializedName("FloorNo")
        var floorNo: String? = "",
        @SerializedName("PropertyBuildType")
        var propertyBuildType: String? = "",
        @SerializedName("PropertyValue")
        var propertyValue: Double? = null,
        var documents: ArrayList<COMDocumentReference> = arrayListOf(),
        var propertyImages: ArrayList<COMPropertyImage> = arrayListOf(),
        var propertyPlans: ArrayList<COMPropertyPlanImage> = arrayListOf()
){
        fun propertyValue(): String = formatWithPrecisionCustomDecimals(propertyValue?.toString()?.trim() ?: "0", true, 2)
}