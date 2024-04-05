package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMSectors(
        @SerializedName("znid")
        var zoneId: Int? = 0,
        @SerializedName("SectorID")
        var sectorId: Int? = 0,
        @SerializedName("sec")
        var sector: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString())

    override fun toString(): String {
        return sector.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(zoneId)
        parcel.writeValue(sectorId)
        parcel.writeString(sector)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMSectors> {
        override fun createFromParcel(parcel: Parcel): COMSectors {
            return COMSectors(parcel)
        }

        override fun newArray(size: Int): Array<COMSectors?> {
            return arrayOfNulls(size)
        }
    }
}