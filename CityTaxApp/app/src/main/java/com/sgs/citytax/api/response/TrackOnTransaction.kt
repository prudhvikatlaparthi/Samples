package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TrackOnTransaction(
        @SerializedName("advrecdid")
        var advancerecievedid: Int? = 0,
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("invdt")
        var invoiceDate: String? = "",
        @SerializedName("ViolationType")
        var violationType: String? = "",
        @SerializedName("ImpoundmentType")
        var impoundmentType: String? = "",
        @SerializedName("vehno")
        var VehicleNo: String? = "",
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
        @SerializedName("Violator")
        var violator: String? = "",
        @SerializedName("vchrno")
        var voucherNo: String? = "",
        @SerializedName("dt")
        var date: String? = "",
        @SerializedName("custname")
        var name: String? = "",
        @SerializedName("amt")
        var amount: Double? = 0.00,
        @SerializedName("pmtmodecode")
        var paymentMode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
        @SerializedName("accttyp")
        var accountType: String? = "",
        @SerializedName("CollectionType")
        var collectionType: String? = "",
        @SerializedName("prodcode")
        var prodcode: String? = "",
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("txnamt")
        var transactionAmount: String? = "",
        @SerializedName("txndt")
        var transactionDate: String? = "",
        @SerializedName("AllowAutoReceiptPrint")
        var allowAutoReceiptPrint: String? = "",
        @SerializedName("AllowAutoNoticePrint")
        var allowAutoNoticePrint: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(advancerecievedid)
        parcel.writeValue(noticeReferenceNo)
        parcel.writeString(invoiceDate)
        parcel.writeString(violationType)
        parcel.writeString(impoundmentType)
        parcel.writeString(VehicleNo)
        parcel.writeString(vehicleOwner)
        parcel.writeString(violationDetails)
        parcel.writeString(impoundmentReason)
        parcel.writeString(violator)
        parcel.writeString(voucherNo)
        parcel.writeString(date)
        parcel.writeString(name)
        parcel.writeValue(amount)
        parcel.writeString(paymentMode)
        parcel.writeString(product)
        parcel.writeValue(accountID)
        parcel.writeString(sycoTaxID)
        parcel.writeString(accountType)
        parcel.writeString(collectionType)
        parcel.writeString(prodcode)
        parcel.writeByte(if (isLoading) 1 else 0)
        parcel.writeString(taxRuleBookCode)
        parcel.writeString(transactionAmount)
        parcel.writeString(transactionDate)
        parcel.writeString(allowAutoReceiptPrint)
        parcel.writeString(allowAutoNoticePrint)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrackOnTransaction> {
        override fun createFromParcel(parcel: Parcel): TrackOnTransaction {
            return TrackOnTransaction(parcel)
        }

        override fun newArray(size: Int): Array<TrackOnTransaction?> {
            return arrayOfNulls(size)
        }
    }
}