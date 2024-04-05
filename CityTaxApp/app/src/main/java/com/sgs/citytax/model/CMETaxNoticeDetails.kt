package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CMETaxNoticeDetails(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("BusinessMobile")
        var businessMobile: String? = "",
        @SerializedName("ActivityClass")
        var activityClass: String? = "",
        @SerializedName("Owners")
        var businessOwner: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxId: String? = "",
        @SerializedName("IFU")
        var ifuNumber: String? = "",
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
        @SerializedName("TurnOverTaxAmount")
        var turnOverTax: Double? = 0.0,
        @SerializedName("AmountDueCurrentYear")
        var amountDueCurrentYear: Double? = 0.0,
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
        var printCounts: Int? = 0,
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("biztyp")
        var businessType:String?="",
        @SerializedName("TRNNo")
        var trnNo:String?="",
        @SerializedName("prod")
        var product:String?=""
)