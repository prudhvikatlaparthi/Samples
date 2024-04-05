package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LAWImpoundmentSubType(
        @SerializedName("ImpoundmentSubType")
        var impoundmentSubType: String? = "",
        @SerializedName("ImpoundmentSubTypeCode")
        var impoundmentSubTypeCode: String? = "",
        @SerializedName("ImpoundmentSubTypeID")
        var impoundmentSubTypeID: Int? = 0,
        @SerializedName("ImpoundmentTypeID")
        var impoundmentTypeID: Int? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun toString(): String {
        return impoundmentSubType ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(impoundmentSubType)
        parcel.writeString(impoundmentSubTypeCode)
        parcel.writeValue(impoundmentSubTypeID)
        parcel.writeValue(impoundmentTypeID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LAWImpoundmentSubType> {
        override fun createFromParcel(parcel: Parcel): LAWImpoundmentSubType {
            return LAWImpoundmentSubType(parcel)
        }

        override fun newArray(size: Int): Array<LAWImpoundmentSubType?> {
            return arrayOfNulls(size)
        }
    }
}