package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LAWImpoundmentType(
        @SerializedName("ImpoundmentType")
        var impoundmentType: String? = "",
        @SerializedName("ImpoundmentTypeCode")
        var impoundmentTypeCode: String? = "",
        @SerializedName("ImpoundmentTypeID")
        var impoundmentTypeID: Int? = 0,
        @SerializedName("ApplicableOnVehicle")
        var applicableOnVehicle: String? = "",
        @SerializedName("ApplicableOnGoods")
        var applicableOnGoods: String? = "",
        @SerializedName("ApplicableOnAnimal")
        var applicableOnAnimal: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun toString(): String {
        return impoundmentType ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(impoundmentType)
        parcel.writeString(impoundmentTypeCode)
        parcel.writeValue(impoundmentTypeID)
        parcel.writeString(applicableOnVehicle)
        parcel.writeString(applicableOnGoods)
        parcel.writeString(applicableOnAnimal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LAWImpoundmentType> {
        override fun createFromParcel(parcel: Parcel): LAWImpoundmentType {
            return LAWImpoundmentType(parcel)
        }

        override fun newArray(size: Int): Array<LAWImpoundmentType?> {
            return arrayOfNulls(size)
        }
    }
}