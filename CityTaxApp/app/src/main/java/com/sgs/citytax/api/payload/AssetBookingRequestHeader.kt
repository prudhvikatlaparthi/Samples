package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AssetBookingRequestHeader(
        @SerializedName("BookingRequestID")
        var bookingRequestID: Int? = 0,
        @SerializedName("BookingRequestDate")
        var bookingRequestDate: String? = "",
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("netrec")
        var netReceivable: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("EstimatedAmount")
        var estimatedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("DesignFilePath")
        var designFilePath: String? = "",
        @SerializedName("IsUpdateable")
        var isUpdatable: Boolean? = false,
        @SerializedName("CustomProperties")
        var customProperties: String? = "",
        @SerializedName("DesignSource")
        var designSource: String? = "",
        @SerializedName("custname", alternate = ["acctname"])
        var customerName: String? = "",
        @SerializedName("PhoneNumber", alternate = ["mob"])
        var phoneNumber: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("recdamt")
        var receivedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("usrorgbrid")
        var userOrgId: Int? = 0 ,
        @SerializedName("AllowPeriodicInvoice")
        var allowPeriodicInvoice: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readSerializable() as BigDecimal?,
            parcel.readSerializable() as BigDecimal?,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as BigDecimal?,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(bookingRequestID)
        parcel.writeString(bookingRequestDate)
        parcel.writeValue(accountID)
        parcel.writeSerializable(netReceivable)
        parcel.writeSerializable(estimatedAmount)
        parcel.writeString(statusCode)
        parcel.writeString(designFilePath)
        parcel.writeValue(isUpdatable)
        parcel.writeString(customProperties)
        parcel.writeString(designSource)
        parcel.writeString(customerName)
        parcel.writeString(phoneNumber)
        parcel.writeString(email)
        parcel.writeSerializable(receivedAmount)
        parcel.writeValue(userOrgId)
        parcel.writeString(allowPeriodicInvoice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetBookingRequestHeader> {
        override fun createFromParcel(parcel: Parcel): AssetBookingRequestHeader {
            return AssetBookingRequestHeader(parcel)
        }

        override fun newArray(size: Int): Array<AssetBookingRequestHeader?> {
            return arrayOfNulls(size)
        }
    }
}