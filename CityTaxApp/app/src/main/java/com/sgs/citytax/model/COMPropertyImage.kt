package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMPropertyImage(
        @SerializedName("PropertyImageID")
        var propertyImageID: Int? = 0,
        @SerializedName("proprtyid", alternate = ["PropertyID"])
        var propertyID: Int? = 0,
        @SerializedName("photo",alternate = ["Photo"])
        var photo: Int? = 0,
        @SerializedName("desc",alternate = ["Description"])
        var description: String? = "",
        @SerializedName("defntn",alternate = ["Default"])
        var default: String? = "",
        @SerializedName("design")
        var design: String? = "",
        @SerializedName("fileName")
        var fileName: String? = "",
        @SerializedName("fileNameWithExtension")
        var fileNameWithExtension: String? = "",
        @SerializedName("designFilePath")
        var designFilePath: String? = "",
        @SerializedName("isUpdateable")
        var isUpdatable: Boolean = true,
        @SerializedName("designSource")
        var data: String? = "",
        @SerializedName("AWSPath")
        var awsPath: String? = ""




) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readByte() != 0.toByte(),
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(propertyImageID)
                parcel.writeValue(propertyID)
                parcel.writeValue(photo)
                parcel.writeString(description)
                parcel.writeString(default)
                parcel.writeString(design)
                parcel.writeString(fileName)
                parcel.writeString(fileNameWithExtension)
                parcel.writeString(designFilePath)
                parcel.writeByte(if (isUpdatable) 1 else 0)
                parcel.writeString(data)
                parcel.writeString(awsPath)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<COMPropertyImage> {
                override fun createFromParcel(parcel: Parcel): COMPropertyImage {
                        return COMPropertyImage(parcel)
                }

                override fun newArray(size: Int): Array<COMPropertyImage?> {
                        return arrayOfNulls(size)
                }
        }
}