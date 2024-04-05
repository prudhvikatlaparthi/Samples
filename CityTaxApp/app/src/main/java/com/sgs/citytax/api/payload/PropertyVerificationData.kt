package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PropertyVerificationData(
        @SerializedName("PropertyVerificationRequestID")
        var propertyVerificationRequestID: Int? = 0,
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = 0,
        @SerializedName("PropertyOwner")
        var propertyOwner: String? = "",
        @SerializedName("PropertySycotaxID")
        var propertySycotaxID: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(propertyVerificationRequestID)
        parcel.writeValue(propertyTypeID)
        parcel.writeString(propertyOwner)
        parcel.writeString(propertySycotaxID)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PropertyVerificationData> {
        override fun createFromParcel(parcel: Parcel): PropertyVerificationData {
            return PropertyVerificationData(parcel)
        }

        override fun newArray(size: Int): Array<PropertyVerificationData?> {
            return arrayOfNulls(size)
        }
    }
}