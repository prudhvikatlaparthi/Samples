package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PropertyOwnersDetailsResponse(
        @SerializedName("RelationshipType")
        var relationshipType: String? = "",
        @SerializedName("OwnerAccountName")
        var ownerAccountName: String? = "",
        @SerializedName("NomineeAccountName")
        var nomineeAccountName: String? = "",
        @SerializedName("cmbval")
        var cmbval: String? = "",
        @SerializedName("PropertyOwnerID")
        var propertyOwnerID: Int? = null,
        @SerializedName("PropertyOwnershipID")
        var propertyOwnershipID: Int? = null,
        @SerializedName("OwnerAccountID")
        var ownerAccountID: Int? = null,
        @SerializedName("NomineeAccountID")
        var nomineeAccountID: Int? = null
):Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(relationshipType)
                parcel.writeString(ownerAccountName)
                parcel.writeString(nomineeAccountName)
                parcel.writeString(cmbval)
                parcel.writeValue(propertyOwnerID)
                parcel.writeValue(propertyOwnershipID)
                parcel.writeValue(ownerAccountID)
                parcel.writeValue(nomineeAccountID)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<PropertyOwnersDetailsResponse> {
                override fun createFromParcel(parcel: Parcel): PropertyOwnersDetailsResponse {
                        return PropertyOwnersDetailsResponse(parcel)
                }

                override fun newArray(size: Int): Array<PropertyOwnersDetailsResponse?> {
                        return arrayOfNulls(size)
                }
        }

}