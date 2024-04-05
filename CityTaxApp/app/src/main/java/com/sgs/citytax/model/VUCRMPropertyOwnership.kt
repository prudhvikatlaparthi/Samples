package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class VUCRMPropertyOwnership(

        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("proprtyid")
        var propertyID: Int? = 0,
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = 0,
        @SerializedName("PropertyOwnershipID")
        var propertyOwnershipID: Int? = 0,
        @SerializedName("regno")
        var registrationNo: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("unitcode")
        var unitCode: String? = "",
        @SerializedName("area")
        var area: Int? = 0,
        @SerializedName("GeoLocationArea")
        var geoLocationArea: String? = "",
        @SerializedName("desc")
        var description: String? = "desc",
        @SerializedName("PropertyDescription")
        var propertyDescription: String? = "",
        @SerializedName("lat")
        var latitude: String? = "",
        @SerializedName("long")
        var longitude: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("GeoAddressType")
        var geoAddressType: String? = "",
        @SerializedName("AddressLabel")
        var addressLabel: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertyCode")
        var propertyCode: String? = "",
        @SerializedName("cntrycode")
        var countryCode: String? = "",
        @SerializedName("stid")
        var stateID: Int = 0,
        @SerializedName("ctyid")
        var cityID: Int = 0,
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("SectorID")
        var sectorID: Int = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0,
        @SerializedName("ParentPropertyID")
        var parentPropertyID: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readString(),
            source.readInt(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(accountID)
        writeValue(propertyID)
        writeValue(propertyTypeID)
        writeValue(propertyOwnershipID)
        writeString(registrationNo)
        writeString(fromDate)
        writeString(toDate)
        writeString(unitCode)
        writeValue(area)
        writeString(geoLocationArea)
        writeString(description)
        writeString(propertyDescription)
        writeString(latitude)
        writeString(longitude)
        writeString(doorNo)
        writeString(block)
        writeString(plot)
        writeString(zipCode)
        writeString(street)
        writeString(geoAddressType)
        writeString(addressLabel)
        writeString(propertyName)
        writeString(propertyCode)
        writeString(countryCode)
        writeInt(stateID)
        writeInt(cityID)
        writeString(zone)
        writeInt(sectorID)
        writeValue(userOrgBranchID)
        writeValue(parentPropertyID)
        writeValue(geoAddressID)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<VUCRMPropertyOwnership> = object : Parcelable.Creator<VUCRMPropertyOwnership> {
            override fun createFromParcel(source: Parcel): VUCRMPropertyOwnership = VUCRMPropertyOwnership(source)
            override fun newArray(size: Int): Array<VUCRMPropertyOwnership?> = arrayOfNulls(size)
        }
    }
}