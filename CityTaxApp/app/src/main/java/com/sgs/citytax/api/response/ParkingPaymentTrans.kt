package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.util.getString
import java.math.BigDecimal

data class ParkingPaymentTrans(
        @SerializedName("Rank")
        var rank: Int? = 0,
        @SerializedName("txntypcode")
        var transactiontypcode: String? = "",
        @SerializedName("TransactionNo")
        var transactionNo: Int? = 0,
        @SerializedName("txndt")
        var transactiondate: String? = "",
        @SerializedName("acctid")
        var accounttId: Int? = 0,
        @SerializedName("prodcode")
        var prodcode: String? = "",
        @SerializedName("amt")
        var amount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("MaxInstallmentNo")
        var maxInstallmentNo: Int? = 0,
        @SerializedName("MinInstalledAmount")
        var minInstalledAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("MinPayAmount")
        var minmumPayAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("typ")
        var type: String? = "",
        @SerializedName("prodtypcode")
        var prodtypcode: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("InvoiceTransactionTypeCode")
        var invoiceTransactionTypeCode: String? = "",
        @SerializedName("InvoiceTransactionVoucherNo")
        var invoiceTransactionVoucherNo: Int? = 0,
        @SerializedName("InvoiceTransactionVoucherDate")
        var invoiceTransactionVoucherDate: String? = "",
        @SerializedName("usrorgbrid")
        var usrorgbrid: Int? = 0,
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycotaxID: String? = "",
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountID: Int? = 0,
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("VehicleOwnerMobile")
        var vehicleOwnerMobile: String? = "",
        @SerializedName("VehicleOwnerEmail")
        var vehicleOwnerEmail: String? = "",
        @SerializedName("crtd")
        var created: String? = "",

        @SerializedName("ParkingTypeID")
        var parkingTypeID: Int = 0,
        @SerializedName("ParkingPlaceID")
        var parkingPlaceID: Int = 0,
        @SerializedName("ParentParkingType")
        var parentParkingType: String? = "",
        @SerializedName("ParkingType")
        var parkingType: String? = "",
        @SerializedName("ParkingPlace")
        var parkingPlace: String? = "",
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false

) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(rank)
        parcel.writeString(transactiontypcode)
        parcel.writeValue(transactionNo)
        parcel.writeString(transactiondate)
        parcel.writeValue(accounttId)
        parcel.writeString(prodcode)
        parcel.writeValue(maxInstallmentNo)
        parcel.writeString(type)
        parcel.writeString(prodtypcode)
        parcel.writeString(noticeReferenceNo)
        parcel.writeString(invoiceTransactionTypeCode)
        parcel.writeValue(invoiceTransactionVoucherNo)
        parcel.writeString(invoiceTransactionVoucherDate)
        parcel.writeValue(usrorgbrid)
        parcel.writeString(vehicleNo)
        parcel.writeString(vehicleSycotaxID)
        parcel.writeValue(vehicleOwnerAccountID)
        parcel.writeString(vehicleOwner)
        parcel.writeString(vehicleOwnerMobile)
        parcel.writeString(vehicleOwnerEmail)
        parcel.writeString(created)
        parcel.writeInt(parkingTypeID)
        parcel.writeInt(parkingPlaceID)
        parcel.writeString(parentParkingType)
        parcel.writeString(parkingType)
        parcel.writeString(parkingPlace)
        parcel.writeByte(if (isLoading) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParkingPaymentTrans> {
        override fun createFromParcel(parcel: Parcel): ParkingPaymentTrans {
            return ParkingPaymentTrans(parcel)
        }

        override fun newArray(size: Int): Array<ParkingPaymentTrans?> {
            return arrayOfNulls(size)
        }
    }
}


