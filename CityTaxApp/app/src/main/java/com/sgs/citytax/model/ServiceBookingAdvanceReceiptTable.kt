package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ServiceBookingAdvanceReceiptTable(
        @SerializedName("advrecdid")
        var advanceReceivedId: Int? = 0,
        @SerializedName("svcreqno")
        var serviceReqNo: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("advdt")
        var advanceDate: String? = "",
        @SerializedName("refno")
        var referanceNo: String? = "",
        @SerializedName("svcreqdt")
        var serviceReqDate: String? = "",
        @SerializedName("acctname")
        var businessName: String? = "",
        @SerializedName("mob")
        var phoneNo: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("SycoTaxID", alternate = ["SycotaxID"])
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
        @SerializedName("ServiceType")
        var serviceType: String? = "",
        @SerializedName("ServiceSubType")
        var serviceSubType: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("pmtmode")
        var paymentMode: String? = "",
        @SerializedName("WalletTransactionNo")
        var walletTransactionNo: String? = "",
        @SerializedName("chqno")
        var chqno: String? = "",
        @SerializedName("chqdt")
        var chqdt: String? = "",
        @SerializedName("bnkname")
        var bnkname: String? = "",
        @SerializedName("ChequeNote")
        var ChequeNote: String? = "",
        @SerializedName("dpstamt")
        var depositAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AmountofThisPayment")
        var amountofThisPayment: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AdvanceAmount")
        var advanceAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("EstimatedAmount")
        var estimatedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("GeneratedBy")
        var generateBy:String?= "",
        @SerializedName("BookingRequestID")
        var bookingRequestId:Int?=0,
        @SerializedName("crtd")
        var createdBy: String? = "",
        @SerializedName("CollectedBy")
        var collectedBy: String? = "",
        @SerializedName("Rate")
        var rate: String? = "",
        @SerializedName("area")
        var area: String? = "",
        @com.google.gson.annotations.SerializedName("BookingRequestDate")
        var bookingRequestDate:String?="",
        @SerializedName("CitizenSycotaxID", alternate = ["CitizenSycoTaxID"])
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("PrintCounts")
        var printCouts: Int? = 0,
       @SerializedName("prodcode")
        var productCode: String? = ""
)