package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GeoFenceLatLong(

        @SerializedName("lat")
        val latitude: Double,
        @SerializedName("lng", alternate = ["long"])
        val longitude: Double) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble())

    fun mLatitude(): String {
        return "Lat: ${latitude} "
    }

    fun mLongitude(): String {
        return "Lng: ${longitude} "
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GeoFenceLatLong> {
        override fun createFromParcel(parcel: Parcel): GeoFenceLatLong {
            return GeoFenceLatLong(parcel)
        }

        override fun newArray(size: Int): Array<GeoFenceLatLong?> {
            return arrayOfNulls(size)
        }
    }
}