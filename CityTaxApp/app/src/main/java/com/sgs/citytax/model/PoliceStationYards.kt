package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PoliceStationYards(
        @SerializedName("PoliceStation")
        var policeStation: String? = "",
        @SerializedName("YardID")
        var yardID: Int,
        @SerializedName("YardCode")
        var yardCode: String? = "",
        @SerializedName("Yard")
        var yard: String? = ""
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun toString(): String {
        return yard ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(policeStation)
        parcel.writeInt(yardID)
        parcel.writeString(yardCode)
        parcel.writeString(yard)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PoliceStationYards> {
        override fun createFromParcel(parcel: Parcel): PoliceStationYards {
            return PoliceStationYards(parcel)
        }

        override fun newArray(size: Int): Array<PoliceStationYards?> {
            return arrayOfNulls(size)
        }
    }
}