package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ShowTaxNoticeReceiptDetails(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("Owners")
        var businessOwners: String? = "",
        @SerializedName("SycoTaxID")
        var sycoTaxId: String? = "",
        @SerializedName("Profession")
        var profession: String? = "",
        @SerializedName("Number")
        var phoneNumber: String? = "",
        @SerializedName("ShowName")
        var showName: String? = "",
        @SerializedName("OperatorType")
        var operatorType: String? = "",
        @SerializedName("prod")
        var product: String? = "",
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
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("Rate")
        var rate: String? = "",
        @SerializedName("Revenue")
        var revenue: Double? = 0.0,
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
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("note")
        var note: String? = "",
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("ShowCount")
        var noOfShows:Double?=0.0,
        @SerializedName("ShowCountRate")
        var showCountRate:String?=""
)