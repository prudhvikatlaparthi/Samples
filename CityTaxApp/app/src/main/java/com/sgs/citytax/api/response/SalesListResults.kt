package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class SalesListResults (
    @SerializedName("SalesOrderDate")
    var taxInvoiceDate: String? = null,
    @SerializedName("PaymentReceiptNo")
    var paymentReceiptNo: String? = null,
    @SerializedName("NetReceivable")
    var netReceivable:BigDecimal? = null,
    @SerializedName("PaymentMode")
    var paymentModeCode: String? = null,
    @SerializedName("AdvanceReceivedID")
    var advanceReceivedId:Int?=0,
    @SerializedName("ChequeStatus")
    var chequeStatus:String?= null,
    @SerializedName("SalesOrderNo")
    var salesOrderNo:Int? = null,
    @SerializedName("ChequeNo")
    var chequeNo:String? = null,
    @SerializedName("Taxes")
    var product:String?= null,
    @SerializedName("PenaltyAmount")
    var penaltyAmount:BigDecimal?= null,
    @SerializedName("ProsecutionFees")
    var prosecutionFees:BigDecimal?= null,
    @SerializedName("SalesAmount")
    var salesAmount:BigDecimal?= null,
    @SerializedName("BankName")
    var bankName:String?= null,
    @SerializedName("ChequeDate")
    var chequeDate: String? = null,
    @SerializedName("ChequeAmount")
    var chequeAmount:BigDecimal?= null,
)
