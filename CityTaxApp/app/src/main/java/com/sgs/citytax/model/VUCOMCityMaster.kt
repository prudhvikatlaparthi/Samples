package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class VUCOMCityMaster(
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("ctyid")
        var cityID: Int? = 0,
        @SerializedName("stid")
        var stateID: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun toString(): String {
        return city.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        parcel.writeValue(cityID)
        parcel.writeValue(stateID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VUCOMCityMaster> {
        override fun createFromParcel(parcel: Parcel): VUCOMCityMaster {
            return VUCOMCityMaster(parcel)
        }

        override fun newArray(size: Int): Array<VUCOMCityMaster?> {
            return arrayOfNulls(size)
        }
    }
}

