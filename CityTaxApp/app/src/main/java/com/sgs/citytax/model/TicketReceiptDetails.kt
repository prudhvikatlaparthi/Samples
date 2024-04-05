package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TicketReceiptDetails(
        @SerializedName("advrecdid")
        var advanceReceivedId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("advdt")
        var advanceDate: String? = "",
        @SerializedName("refno")
        var referanceNo: String? = "",
        @SerializedName("pmtmode")
        var paymentMode: String? = "",
        @SerializedName("pmtmodecode")
        var paymentModeCode: String? = "",
        @SerializedName("TotalAmount")
        var totalAmount: Double? = 0.0,
        @SerializedName("CollectedBy")
        var collectedBy: String? = "",
        @SerializedName("WalletTransactionNo")
        var walletTransactionNumber: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("vehno")
        var vehicleNumber:String?="",
        @SerializedName("VehicleSycoTaxID")
        var vehicleSycotaxID: String?="",
        @SerializedName("ExtraCharge")
        var extraCharge:Double?=0.0,
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0,
        @SerializedName("bnkname")
        val bankName: String? = null,
        @SerializedName("chqno")
        val chequeNumber: String? = null,
        @SerializedName("chqdt")
        val chequeDate: String? = null,
        @SerializedName("ChequeAmount")
        val chequeAmount: BigDecimal? = null,
        @SerializedName("ChequeStatusCode")
        val chequeStatusCode: String? = null,
        @SerializedName("ChequeStatus")
        val chequeStatus: String? = null,
)