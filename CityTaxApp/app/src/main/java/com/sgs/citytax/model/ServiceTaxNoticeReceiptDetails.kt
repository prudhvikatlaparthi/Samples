package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ServiceTaxNoticeReceiptDetails(
        @SerializedName("svcreqno")
        var serviceRequestNo: Int? = 0,
        @SerializedName("svcreqdt")
        var serviceRequestDate: String? = "",
        @SerializedName("TaxationDate")
        var taxationDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferanceNo: String? = "",
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("SycoTaxID")
        var sycoTaxID: String? = "",
        @SerializedName("acctname")
        var accountName: String? = "",
        @SerializedName("mob")
        var mobile: String? = "",
        @SerializedName("email")
        var email: String? = "",
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
        @SerializedName("CitizenSycotaxID", alternate = ["CitizenSycoTaxID"])
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("Rate")
        var rate: String? = "",
        @SerializedName("BookingDeposit")
        var bookingDeposit: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ServiceAmount")
        var serviceAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ExtraCharges")
        var extraCharges: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("RemainingToPay")
        var remainingToPay: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("pmtmode")
        var paymentMode: String? = "",
        @SerializedName("refno")
        var referanceNo: String? = "",
        @SerializedName("advrecdid")
        var advanceReceivedID: Int? = 0,
        @SerializedName("WalletTransactionNo")
        var walletTransactionNo: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("CollectedBy")
        var collectedBy: String? = "",
        @SerializedName("PrintCounts")
        var printCouts: Int? = 0,
        @SerializedName("ServiceType")
        var serviceType: String? = "",
        @SerializedName("ServiceSubType")
        var serviceSubType: String? = "",
        @SerializedName("area")
        var area: Double? = 0.0
)