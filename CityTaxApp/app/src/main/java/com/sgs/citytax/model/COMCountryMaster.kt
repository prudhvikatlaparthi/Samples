package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMCountryMaster(
        @SerializedName("cntrycode")
        var countryCode: String? = "",
        @SerializedName("cntry")
        var country: String? = "",
        @SerializedName("act")
        var active: String? = "",
        @SerializedName("telcode")
        var telephoneCode: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun toString(): String {
        return country.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(countryCode)
        parcel.writeString(country)
        parcel.writeString(active)
        parcel.writeValue(telephoneCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMCountryMaster> {
        override fun createFromParcel(parcel: Parcel): COMCountryMaster {
            return COMCountryMaster(parcel)
        }

        override fun newArray(size: Int): Array<COMCountryMaster?> {
            return arrayOfNulls(size)
        }
    }
}