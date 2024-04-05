package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CitizenIdentityCard(
        @SerializedName("CitizenCardID")
        var cardID: Int? = null,
        @SerializedName("CitizenCardNo")
        var cardNo: String? = null,
        @SerializedName("conid")
        var contactID: Int? = null,
        @SerializedName("issdt")
        var issuedDate: String? = null,
        @SerializedName("DeliveryDate")
        var deliveryDate: String? = null,
        @SerializedName("act")
        var active: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(cardID)
        parcel.writeString(cardNo)
        parcel.writeValue(contactID)
        parcel.writeString(issuedDate)
        parcel.writeString(deliveryDate)
        parcel.writeString(active)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CitizenIdentityCard> {
        override fun createFromParcel(parcel: Parcel): CitizenIdentityCard {
            return CitizenIdentityCard(parcel)
        }

        override fun newArray(size: Int): Array<CitizenIdentityCard?> {
            return arrayOfNulls(size)
        }
    }
}