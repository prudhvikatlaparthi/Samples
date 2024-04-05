package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MobiCashPayment(
        @SerializedName("message")
        var message: String? = null,
        @SerializedName("mpin")
        var mpin: Int? = 0,
        @SerializedName("mobicashTxnid")
        var transactionID: String? = null,
        @SerializedName("status")
        var status: String? = null,
        @SerializedName("request-id")
        var requestId: String? = null,
        @SerializedName("trans-id")
        var transId: String? = null

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeValue(mpin)
        parcel.writeString(transactionID)
        parcel.writeString(status)
        parcel.writeString(requestId)
        parcel.writeString(transId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MobiCashPayment> {
        override fun createFromParcel(parcel: Parcel): MobiCashPayment {
            return MobiCashPayment(parcel)
        }

        override fun newArray(size: Int): Array<MobiCashPayment?> {
            return arrayOfNulls(size)
        }
    }
}