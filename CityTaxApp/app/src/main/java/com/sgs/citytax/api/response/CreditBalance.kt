package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CreditBalance(
        @SerializedName("PaymentDate")
        var date: String,
        @SerializedName("AccountName")
        var taxPayerName: String?,
        @SerializedName("AccountID")
        var taxPayerID: Int = 0,
        @SerializedName("Credit")
        var credit: Double?,
        @SerializedName("Debit")
        var debit: Double?,
        @SerializedName("TaxElement")
        var taxElement: String?,
        @SerializedName("PaymentMode")
        var paymentMode: String,
        @SerializedName("ChequeNo")
        var chequeNumber: String,
        @SerializedName("ChequeStatus")
        var chequeStatus: String
)