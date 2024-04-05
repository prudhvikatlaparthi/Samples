package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PropertyTaxNoticeModel(
        @SerializedName("advrecdid")
        var advrecdid: String? = null,
        @SerializedName("TaxationYear")
        var TaxationYear: String? = null,
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("advdt")
        var advdt: String? = "",
        @SerializedName("refno")
        var refno: String? = "",
        @SerializedName("prodcode")
        var prodcode: String? = "",
        @SerializedName("prod")
        var prod: String? = "",
        @SerializedName("TaxSubType")
        var TaxSubType: String? = "",
        @SerializedName("pmtmode")
        var pmtmode: String? = "",
        @SerializedName("pmtmodecode")
        var pmtmodecode: String? = "",
        @SerializedName("WalletTransactionNo")
        var WalletTransactionNo: String? = "",
        @SerializedName("chqno")
        var chqno: String? = "",
        @SerializedName("chqdt")
        var chqdt: String? = "",
        @SerializedName("bnkname")
        var bnkname: String? = "",
        @SerializedName("ChequeNote")
        var ChequeNote: String? = "",
        @SerializedName("AmountOfTaxImposed")
        var AmountOfTaxImposed:  Double? = 0.0,
        @SerializedName("AmountofThisPayment")
        var AmountofThisPayment: Double? = 0.0,
        @SerializedName("TotalDeposit")
        var TotalDeposit: Double? = 0.0,
        @SerializedName("AmountPaidCurrentYear")
        var AmountPaidCurrentYear:  Double? = 0.0,
        @SerializedName("AmountPaidAnteriorYear")
        var AmountPaidAnteriorYear:  Double? = 0.0,
        @SerializedName("AmountPaidPreviousYear")
        var AmountPaidPreviousYear:  Double? = 0.0,
        @SerializedName("PenaltyPaid")
        var PenaltyPaid:  Double? = 0.0,
        @SerializedName("AmountDueCurrentYear")
        var AmountDueCurrentYear:  Double? = 0.0,
        @SerializedName("AmountDueAnteriorYear")
        var AmountDueAnteriorYear:  Double? = 0.0,
        @SerializedName("AmountDuePreviousYear")
        var AmountDuePreviousYear: Double? = 0.0,
        @SerializedName("PenaltyDue")
        var PenaltyDue:  Double? = 0.0,
        @SerializedName("GeneratedBy")
        var GeneratedBy: String? = "",
        @SerializedName("PrintCounts")
        var PrintCounts: Int = 0
):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
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
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(advrecdid)
        parcel.writeString(TaxationYear)
        parcel.writeValue(taxInvoiceId)
        parcel.writeString(advdt)
        parcel.writeString(refno)
        parcel.writeString(prodcode)
        parcel.writeString(prod)
        parcel.writeString(TaxSubType)
        parcel.writeString(pmtmode)
        parcel.writeString(pmtmodecode)
        parcel.writeString(WalletTransactionNo)
        parcel.writeString(chqno)
        parcel.writeString(chqdt)
        parcel.writeString(bnkname)
        parcel.writeString(ChequeNote)
        parcel.writeValue(AmountOfTaxImposed)
        parcel.writeValue(AmountofThisPayment)
        parcel.writeValue(TotalDeposit)
        parcel.writeValue(AmountPaidCurrentYear)
        parcel.writeValue(AmountPaidAnteriorYear)
        parcel.writeValue(AmountPaidPreviousYear)
        parcel.writeValue(PenaltyPaid)
        parcel.writeValue(AmountDueCurrentYear)
        parcel.writeValue(AmountDueAnteriorYear)
        parcel.writeValue(AmountDuePreviousYear)
        parcel.writeValue(PenaltyDue)
        parcel.writeString(GeneratedBy)
        parcel.writeInt(PrintCounts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PropertyTaxNoticeModel> {
        override fun createFromParcel(parcel: Parcel): PropertyTaxNoticeModel {
            return PropertyTaxNoticeModel(parcel)
        }

        override fun newArray(size: Int): Array<PropertyTaxNoticeModel?> {
            return arrayOfNulls(size)
        }
    }

}