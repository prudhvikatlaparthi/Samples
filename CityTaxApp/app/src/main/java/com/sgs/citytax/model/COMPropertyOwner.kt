package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class COMPropertyOwner(
        @SerializedName("PropertyOwnershipID")
        var propertyOwnershipID: Int? = 0,
        @SerializedName("proprtyid")
        var proprtyId: Int? = 0,
        @SerializedName("photo")
        var photo: Int? = 0,
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("regno")
        var registrationNo: String? = "",
        @SerializedName("Owner")
        var owner: String? = "",
        @SerializedName("mob", alternate = ["OwnerMobile"])
        var phoneNumber: String? = "",
        @SerializedName("email", alternate = ["OwnerEmail"])
        var email: String? = "",
        @SerializedName("PropertyOwnerID")
        var propertyOwnerID: Int? = 0,
        @SerializedName("SycotaxID")
        var sycotaxID: String? = "",
        @SerializedName("CitizenID")
        var citizenID: String? = "",
        @SerializedName("OwnerCitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("OwnerCitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("PropertyOwnerIDSycoTax")
        var propertyOwnerIDSycoTax: String? = ""


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
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(propertyOwnershipID)
        parcel.writeValue(proprtyId)
        parcel.writeValue(photo)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
        parcel.writeString(registrationNo)
        parcel.writeString(owner)
        parcel.writeString(phoneNumber)
        parcel.writeString(email)
        parcel.writeValue(propertyOwnerID)
        parcel.writeString(sycotaxID)
        parcel.writeString(citizenID)
        parcel.writeString(citizenSycoTaxId)
        parcel.writeString(citizenCardNumber)
        parcel.writeString(propertyOwnerIDSycoTax)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<COMPropertyOwner> {
        override fun createFromParcel(parcel: Parcel): COMPropertyOwner {
            return COMPropertyOwner(parcel)
        }

        override fun newArray(size: Int): Array<COMPropertyOwner?> {
            return arrayOfNulls(size)
        }
    }
}