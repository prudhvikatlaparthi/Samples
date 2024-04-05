package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class SubscriptionRenewal(
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("amt")
        var amount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("SubscriptionModelID")
        var subscriptionModelID: Int? = 0,
        @SerializedName("RenewalDate")
        var renewalDate: String? = "",
        @SerializedName("Customer")
        var customer: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal?,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
        parcel.writeValue(amount)
        parcel.writeValue(subscriptionModelID)
        parcel.writeString(renewalDate)
        parcel.writeString(customer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionRenewal> {
        override fun createFromParcel(parcel: Parcel): SubscriptionRenewal {
            return SubscriptionRenewal(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionRenewal?> {
            return arrayOfNulls(size)
        }
    }
}