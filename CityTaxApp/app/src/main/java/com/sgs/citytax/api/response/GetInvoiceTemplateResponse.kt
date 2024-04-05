package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GetInvoiceTemplateResponse(
        // region Header
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("OccupancyName")
        var occupancyName: String? = "",
        @SerializedName("MarketName", alternate = ["MarkteName"])
        var marketName: String? = "",
        @SerializedName("SubTotal")
        var subTotal: Double? = 0.000,
        @SerializedName("Sector")
        var sector: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("Zone")
        var zone: String? = "",
        @SerializedName("ProductCode")
        var productCode: String? = "",
        @SerializedName("DueAmount")
        var dueAmount: Double? = 0.000,
        @SerializedName("InvoiceDueAmount")
        var invoiceDueAmount: Double? = 0.000,
        @SerializedName("PaymentMode")
        var paymentMode: String? = "",
        @SerializedName("CustomerName")
        var customerName: String? = "",
        @SerializedName("ReceivedAmount")
        var receivedAmount: String? = "",
        @SerializedName("IFU")
        var IFU: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        // endregion
        // region Line
        @SerializedName("Rate")
        var rate: Double? = 0.000,
        @SerializedName("PaymentCycle")
        var paymentCycle: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("TaxableMatterName")
        var taxableMatterName: String? = "",
        @SerializedName("PricingRuleType")
        var pricingRuleType: String? = "",
        @SerializedName("TaxableMatter")
        var taxableMatter: Double? = 0.000,
        @SerializedName("Length")
        var length: String? = "",
        @SerializedName("Height")
        var height: String? = "",
        @SerializedName("Width")
        var width: String? = "",
        @SerializedName("ROPTaxableMatter")
        var ropTaxableMatter: String? = ""
        // endregion
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(taxInvoiceID)
        parcel.writeString(sycoTaxID)
        parcel.writeString(taxInvoiceDate)
        parcel.writeValue(taxationYear)
        parcel.writeString(occupancyName)
        parcel.writeString(marketName)
        parcel.writeValue(subTotal)
        parcel.writeString(sector)
        parcel.writeString(street)
        parcel.writeString(zone)
        parcel.writeString(productCode)
        parcel.writeValue(dueAmount)
        parcel.writeValue(invoiceDueAmount)
        parcel.writeString(paymentMode)
        parcel.writeString(customerName)
        parcel.writeString(receivedAmount)
        parcel.writeString(IFU)
        parcel.writeValue(printCounts)
        parcel.writeValue(rate)
        parcel.writeString(paymentCycle)
        parcel.writeString(billingCycle)
        parcel.writeString(taxableMatterName)
        parcel.writeString(pricingRuleType)
        parcel.writeValue(taxableMatter)
        parcel.writeString(length)
        parcel.writeString(height)
        parcel.writeString(width)
        parcel.writeString(ropTaxableMatter)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetInvoiceTemplateResponse> {
        override fun createFromParcel(parcel: Parcel): GetInvoiceTemplateResponse {
            return GetInvoiceTemplateResponse(parcel)
        }

        override fun newArray(size: Int): Array<GetInvoiceTemplateResponse?> {
            return arrayOfNulls(size)
        }
    }
}