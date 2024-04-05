package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ROPDetails(
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("vchrno")
        var voucherNo: Int,
        @SerializedName("InvoiceCount")
        var invoiceCount: Int,
        @SerializedName("OccupancyName")
        var occupancyName: String? = null,
        @SerializedName("desc")
        var description: String? = null,
        @SerializedName("Length")
        var length: Double,
        @SerializedName("Width")
        var width: Double,
        @SerializedName("Height")
        var height: Double,
        @SerializedName("TaxableMatter")
        var taxableMatter: Double,
        @SerializedName("amt")
        var Amount: Double,
        @SerializedName("Due")
        var due: Double
)