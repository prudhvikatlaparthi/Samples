package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ParkingTicket(
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("ParkingTicketID")
        var parkingTicketID: Int? = 0,
        @SerializedName("ParkingTicketDate")
        var parkingTicketDate: String? = "",
        @SerializedName("ParkingStartDate")
        var parkingStartDate: String? = "",
        @SerializedName("ParkingEndDate")
        var parkingEndDate: String? = "",
        @SerializedName("VehicleNo")
        var vehicleNo: String? = "",
        @SerializedName("ParkingPlace")
        var parkingPlace: String? = "",
        @SerializedName("NetReceivable")
        var netReceivable: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AccountName")
        var accountName: String? = "",
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ParkingType")
        var parkingType: String? = "",
        @SerializedName("Status")
        var status: String? = "" ,
        @SerializedName("StatusCode")
        var statusCode: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as BigDecimal?,
            parcel.readString(),
            parcel.readSerializable() as BigDecimal?,
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(noticeReferenceNo)
        parcel.writeValue(parkingTicketID)
        parcel.writeString(parkingTicketDate)
        parcel.writeString(parkingStartDate)
        parcel.writeString(parkingEndDate)
        parcel.writeString(vehicleNo)
        parcel.writeString(parkingPlace)
        parcel.writeSerializable(netReceivable)
        parcel.writeString(accountName)
        parcel.writeSerializable(currentDue)
        parcel.writeString(parkingType)
        parcel.writeString(status)
        parcel.writeString(statusCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParkingTicket> {
        override fun createFromParcel(parcel: Parcel): ParkingTicket {
            return ParkingTicket(parcel)
        }

        override fun newArray(size: Int): Array<ParkingTicket?> {
            return arrayOfNulls(size)
        }
    }
}