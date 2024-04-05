package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StorePropertyOwnershipWithPropertyOwnerResponse(
        @SerializedName("propertyOwnershipID", alternate = ["PropertyOwnershipID"])
        var propertyOwnershipID: Int? = null,
        @SerializedName("proprtyid", alternate = ["propertyID"])
        var propertyID: Int? = null,
        @SerializedName("4rmdt", alternate = ["fromDate"])
        var fromDate: String? = "",
        @SerializedName("2dt", alternate = ["toDate"])
        var toDate: String? = null,
        @SerializedName("regno", alternate = ["registrationNo"])
        var registrationNo: String? = "",
        @SerializedName("Owners")
        var owners: String? = "",
        @SerializedName("PropertyExemptionReasonID")
        var propertyExemptionReasonID: Int? = null,
        @SerializedName("OwnersDetails")
        var propertyowners: ArrayList<PropertyOwnersDetailsResponse> = arrayListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(propertyOwnershipID)
        parcel.writeValue(propertyID)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
        parcel.writeString(registrationNo)
        parcel.writeString(owners)
        parcel.writeValue(propertyExemptionReasonID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StorePropertyOwnershipWithPropertyOwnerResponse> {
        override fun createFromParcel(parcel: Parcel): StorePropertyOwnershipWithPropertyOwnerResponse {
            return StorePropertyOwnershipWithPropertyOwnerResponse(parcel)
        }

        override fun newArray(size: Int): Array<StorePropertyOwnershipWithPropertyOwnerResponse?> {
            return arrayOfNulls(size)
        }
    }

}
