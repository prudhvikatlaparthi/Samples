package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMPropertyPlanImage(
        @SerializedName("PropertyPlanID")
        var propertyPlanId: Int? = 0,
        @SerializedName("proprtyid")
        var propertyID: Int? = 0,
        @SerializedName("Plan")
        var plan: Int? = 0,
        @SerializedName("refvchrno")
        var referenceVoucherNo: Int? = 0,
        @SerializedName("Design")
        var design: String? = "",
        @SerializedName("filename")
        var fileName: String? = "",
        @SerializedName("fileNameWithExtension")
        var fileNameWithExtension: String? = "",
        @SerializedName("isUpdateable")
        var isUpdatable: Boolean = true,
        @SerializedName("designSource")
        var data: String? = "",
        @SerializedName("defntn")
        var default: String? = "",
        @SerializedName("desc")
        var description: String? = "",
        @SerializedName("AWSPath")
        var awsPath: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(propertyPlanId)
        parcel.writeValue(propertyID)
        parcel.writeValue(plan)
        parcel.writeValue(referenceVoucherNo)
        parcel.writeString(design)
        parcel.writeString(fileName)
        parcel.writeString(fileNameWithExtension)
        parcel.writeByte(if (isUpdatable) 1 else 0)
        parcel.writeString(data)
        parcel.writeString(default)
        parcel.writeString(description)
        parcel.writeString(awsPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMPropertyPlanImage> {
        override fun createFromParcel(parcel: Parcel): COMPropertyPlanImage {
            return COMPropertyPlanImage(parcel)
        }

        override fun newArray(size: Int): Array<COMPropertyPlanImage?> {
            return arrayOfNulls(size)
        }
    }
}