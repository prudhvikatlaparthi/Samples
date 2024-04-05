package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AgentRechargeReceiptDetails(
        @SerializedName("advrecdid")
        var advanceReceivedId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("advdt")
        var advanceDate: String? = "",
        @SerializedName("refno")
        var referanceNo: String? = "",
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("AgentName")
        var agentName: String? = "",
        @SerializedName("AgentCode")
        var agentCode: String? = "",
        @SerializedName("brname")
        var branchName: String? = "",
        @SerializedName("ph")
        var phone: String? = "",
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
        @SerializedName("AmountPaid")
        var amountPaid: Double? = 0.0,
        @SerializedName("CollectedBy")
        var collectedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("WalletTransactionNo")
        var walletTransactionNo: String? = ""
)