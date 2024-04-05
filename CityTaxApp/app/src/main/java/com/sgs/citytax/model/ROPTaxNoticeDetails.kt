package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ROPTaxNoticeDetails(
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
        @SerializedName("Market")
        var market: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("Owners")
        var businessOwners: String? = "",
        @SerializedName("OwnerNumbers")
        var ownersNumber: String? = "",
        @SerializedName("BillingCycleCode")
        var billingCycleCode: String? = "",
        @SerializedName("OccupancyName")
        var occupancyName: String? = "",
        @SerializedName("SycotaxID")
        var sycotaxId: String? = "",
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
        @SerializedName("Length")
        var length: Double? = 0.0,
        @SerializedName("NumberOfHead")
        var numberOfHeads: Double? = 0.0,
        @SerializedName("Rate")
        var rate: String? = "",
        @SerializedName("InvoiceAmount")
        var invoiceAmounnt: Double? = 0.0,
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
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = ""
)