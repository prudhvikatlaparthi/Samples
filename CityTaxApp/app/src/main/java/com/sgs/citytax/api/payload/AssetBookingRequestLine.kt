package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.GeoAddress
import java.math.BigDecimal
import java.util.*

data class AssetBookingRequestLine(
        @SerializedName("BookingRequestLineID")
        var bookingRequestLineID: Int? = 0,
        @SerializedName("BookingRequestID")
        var bookingRequestID: Int? = 0,
        @SerializedName("AssetCategoryID")
        var assetCategoryID: Int? = 0,
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("BookingQuantity")
        var bookingQuantity: Int? = 0,
        @SerializedName("Distance")
        var distance: Int? = 0,
        @SerializedName("BookingStartDate")
        var bookingStartDate: String? = "",
        @SerializedName("BookingEndDate")
        var bookingEndDate: String? = "",
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("DurationPaymentCycleID")
        var durationPaymentCycleID: Int? = 0,
        @SerializedName("DurationPricingRuleID")
        var durationPricingRuleID: Int? = 0,
        @SerializedName("DistancePricingRuleID")
        var distancePricingRuleID: Int? = 0,
        @SerializedName("DestinationGeoAddressID")
        var destinationGeoAddressID: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("BookingAdvance")
        var bookingAdvance: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("SecurityDeposit")
        var securityDeposit: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("EstimatedRentAmount")
        var estimatedRentAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("TenurePeriod")
        var tenurePeriod: Int? = 0,
        @SerializedName("assetrenttypeid", alternate = ["AssetRentTypeID"])
        var rentTypeID: Int? = 0,
        @SerializedName("cntrycode")
        var countryCode: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("stid", alternate = ["StateID"])
        var stateID: Int? = 0,
        @SerializedName("ctyid", alternate = ["CityID"])
        var cityID: Int? = 0,
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("SectorID")
        var sectorID: Int? = 0,
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("AllowMaintenance")
        var allowMaintenance: String? = "",
        @SerializedName("AllowInsurance")
        var allowInsurance: String? = "",
        @SerializedName("AllowFitness")
        var allowFitness: String? = "",
        @SerializedName("AllowRentBooking")
        var allowRentBooking: String? = "",
        @SerializedName("TrackOdometer")
        var trackOdometer: String? = "",
        @SerializedName("CheckListSpecificationSetID")
        var checkListSpecificationSetID: String? = "",
        @SerializedName("DesignFilePath")
        var designFilePath: String? = "",
        @SerializedName("IsUpdateable")
        var isUpdatable: Boolean? = false,
        @SerializedName("CustomProperties")
        var customProperties: String? = "",
        @SerializedName("DesignSource")
        var designSource: String? = "",
        @Expose(serialize = false, deserialize = false)
        @Transient
        var uniqueID: UUID? = UUID.randomUUID(),
        @SerializedName("AssetCategoryName")
        var assetCategory: String? = "",
        @SerializedName("GeoAddress")
        var geoAddress: GeoAddress? = null,
        @SerializedName("PickupGeoAddress")
        var pickupGeoAddress: GeoAddress? = null,
        @SerializedName("AssignQuantity")
        var assignQuantity: Int? = 0,
        @SerializedName("AssetNo")
        var assetNo: String? = "",
        @SerializedName("IsMovable")
        var isMovable: String? = "",
        @SerializedName("Length")
        var length: Int? = null,
        @SerializedName("Width")
        var width: Int? = null,
        @SerializedName("Area")
        var area: Int? = null,
        @SerializedName("AllowPeriodicInvoice")
        var allowPeriodicInvoice: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readSerializable() as? BigDecimal,
            parcel.readSerializable() as? BigDecimal,
            parcel.readSerializable() as? BigDecimal,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
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
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as UUID,
            parcel.readString(),
            parcel.readParcelable(GeoAddress::class.java.classLoader),
            parcel.readParcelable(GeoAddress::class.java.classLoader),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(), parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(bookingRequestLineID)
        parcel.writeValue(bookingRequestID)
        parcel.writeValue(assetCategoryID)
        parcel.writeValue(assetID)
        parcel.writeValue(bookingQuantity)
        parcel.writeValue(distance)
        parcel.writeString(bookingStartDate)
        parcel.writeString(bookingEndDate)
        parcel.writeString(startDate)
        parcel.writeValue(durationPaymentCycleID)
        parcel.writeValue(durationPricingRuleID)
        parcel.writeValue(distancePricingRuleID)
        parcel.writeValue(destinationGeoAddressID)
        parcel.writeValue(geoAddressID)
        parcel.writeSerializable(bookingAdvance)
        parcel.writeSerializable(securityDeposit)
        parcel.writeSerializable(estimatedRentAmount)
        parcel.writeValue(tenurePeriod)
        parcel.writeValue(rentTypeID)
        parcel.writeString(countryCode)
        parcel.writeString(state)
        parcel.writeString(city)
        parcel.writeValue(stateID)
        parcel.writeValue(cityID)
        parcel.writeString(zone)
        parcel.writeValue(sectorID)
        parcel.writeString(zipCode)
        parcel.writeString(street)
        parcel.writeString(plot)
        parcel.writeString(block)
        parcel.writeString(doorNo)
        parcel.writeString(allowMaintenance)
        parcel.writeString(allowInsurance)
        parcel.writeString(allowFitness)
        parcel.writeString(allowRentBooking)
        parcel.writeString(trackOdometer)
        parcel.writeString(checkListSpecificationSetID)
        parcel.writeString(designFilePath)
        parcel.writeValue(isUpdatable)
        parcel.writeString(customProperties)
        parcel.writeString(designSource)
        parcel.writeSerializable(uniqueID)
        parcel.writeString(assetCategory)
        parcel.writeParcelable(geoAddress, flags)
        parcel.writeParcelable(pickupGeoAddress, flags)
        parcel.writeValue(assignQuantity)
        parcel.writeString(assetNo)
        parcel.writeString(isMovable)
        parcel.writeValue(length)
        parcel.writeValue(width)
        parcel.writeValue(area)
        parcel.writeString(allowPeriodicInvoice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetBookingRequestLine> {
        override fun createFromParcel(parcel: Parcel): AssetBookingRequestLine {
            return AssetBookingRequestLine(parcel)
        }

        override fun newArray(size: Int): Array<AssetBookingRequestLine?> {
            return arrayOfNulls(size)
        }
    }
}