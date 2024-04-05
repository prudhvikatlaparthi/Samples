package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMZoneMaster(
        @SerializedName("ctyid")
        var cityID: Int? = 0,
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("zncode")
        var zoneCode: String? = "",
        @SerializedName("znid")
        var zoneID: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun toString(): String {
        return zone ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cityID)
        parcel.writeString(zone)
        parcel.writeString(zoneCode)
        parcel.writeValue(zoneID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMZoneMaster> {
        override fun createFromParcel(parcel: Parcel): COMZoneMaster {
            return COMZoneMaster(parcel)
        }

        override fun newArray(size: Int): Array<COMZoneMaster?> {
            return arrayOfNulls(size)
        }
    }
}