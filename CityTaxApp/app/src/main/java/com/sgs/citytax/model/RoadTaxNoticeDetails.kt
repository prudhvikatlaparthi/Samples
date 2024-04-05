package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class RoadTaxNoticeDetails(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        val noticeReferanceNo:String?="",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("ph")
        var phone:String?="",
        @SerializedName("SycotaxID")
        var sycotaxId: String? = "",
        @SerializedName("Owners")
        var businessOwners: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("sec")
        var sector: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("InvoiceAmount")
        var invoiceAmount: Double? = 0.0,
        @SerializedName("AmountDueCurrentYear")
        var amountDueForCurrentYear: Double? = 0.0,
        @SerializedName("AmountDueAnteriorYear")
        var amountDueAnteriorYear: Double? = 0.0,
        @SerializedName("AmountDuePreviousYear")
        var amountDuePreviousYear: Double? = 0.0,
        @SerializedName("PenaltyDue")
        var penaltyDue: Double? = 0.0,
        @SerializedName("note")
        var note: String? = "",
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0
)