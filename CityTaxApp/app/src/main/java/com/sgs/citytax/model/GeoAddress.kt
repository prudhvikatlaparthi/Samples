package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GeoAddress(
        @SerializedName("AccountAddressID")
        var addressID: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("GeoAddressType")
        var geoAddressType: String? = "",
        @SerializedName("cntrycode", alternate = ["CountryCode"])
        var countryCode: String? = "",
        @SerializedName("cntry", alternate = ["Country"])
        var country: String? = "",
        @SerializedName("stid", alternate = ["StateID"])
        var stateID: Int? = 0,
        @SerializedName("st", alternate = ["State"])
        var state: String? = "",
        @SerializedName("ctyid", alternate = ["CityID"])
        var cityID: Int? = 0,
        @SerializedName("cty", alternate = ["City"])
        var city: String? = "",
        @SerializedName("zn", alternate = ["Zone"])
        var zone: String? = "",
        @SerializedName("SectorID")
        var sectorID: Int? = 0,
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip", alternate = ["ZipCode"])
        var zipCode: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno", alternate = ["DoorNo"])
        var doorNo: String? = "",
        @SerializedName("lat", alternate = ["Latitude"])
        var latitude: String? = "",
        @SerializedName("long", alternate = ["Longitude"])
        var longitude: String? = "",
        @SerializedName("desc", alternate = ["description", "Description"])
        var description: String? = "",
        @SerializedName("sec", alternate = ["Sector"])
        var sector: String? = ""
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
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
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(addressID)
                parcel.writeValue(geoAddressID)
                parcel.writeValue(accountId)
                parcel.writeString(geoAddressType)
                parcel.writeString(countryCode)
                parcel.writeString(country)
                parcel.writeValue(stateID)
                parcel.writeString(state)
                parcel.writeValue(cityID)
                parcel.writeString(city)
                parcel.writeString(zone)
                parcel.writeValue(sectorID)
                parcel.writeString(street)
                parcel.writeString(zipCode)
                parcel.writeString(plot)
                parcel.writeString(block)
                parcel.writeString(doorNo)
                parcel.writeString(latitude)
                parcel.writeString(longitude)
                parcel.writeString(description)
                parcel.writeString(sector)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<GeoAddress> {
                override fun createFromParcel(parcel: Parcel): GeoAddress {
                        return GeoAddress(parcel)
                }

                override fun newArray(size: Int): Array<GeoAddress?> {
                        return arrayOfNulls(size)
                }
        }

}