package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMStateMaster(
        @SerializedName("cntrycode")
        var countryCode: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("stid")
        var stateID: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun toString(): String {
        return state.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(countryCode)
        parcel.writeString(state)
        parcel.writeValue(stateID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMStateMaster> {
        override fun createFromParcel(parcel: Parcel): COMStateMaster {
            return COMStateMaster(parcel)
        }

        override fun newArray(size: Int): Array<COMStateMaster?> {
            return arrayOfNulls(size)
        }
    }
}