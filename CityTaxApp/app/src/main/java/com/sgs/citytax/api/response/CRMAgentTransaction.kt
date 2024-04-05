package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CRMAgentTransaction(
        @SerializedName("dt")
        var date: String,
        @SerializedName("custname")
        var customerName: String?,
        @SerializedName("taxname")
        var taxName: String?,
        @SerializedName("pmtmode")
        var paymentMode: String?,
        @SerializedName("amt")
        var amount: Double?,
        @SerializedName("vchrno")
        var voucherNo: String?,
        @SerializedName("Commission")
        var commissionAmount: Double? = 0.0,
        @SerializedName("CommissionBalance")
        var commissionBalance: Double? = 0.0,
        @SerializedName("CollectedBy")
        var collectedBy: String?,
        @SerializedName("accttypcode")
        var accountTypeCode: String?,
        @SerializedName("taxtyp")
        var taxType: String? = "",
        @SerializedName("IsAdminUser")
        var isAdminUser: String?,
        @SerializedName("recdby")
        var receivedBy: String?,
        @SerializedName("ChequeStatusCode")
        var chequeStatusCode: String,
        @SerializedName("ChequeStatus")
        var chequeStatus: String,
        @SerializedName("vehno")
        var vehicleNo: String,
        @SerializedName("TaxPayer")
        var taxPayer: String,
        @SerializedName("prodtypcode")
        var prodtypcode: String
)