package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.AssetBookingRequestHeader
import com.sgs.citytax.api.payload.AssetBookingRequestLine
import com.sgs.citytax.util.formatDateTimeInMillisecond
import com.sgs.citytax.util.getString

data class AssetBooking(
        @SerializedName("Head")
        var assetBookingRequestHeader: AssetBookingRequestHeader? = null,
        @SerializedName("Lines")
        var assetBookingRequestLines: ArrayList<AssetBookingRequestLine>? = arrayListOf(),
        @SerializedName("LineDetails")
        var assetBookingRequestLine: AssetBookingRequestLine?=null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(AssetBookingRequestHeader::class.java.classLoader),
            parcel.createTypedArrayList(AssetBookingRequestLine)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(assetBookingRequestHeader, flags)
        parcel.writeTypedList(assetBookingRequestLines)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetBooking> {
        override fun createFromParcel(parcel: Parcel): AssetBooking {
            return AssetBooking(parcel)
        }

        override fun newArray(size: Int): Array<AssetBooking?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return getString(R.string.booking_request_id) + " : " + assetBookingRequestHeader?.bookingRequestID + "\n" +
                getString(R.string.customer_name) + " : " + assetBookingRequestHeader?.customerName + "\n" +
                getString(R.string.booking_request_date) + " : " + formatDateTimeInMillisecond(assetBookingRequestHeader?.bookingRequestDate) + "\n" +
                getString(R.string.booking_estimated_amount) + " : " + assetBookingRequestHeader?.estimatedAmount + "\n" +
                getString(R.string.booking_security_deposit) + " : " + assetBookingRequestHeader?.netReceivable + "\n"
    }

}