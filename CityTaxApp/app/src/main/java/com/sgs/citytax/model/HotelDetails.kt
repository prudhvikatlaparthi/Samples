package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class HotelDetails(
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("HotelID")
        var hotelId: Int? = 0,
        @SerializedName("orgzid")
        var organisationID: Int? = 0,
        @SerializedName("HotelName")
        var hotelName: String? = "",
        @SerializedName("StarID")
        var startId: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressId: Int? = 0,
        @SerializedName("NoOfRoom")
        var noOfRooms: Int? = 0,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("act")
        var active: String ?= "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("Star")
        var star: String? = "",
        @SerializedName("StarCode")
        var startCode: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("GeoAddress")
        var geoAddress: ArrayList<GeoAddress>? = arrayListOf(),
        @SerializedName("AllowDelete")
        var allowDelete: String? = "",
        @Transient
        var isLoading: Boolean = false,
        var documents :ArrayList<COMDocumentReference> = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(GeoAddress),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(accountID)
        parcel.writeValue(hotelId)
        parcel.writeValue(organisationID)
        parcel.writeString(hotelName)
        parcel.writeValue(startId)
        parcel.writeValue(geoAddressId)
        parcel.writeValue(noOfRooms)
        parcel.writeString(startDate)
        parcel.writeString(active)
        parcel.writeString(description)
        parcel.writeString(star)
        parcel.writeString(startCode)
        parcel.writeString(billingCycle)
        parcel.writeTypedList(geoAddress)
        parcel.writeString(allowDelete)
        parcel.writeByte(if (isLoading) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HotelDetails> {
        override fun createFromParcel(parcel: Parcel): HotelDetails {
            return HotelDetails(parcel)
        }

        override fun newArray(size: Int): Array<HotelDetails?> {
            return arrayOfNulls(size)
        }
    }
}