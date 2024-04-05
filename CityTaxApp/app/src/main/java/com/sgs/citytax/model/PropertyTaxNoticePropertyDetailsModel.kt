package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PropertyTaxNoticePropertyDetailsModel(
        @SerializedName("proprtyid")
        var proprtyid: Int? = null,
        @SerializedName("PropertyName")
        var PropertyName: String? = "",
        @SerializedName("PropertyTypeID")
        var PropertyTypeID: Int? = null,
        @SerializedName("GeoAddressID")
        var GeoAddressID: Int? = null,
        @SerializedName("usrorgbrid")
        var usrorgbrid: Int? = null,
        @SerializedName("ParentPropertyID")
        var ParentPropertyID: Int? = null,
        @SerializedName("PropertyCode")
        var PropertyCode: String? = "",
        @SerializedName("unitcode")
        var unitcode: String? = "",
        @SerializedName("area")
        var area: Double? = null,
        @SerializedName("GeoLocationArea")
        var GeoLocationArea: String? = "",
        @SerializedName("desc")
        var desc: String? = "",
        @SerializedName("act")
        var act: String? = "",
        @SerializedName("crtd")
        var crtd: String? = "",
        @SerializedName("crtddt")
        var crtddt: String? = "",
        @SerializedName("mdfd")
        var mdfd: String? = "",
        @SerializedName("mdfddt")
        var mdfddt: String? = "",
        @SerializedName("PropertySycotaxID")
        var PropertySycotaxID: String? = "",
        @SerializedName("LandPropertyID")
        var LandPropertyID: Int? = null,
        @SerializedName("ElectricityConsumptionID")
        var ElectricityConsumptionID: Int? = null,
        @SerializedName("PhaseOfElectricityID")
        var PhaseOfElectricityID: Int? = null,
        @SerializedName("WaterConsumptionID")
        var WaterConsumptionID: Int? = null,
        @SerializedName("ComfortLevelID")
        var ComfortLevelID: Int? = null,
        @SerializedName("LandUseTypeID")
        var LandUseTypeID: Int? = null,
        @SerializedName("MonthlyRentAmount")
        var MonthlyRentAmount: Double? = null,
        @SerializedName("EstimatedRentAmount")
        var EstimatedRentAmount: Double? = null,
        @SerializedName("SurveyNo")
        var SurveyNo: String? = "",
        @SerializedName("regno")
        var regno: String? = "",
        @SerializedName("NoOfFloors")
        var NoOfFloors: String? = null,
        @SerializedName("BuiltUpAreaPerFloor")
        var BuiltUpAreaPerFloor: Int? = null,
        @SerializedName("TotalBuiltUpArea")
        var TotalBuiltUpArea: Int? = null,
        @SerializedName("OpenSpace")
        var OpenSpace: Int? = null,
        @SerializedName("NoOfHousesPerFloor")
        var NoOfHousesPerFloor: Int? = null,
        @SerializedName("NoOfEstablishmentsPerFloor")
        var NoOfEstablishmentsPerFloor: Int? = null,
        @SerializedName("NoOfParkingPlaces")
        var NoOfParkingPlaces: Int? = null,
        @SerializedName("Length")
        var Length: Double? = null,
        @SerializedName("wdth")
        var wdth: Double? = null,
        @SerializedName("CreatedByAccountID")
        var CreatedByAccountID: Int? = null,
        @SerializedName("ConstructedDate")
        var ConstructedDate: String? = "",
        @SerializedName("stscode")
        var stscode: String? = "",
        @SerializedName("RegistrationDate")
        var RegistrationDate: String? = "",
        @SerializedName("sts")
        var sts: String? = "",
        @SerializedName("PropertyType")
        var PropertyType: String? = "",
        @SerializedName("prodcode")
        var prodcode: String? = "",
        @SerializedName("ElectricityConsumption")
        var ElectricityConsumption: String? = "",
        @SerializedName("WaterConsumption")
        var WaterConsumption: String? = "",
        @SerializedName("PhaseOfElectricity")
        var PhaseOfElectricity: String? = "",
        @SerializedName("ComfortLevel")
        var ComfortLevel: String? = "",
        @SerializedName("brname")
        var brname: String? = "",
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("TaxRuleBookCode")
        var TaxRuleBookCode: String? = "",
        @SerializedName("ParentProperty")
        var ParentProperty: String? = "",
        @SerializedName("LandProperty")
        var LandProperty: String? = "",
        @SerializedName("AreaType")
        var AreaType: String? = "",
        @SerializedName("LandUseType")
        var LandUseType: String? = "",
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("sec")
        var sector: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("Sector1")
        var sector1: String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(proprtyid)
        parcel.writeString(PropertyName)
        parcel.writeValue(PropertyTypeID)
        parcel.writeValue(GeoAddressID)
        parcel.writeValue(usrorgbrid)
        parcel.writeValue(ParentPropertyID)
        parcel.writeString(PropertyCode)
        parcel.writeString(unitcode)
        parcel.writeValue(area)
        parcel.writeString(GeoLocationArea)
        parcel.writeString(desc)
        parcel.writeString(act)
        parcel.writeString(crtd)
        parcel.writeString(crtddt)
        parcel.writeString(mdfd)
        parcel.writeString(mdfddt)
        parcel.writeString(PropertySycotaxID)
        parcel.writeValue(LandPropertyID)
        parcel.writeValue(ElectricityConsumptionID)
        parcel.writeValue(PhaseOfElectricityID)
        parcel.writeValue(WaterConsumptionID)
        parcel.writeValue(ComfortLevelID)
        parcel.writeValue(LandUseTypeID)
        parcel.writeValue(MonthlyRentAmount)
        parcel.writeValue(EstimatedRentAmount)
        parcel.writeString(SurveyNo)
        parcel.writeString(regno)
        parcel.writeString(NoOfFloors)
        parcel.writeValue(BuiltUpAreaPerFloor)
        parcel.writeValue(TotalBuiltUpArea)
        parcel.writeValue(OpenSpace)
        parcel.writeValue(NoOfHousesPerFloor)
        parcel.writeValue(NoOfEstablishmentsPerFloor)
        parcel.writeValue(NoOfParkingPlaces)
        parcel.writeValue(Length)
        parcel.writeValue(wdth)
        parcel.writeValue(CreatedByAccountID)
        parcel.writeString(ConstructedDate)
        parcel.writeString(stscode)
        parcel.writeString(RegistrationDate)
        parcel.writeString(sts)
        parcel.writeString(PropertyType)
        parcel.writeString(prodcode)
        parcel.writeString(ElectricityConsumption)
        parcel.writeString(WaterConsumption)
        parcel.writeString(PhaseOfElectricity)
        parcel.writeString(ComfortLevel)
        parcel.writeString(brname)
        parcel.writeString(unit)
        parcel.writeString(TaxRuleBookCode)
        parcel.writeString(ParentProperty)
        parcel.writeString(LandProperty)
        parcel.writeString(AreaType)
        parcel.writeString(LandUseType)
        parcel.writeString(zone)
        parcel.writeString(sector)
        parcel.writeString(plot)
        parcel.writeString(block)
        parcel.writeString(doorNo)
        parcel.writeString(street)
        parcel.writeString(zipCode)
        parcel.writeString(city)
        parcel.writeString(state)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PropertyTaxNoticePropertyDetailsModel> {
        override fun createFromParcel(parcel: Parcel): PropertyTaxNoticePropertyDetailsModel {
            return PropertyTaxNoticePropertyDetailsModel(parcel)
        }

        override fun newArray(size: Int): Array<PropertyTaxNoticePropertyDetailsModel?> {
            return arrayOfNulls(size)
        }
    }

}