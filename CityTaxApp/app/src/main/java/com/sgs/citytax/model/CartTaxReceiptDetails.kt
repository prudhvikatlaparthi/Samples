package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CartTaxReceiptDetails(
        @SerializedName("advrecdid")
        var advanceReceiptId: String? = "",
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("advdt")
        var advancedate: String? = "",
        @SerializedName("refno")
        var referenceNo: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("TaxSubType")
        var taxSubType: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("ph")
        var phoneNumber: String? = "",
        @SerializedName("SycotaxID")
        var sycotaxID: String? = "",
        @SerializedName("ActivityClass")
        var activityClass: String? = "",
        @SerializedName("ActivityDomain")
        var activityDomain: String? = "",
        @SerializedName("Owners")
        var businessOwner: String? = "",
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
        @SerializedName("pmtmode")
        var paymentmode: String? = "",
        @SerializedName("WalletTransactionNo")
        var walletTransactionNo: String? = "",
        @SerializedName("chqno")
        var chequeNumber: String? = "",
        @SerializedName("chqdt")
        var chequeDate: String? = "",
        @SerializedName("bnkname")
        var bankName: String? = "",
        @SerializedName("TotalDeposit")
        var totalDeposits: Double? = 0.0,
        @SerializedName("AmountOfTaxImposed")
        var amountOfTaxImposed: Double? = 0.0,
        @SerializedName("AmountofThisPayment")
        var amountofThisPayment: Double? = 0.0,
        @SerializedName("AmountPaidCurrentYear")
        var amountPaidCurrentYear: Double? = 0.0,
        @SerializedName("AmountPaidAnteriorYear")
        var amountPaidAnteriorYear: Double? = 0.0,
        @SerializedName("AmountPaidPreviousYear")
        var amountPaidPreviousYear: Double? = 0.0,
        @SerializedName("PenaltyPaid")
        var PenaltyPaid: Double? = 0.0,
        @SerializedName("AmountDueCurrentYear")
        var amountDueCurrentYear: Double? = 0.0,
        @SerializedName("AmountDueAnteriorYear")
        var amountDueAnteriorYear: Double? = 0.0,
        @SerializedName("AmountDuePreviousYear")
        var amountDuePreviousYear: Double? = 0.0,
        @SerializedName("PenaltyDue")
        var penaltyDue: Double? = 0.0,
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("ChequeNote")
        var chequeNote:String?="",
        @SerializedName("pmtmodecode")
        var paymentModeCode:String?="",
        @SerializedName("CitizenSycoTaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = ""

)