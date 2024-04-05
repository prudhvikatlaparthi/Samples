package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BookingRequestReceiptDetails(
        @SerializedName("BookingRequestID")
        var bookingRequestId: Int? = 0,
        @SerializedName("BookingRequestDate")
        var bookingRequestDate: String? = "",
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("SycotaxID")
        var sycoTaxId: String? = "",
        @SerializedName("acctname")
        var accountName: String? = "",
        @SerializedName("mob")
        var mobile: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("cty")
        var city: String? = "",
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
        @SerializedName("EstimatedAmount")
        var estimatedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("netrec")
        var netReceivable:Double?=0.0,
        @SerializedName("BookingRequestBy")
        var bookingRequestBy: String? = "",
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("AllowPeriodicInvoice")
        var allowPeriodicInvoice: String? = null,
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0
)