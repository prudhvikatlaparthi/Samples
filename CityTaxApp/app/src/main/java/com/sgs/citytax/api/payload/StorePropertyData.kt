package com.sgs.citytax.api.payload

import android.os.Parcelable
import com.bumptech.glide.load.resource.bitmap.VideoDecoder.parcel
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.formatWithPrecision
import com.sgs.citytax.util.formatWithPrecisionCustomDecimals
import com.sgs.citytax.util.getString
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal


@Parcelize
data class StorePropertyData(
        @SerializedName("proprtyid", alternate = ["PropertyID"])
        var propertyID: Int? = null,
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = null,
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = null,
        @SerializedName("usrorgbrid", alternate = ["UserOrgBranchID"])
        var userOrgBranchID: Int? = null,
        @SerializedName("ParentPropertyID")
        var parentPropertyID: Int? = null,
        @SerializedName("PropertyCode")
        var propertyCode: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("unitCode", alternate = ["unitcode"])
        var unitCode: String? = "",
        @SerializedName("Area", alternate = ["area"])
        var area:BigDecimal= BigDecimal.ZERO,
        @SerializedName("MapArea")
        var mapArea: BigDecimal= BigDecimal.ZERO,
        @SerializedName("GeoLocationArea")
        var geoLocationArea: String? = "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("PropertySycotaxID")
        var propertySycotaxID: String? = "",
        @SerializedName("LandPropertyID")
        var landPropertyID: Int? = null,
        @SerializedName("ElectricityConsumptionID")
        var electricityConsumptionID: Int? = null,
        @SerializedName("PhaseOfElectricityID")
        var phaseOfElectricityID: Int? = null,
        @SerializedName("WaterConsumptionID")
        var waterConsumptionID: Int? = null,
        @SerializedName("ComfortLevelID")
        var comfortLevelID: Int? = null,
        @SerializedName("LandUseTypeID")
        var landUseTypeID: Int? = null,
        @SerializedName("MonthlyRentAmount")
        var monthlyRentAmount: BigDecimal= BigDecimal.ZERO,
        @SerializedName("EstimatedRentAmount")
        var estimatedRentAmount: BigDecimal= BigDecimal.ZERO,
        @SerializedName("SurveyNo")
        var surveyNo: String? = "",
        @SerializedName("regno", alternate = ["RegistrationNo"])
        var registrationNo: String? = "",
        @SerializedName("NoOfFloors")
        var noOfFloors: Int? = null,
        @SerializedName("BuiltUpAreaPerFloor")
        var builtUpAreaPerFloor: Double = 0.0,
        @SerializedName("TotalBuiltUpArea")
        var totalBuiltUpArea: Double = 0.0,
        @SerializedName("OpenSpace")
        var openSpace: Double = 0.0,
        @SerializedName("NoOfHousesPerFloor")
        var noOfHousesPerFloor: Int? = null,
        @SerializedName("NoOfEstablishmentsPerFloor")
        var noOfEstablishmentsPerFloor: Int? = null,
        @SerializedName("NoOfParkingPlaces")
        var noOfParkingPlaces: Int? = null,
        @SerializedName("Length")
        var length: BigDecimal= BigDecimal.ZERO,
        @SerializedName("wdth", alternate = ["Width"])
        var width: BigDecimal= BigDecimal.ZERO,
//        @SerializedName("createdByAccountID")
//        var createdByAccountID: Int? = null,
        @SerializedName("ConstructedDate")
        var constructedDate: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("GeoAddress")
        var geoAddress: GeoAddress? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("EstimatedLandArea")
        var estimatedLandArea: Double = 0.0,
        @SerializedName("PropertyRegistrationTypeID")
        var propertyRegistrationTypeID: Int? = null,
        @SerializedName("EstimatedTax")
        var estimatedTax: Double = 0.0,
        @SerializedName("IsInvoiceGenerated")
        var isInvoiceGenerated: Boolean = false,
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = null,
        @SerializedName("PropertySplitCode")
        var propertySplitCode: String? = "",
        @SerializedName("IsApartment")
        var isApartment: String? = "",
        @SerializedName("FloorNo")
        var floorNo: Int? = null,
        @SerializedName("PropertyBuildTypeID")
        var propertyBuildTypeID: Int? = null,
        @SerializedName("PropertyValue")
        var propertyValue: BigDecimal= BigDecimal.ZERO,
        @SerializedName("PropertyBuildTypeCode")
        var propertyBuildTypeCode: String? = "",
        @SerializedName("PropertyBuildType")
        var propertyBuildType: String? = "",
        @SerializedName("EstimatedTax4LC")
        var propertyEstimateTax: Double = 0.0,
        @SerializedName("CurrentDue4LC")
        var propertyLCCurrentDue: Double? = -1.0,
        @SerializedName("TaxInvoiceID4LC")
        var taxInvoiceIDLC: Int? = null,
        @SerializedName("CurrentBillingCycleInvoiceCount")
        var currentBillingCycleInvoiceCount: Int? = null
) : Parcelable {

    fun landPropertyID(): String {
        return landPropertyID.toString()
    }

    fun mapArea(): String {
        return if (mapArea != null)
            mapArea.toString() + " " + getString(R.string.meter_square)
        else
            ""

    }

    fun propertyValue(): String = formatWithPrecisionCustomDecimals(propertyValue?.toString()?.trim() ?: "0", true, 2)

    fun electricityConsumptionID(): String {
        return electricityConsumptionID.toString()
    }

    fun phaseOfElectricityID(): String {
        return phaseOfElectricityID.toString()
    }

    fun waterConsumptionID(): String {
        return waterConsumptionID.toString()
    }

    fun comfortLevelID(): String {
        return comfortLevelID.toString()
    }

    fun landUseTypeID(): String {
        return landUseTypeID.toString()
    }

    fun monthlyRentAmount(): String {
        return formatWithPrecision(monthlyRentAmount, true)
    }

    fun estimatedRentAmount(): String {
        return formatWithPrecision(estimatedRentAmount, true)
    }

    fun estimatedLandArea(): String {
        return estimatedLandArea.toString()
    }

    fun noOfFloors(): String {
        return noOfFloors.toString()
    }

    fun floorno(): String {
        return floorNo.toString()
    }

    fun builtUpAreaPerFloor(): String {
        return builtUpAreaPerFloor.toString()
    }

    fun totalBuiltUpArea(): String {
        return totalBuiltUpArea.toString()
    }

    fun openSpace(): String {
        return openSpace.toString()
    }

    fun noOfHousesPerFloor(): String {
        return noOfHousesPerFloor.toString()
    }

    fun noOfEstablishmentsPerFloor(): String {
        return noOfEstablishmentsPerFloor.toString()
    }

    fun noOfParkingPlaces(): String {
        return noOfParkingPlaces.toString()
    }

    fun length(): String {
        return formatWithPrecisionCustomDecimals(length.toString(), false, 3)
    }

    fun width(): String {
        return formatWithPrecisionCustomDecimals(width.toString(), false, 3)
    }

    fun area(): String {
        return formatWithPrecisionCustomDecimals(area.toString(), false, 3)
    }

    fun constructedDate(): String {
        return displayFormatDate(constructedDate)
    }

    fun registrationDate(): String {
        return displayFormatDate(registrationDate)
    }

    fun estimatedTax(): String {
        return formatWithPrecision(estimatedTax)
    }

    fun propertyEstimatedTax(): String {
        return formatWithPrecision(propertyEstimateTax)
    }

}
