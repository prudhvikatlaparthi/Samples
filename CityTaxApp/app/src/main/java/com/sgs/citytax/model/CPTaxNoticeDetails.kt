package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CPTaxNoticeDetails(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("BusinessMobile")
        var businessMobile: String? = "",
        @SerializedName("ActivityClass")
        var activityClass: String? = "",
        @SerializedName("ActivityDomain")
        var activityDomain: String? = "",
        @SerializedName("Owners")
        var owners: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
        @SerializedName("IFU")
        var ifu: String? = "",
        @SerializedName("TRNNo")
        var tradeNo: String? = "",
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
        @SerializedName("TurnOverAmount")
        var turnOverAmount: Double? = 0.000,
        @SerializedName("TurnOverTaxAmount")
        var turnOverTaxAmount: Double? = 0.000,
        @SerializedName("AmountDueCurrentYear")
        var amountDueCurrentYear: Double? = 0.000,
        @SerializedName("AmountDueAnteriorYear")
        var amountDueAnteriorYear: Double? = 0.000,
        @SerializedName("AmountDuePreviousYear")
        var amountDuePreviousYear: Double? = 0.000,
        @SerializedName("PenaltyDue")
        var penaltyDue: Double? = 0.000,
        @SerializedName("note")
        var note: String? = "",
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("seg")
        var businessType:String?="",
        @SerializedName("strtdt")
        var startDate:String?="",
        @SerializedName("TaxRefundDemandAmount")
        var taxRefundDemandAmount:  Double? = 0.000

)