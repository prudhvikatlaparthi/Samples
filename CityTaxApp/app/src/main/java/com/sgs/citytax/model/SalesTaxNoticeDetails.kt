package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class SalesTaxNoticeDetails(
    @SerializedName("AdministrativeOffice")
    val administrativeOffice: String? = null,
    @SerializedName("Block")
    val block: String? = null,
    @SerializedName("bnkname")
    val bnkname: String? = null,
    @SerializedName("ChequeAdvanceReceivedID")
    val chequeAdvanceReceivedID: Int? = null,
    @SerializedName("ChequeAmount")
    val chequeAmount: BigDecimal? = null,
    @SerializedName("ChequeNote")
    val chequeNote: String? = null,
    @SerializedName("ChequeReceiptNo")
    val chequeReceiptNo: String? = null,
    @SerializedName("ChequeStatus")
    val chequeStatus: String? = null,
    @SerializedName("ChequeStatusCode")
    val chequeStatusCode: String? = null,
    @SerializedName("chqdt")
    val chqdt: String? = null,
    @SerializedName("chqno")
    val chqno: String? = null,
    @SerializedName("CitizenCardNo")
    val citizenCardNo: String? = null,
    @SerializedName("CitizenSycoTaxID")
    val citizenSycoTaxID: String? = null,
    @SerializedName("cty")
    val cty: String? = null,
    @SerializedName("Customer")
    val customer: String? = null,
    @SerializedName("doorno")
    val doorno: String? = null,
    @SerializedName("GeneratedBy")
    val generatedBy: String? = null,
    @SerializedName("netrec")
    val netrec: BigDecimal? = null,
    @SerializedName("PaymentAdvanceReceivedID")
    val paymentAdvanceReceivedID: String? = null,
    @SerializedName("PaymentDate")
    val paymentDate: String? = null,
    @SerializedName("PaymentReceiptNo")
    val paymentReceiptNo: String? = null,
    @SerializedName("PenaltyAmount")
    val penaltyAmount: BigDecimal? = null,
    @SerializedName("ph")
    val ph: String? = null,
    @SerializedName("Plot")
    val plot: String? = null,
    @SerializedName("pmtmode")
    val pmtmode: String? = null,
    @SerializedName("pmtmodecode")
    val pmtmodecode: String? = null,
    @SerializedName("PrintCounts")
    val printCounts: Int? = null,
    @SerializedName("prodtypcode")
    val prodtypcode: String? = null,
    @SerializedName("ProsecutionFees")
    val prosecutionFees: BigDecimal? = null,
    @SerializedName("PenaltyPercentage")
    val penaltyPercentage: BigDecimal? = null,
    @SerializedName("SalesAmount")
    val salesAmount: BigDecimal? = null,
    @SerializedName("sec")
    val sec: String? = null,
    @SerializedName("sodt")
    val sodt: String? = null,
    @SerializedName("sono")
    val sono: Int? = null,
    @SerializedName("st")
    val st: String? = null,
    @SerializedName("Street")
    val street: String? = null,
    @SerializedName("TaxRuleBookCode")
    val taxRuleBookCode: String? = null,
    @SerializedName("TaxationYear")
    val taxationYear: Int? = null,
    @SerializedName("WalletTransactionNo")
    val walletTransactionNo: String? = null,
    @SerializedName("zip")
    val zip: String? = null,
    @SerializedName("zn")
    val zn: String? = null
)