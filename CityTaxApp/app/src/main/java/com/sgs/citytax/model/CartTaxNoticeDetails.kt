package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CartTaxNoticeDetails(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("CartNo")
        var cartNo: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("Owner")
        var businessOwner: String? = "",
        @SerializedName("BusinessOwnerID")
        var businessOwnerID: String? = "",
        @SerializedName("CitizenID")
        var citizenID: String? = "",
        @SerializedName("Profession")
        var profession: String? = "",
        @SerializedName("Number")
        var phoneNumber: String? = "",
        @SerializedName("CartType")
        var cartType: String? = "",
        @SerializedName("CartSycotaxID")
        var cartSycotaxID: String? = "",
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
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zip: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("BillingCycle")
        var billingCycle: String? = "",
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
        @SerializedName("CitizenSycoTaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("SycotaxID")
        var sycotaxID: String? = ""

)