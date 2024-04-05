package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BookingAdvanceReceiptTable(
        @SerializedName("advrecdid")
        var advanceReceivedId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("advdt")
        var advanceDate: String? = "",
        @SerializedName("refno")
        var referanceNo: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("ph")
        var phoneNo: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
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
        var paymentMode: String? = "",
        @SerializedName("dpstamt")
        var depositAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("GeneratedBy")
        var generateBy:String?= "",
        @SerializedName("BookingRequestID")
        var bookingRequestId:Int?=0,
        @SerializedName("BookingRequestDate")
        var bookingRequestDate:String?="",
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("BookingDeposit")
        var bookingDeposit:Double?=0.0,
        @SerializedName("SecurityDeposit")
        var securityDeposit:Double?=0.0,
        @SerializedName("WalletTransactionNo")
        var walletTransactionNumber: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0
)