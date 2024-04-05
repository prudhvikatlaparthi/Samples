package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class SAL_TaxDetails(
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("occupancy")
        var occupancy: String? = null,
        @SerializedName("prod")
        var product: String? = null,
        @SerializedName("acctid")
        var AccountID: Int = 0,
        @SerializedName("CurrDue")
        var currentDue: BigDecimal = BigDecimal.ZERO,
        @SerializedName("CurrTaxInvNo")
        var currentTaxInvoiceNo: Int = 0,
        @SerializedName("PrevDue")
        var previousDue: BigDecimal = BigDecimal.ZERO,
        @SerializedName("TotalDue")
        var TotalDue: BigDecimal = BigDecimal.ZERO,
        @SerializedName("vchrno")
        var VoucherNo: Int,
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal = BigDecimal.ZERO,
        @SerializedName("TaxPayer")
        var taxPayer: TaxPayerDetails?,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("PenaltyDue")
        var penaltyDue: BigDecimal = BigDecimal.ZERO,
        @SerializedName("MinimumPayAmt")
        var minimumPayAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("showGenerateInvoice")
        var showGenerateInvoice: Boolean,
        @SerializedName("ChequeStatus")
        var chequeStatus: String? = "",
        @SerializedName("IsPaymentSettledByCheque")
        var isPaymentSettledByCheque: Boolean,
        @SerializedName("TaxTypeName")
        var taxTypeName: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("StartDateOfTax")
        var taxStartDate: String? = "",
        @SerializedName("TaxYear")
        var taxYear: Int? = 0,
        @SerializedName("TaxSubTypeName")
        var taxSubTypeName: String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readInt(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readInt(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readParcelable(TaxPayerDetails::class.java.classLoader),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as BigDecimal,
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productCode)
        parcel.writeString(occupancy)
        parcel.writeString(product)
        parcel.writeInt(AccountID)
        parcel.writeInt(currentTaxInvoiceNo)
        parcel.writeInt(VoucherNo)
        parcel.writeParcelable(taxPayer, flags)
        parcel.writeString(taxRuleBookCode)
        parcel.writeByte(if (showGenerateInvoice) 1 else 0)
        parcel.writeString(chequeStatus)
        parcel.writeByte(if (isPaymentSettledByCheque) 1 else 0)
        parcel.writeString(taxTypeName)
        parcel.writeString(propertyName)
        parcel.writeString(billingCycle)
        parcel.writeString(taxStartDate)
        parcel.writeInt(taxYear ?: 0)
        parcel.writeString(taxSubTypeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SAL_TaxDetails> {
        override fun createFromParcel(parcel: Parcel): SAL_TaxDetails {
            return SAL_TaxDetails(parcel)
        }

        override fun newArray(size: Int): Array<SAL_TaxDetails?> {
            return arrayOfNulls(size)
        }
    }

}