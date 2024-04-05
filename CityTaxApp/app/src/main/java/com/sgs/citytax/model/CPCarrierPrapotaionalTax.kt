package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CPCarrierPrapotaionalTax(
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("RentPerYear")
        var rentPerYear: Double? = 0.000,
        @SerializedName("RentTaxRate")
        var rentTaxRate: String? = "",
        @SerializedName("RentApplied")
        var rentApplied: String? = "",
        @SerializedName("RentTaxAmount")
        var rentTaxAmount: Double? = 0.000,
        @SerializedName("FixedPriceTurnoverTax")
        var fixedPriceTurnoverTax: Double? = 0.000,
        @SerializedName("PriceFactor")
        var priceFactor: Double? = 0.000,
        @SerializedName("FixedPriceApplied")
        var fixedPriceApplied: String? = "",
        @SerializedName("FixedPriceTaxAmount")
        var fixedPriceTaxAmount: Double? = 0.000
)