package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BusinessTaxDueYearSummary(
        @SerializedName("Year")
        var year: Int? = 0,
        @SerializedName("ProductCode", alternate = ["prodcode"])
        var productCode: String? = "",
        @SerializedName("Product", alternate = ["prod"])
        var product: String? = "",
        @SerializedName("VoucherNo", alternate = ["vchrno"])
        var voucherNo: Int? = 0,
        @SerializedName("TaxSubType")
        var taxSubType: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("InvoiceAmount")
        var invoiceAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("InvoiceDue")
        var invoiceDue: BigDecimal = BigDecimal.ZERO,
        @SerializedName("PenaltyAmount")
        var penaltyAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("PenaltyDue")
        var penaltyDue: BigDecimal = BigDecimal.ZERO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal,
            parcel.readSerializable() as BigDecimal)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(year)
        parcel.writeString(productCode)
        parcel.writeString(product)
        parcel.writeValue(voucherNo)
        parcel.writeString(taxSubType)
        parcel.writeString(taxRuleBookCode)
        parcel.writeSerializable(invoiceAmount)
        parcel.writeSerializable(invoiceDue)
        parcel.writeSerializable(penaltyAmount)
        parcel.writeSerializable(penaltyDue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BusinessTaxDueYearSummary> {
        override fun createFromParcel(parcel: Parcel): BusinessTaxDueYearSummary {
            return BusinessTaxDueYearSummary(parcel)
        }

        override fun newArray(size: Int): Array<BusinessTaxDueYearSummary?> {
            return arrayOfNulls(size)
        }
    }

}