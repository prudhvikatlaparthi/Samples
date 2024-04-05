package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class TaxReceiptsDetails(
        @SerializedName("advrecdid")
        var advanceReceivedID: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("advdt")
        var advanceDate: String? = "",
        @SerializedName("refno")
        var referanceNo: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("TaxSubType")
        var taxSubType: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("ph")
        var phoneNo: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxId: String? = "",
        @SerializedName("Owners")
        var businessOwner: String? = "",
        @SerializedName("OwnerNumbers")
        var businessOwnerPhone: String? = "",
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
        @SerializedName("pmtmode")
        var paymentMode: String? = "",
        @SerializedName("AmountOfTaxImposed")
        var taxImposed: Double? = 0.0,
        @SerializedName("AmountofThisPayment")
        var amountOfThisPayment: Double? = 0.0,
        @SerializedName("AmountPaidCurrentYear")
        var amountPaidCurrentYear:Double?= 0.0,
        @SerializedName("AmountPaidAnteriorYear")
        var amountPaidAnteriorYear:Double?= 0.0,
        @SerializedName("AmountPaidPreviousYear")
        var amountPaidPreviousYear:Double?=0.0,
        @SerializedName("PenaltyPaid")
        var penaltyPaid:Double?=0.0,
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
        @SerializedName("TotalDeposit")
        var totalDeposits:Double?=0.0,
        @SerializedName("WalletTransactionNo")
        var walletTransactionNo: String? = "",
        @SerializedName("chqno")
        var chequeNumber: String? = "",
        @SerializedName("chqdt")
        var chequeDate: String? = "",
        @SerializedName("bnkname")
        var bankName: String? = "",
        @SerializedName("ChequeNote")
        var chequeNote:String?="",
        @SerializedName("pmtmodecode")
        var paymentModeCode:String?="",
        @SerializedName("licenseCategory")
        var licenseCategory:String?="",
        @SerializedName("authorizedBeverages")
        var authorizedBeverages:String?=""
)