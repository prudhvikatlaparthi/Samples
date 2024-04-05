package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionHistoryGenModel(
        @SerializedName("AdvRecvID")
        var advancerecievedid: Int? = 0,
        @SerializedName("vchrno")
        var voucherNo: String? = "",
        @SerializedName("dt")
        var date: String? = "",
        @SerializedName("custname")
        var name: String? = "",
        @SerializedName("amt")
        var amount: Double? = 0.00,
        @SerializedName("pmtmode")
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
        @SerializedName("SubTypeVoucherNo")
        var subTypeVoucherNo: Int? = null,
        @SerializedName("ChequeStatusCode")
        var chequeStatusCode: String? = "",
        @SerializedName("ChequeStatus")
        var chequeStatus: String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
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
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(advancerecievedid)
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
        parcel.writeValue(subTypeVoucherNo)
        parcel.writeString(chequeStatusCode)
        parcel.writeString(chequeStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransactionHistoryGenModel> {
        override fun createFromParcel(parcel: Parcel): TransactionHistoryGenModel {
            return TransactionHistoryGenModel(parcel)
        }

        override fun newArray(size: Int): Array<TransactionHistoryGenModel?> {
            return arrayOfNulls(size)
        }
    }
}