package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LAWViolatorTypes(
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("ViolatorType")
        var violatorType: String? = "",
        @SerializedName("desc")
        var description: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(violatorTypeCode)
        parcel.writeString(violatorType)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "$violatorType"
    }

    companion object CREATOR : Parcelable.Creator<LAWViolatorTypes> {
        override fun createFromParcel(parcel: Parcel): LAWViolatorTypes {
            return LAWViolatorTypes(parcel)
        }

        override fun newArray(size: Int): Array<LAWViolatorTypes?> {
            return arrayOfNulls(size)
        }
    }
}