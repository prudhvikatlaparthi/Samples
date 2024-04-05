package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GetSearchIndividualTaxDetails(
        @SerializedName("TypeName")
        var typeName : String ?= null,
        @SerializedName("SycotaxID")
        var sycotaxID : String ?= null,
        @SerializedName("ProductCode")
        var productCode : String ?= null,
        @SerializedName("Product")
        var product : String ?= null,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode : String ?= null,
        @SerializedName("MultiInvoice")
        var multiInvoice : String ?= null,
        @SerializedName("IsDefault")
        var isDefault : String ?= null,
        @SerializedName("VoucherNo")
        var voucherNo : String ?= null,
        @SerializedName("AccountID")
        var accountID : String ?= null,
        @SerializedName("PricingRuleID")
        var pricingRuleID : String ?= null,
        @SerializedName("PaymentCycleID")
        var paymentCycleID : String ?= null,
        @SerializedName("BillingCycleID")
        var billingCycleID : String ?= null,
        @SerializedName("TaxRuleBookID")
        var taxRuleBookID : String ?= null,
        @SerializedName("EstimatedTax")
        var estimatedTax : String ?= null,
        @SerializedName("IsInvoiceGenerated")
        var isInvoiceGenerated : String ?= null,
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID : String ?= null
): Parcelable {
        constructor(parcel: Parcel) : this(
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
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(typeName)
                parcel.writeString(sycotaxID)
                parcel.writeString(productCode)
                parcel.writeString(product)
                parcel.writeString(taxRuleBookCode)
                parcel.writeString(multiInvoice)
                parcel.writeString(isDefault)
                parcel.writeString(voucherNo)
                parcel.writeString(accountID)
                parcel.writeString(pricingRuleID)
                parcel.writeString(paymentCycleID)
                parcel.writeString(billingCycleID)
                parcel.writeString(taxRuleBookID)
                parcel.writeString(estimatedTax)
                parcel.writeString(isInvoiceGenerated)
                parcel.writeString(taxInvoiceID)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<GetSearchIndividualTaxDetails> {
                override fun createFromParcel(parcel: Parcel): GetSearchIndividualTaxDetails {
                        return GetSearchIndividualTaxDetails(parcel)
                }

                override fun newArray(size: Int): Array<GetSearchIndividualTaxDetails?> {
                        return arrayOfNulls(size)
                }
        }
}