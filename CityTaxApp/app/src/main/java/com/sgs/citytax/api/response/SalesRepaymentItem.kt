package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.util.OrderSequence
import java.math.BigDecimal

data class SalesRepaymentItem(
    @OrderSequence(1)
    @SerializedName("SalesOrderDate")
    val sales_order_date: String? = null,
    @OrderSequence(2)
    @SerializedName("SalesOrderNo")
    val sales_order_no: Int? = null,
    @OrderSequence(3)
    @SerializedName("Taxes")
    val title_taxes: String? = null,
    @OrderSequence(4)
    @SerializedName("AccountID")
    val accountID_: Int? = null,
    @OrderSequence(5)
    @SerializedName("AccountName")
    val citizen_name: String? = null,
    @OrderSequence(6)
    @SerializedName("CitizenSycoTaxID")
    val str_citizen_id_sycotax_: String? = null,
    @OrderSequence(7)
    @SerializedName("AgentName")
    val agent_name: String? = null,
    @OrderSequence(8)
    @SerializedName("AgentCode")
    val agentCode_: String? = null,
    @OrderSequence(9)
    @SerializedName("BankName")
    val bank_name: String? = null,
    @OrderSequence(10)
    @SerializedName("ChequeNo")
    val cheque_number: String? = null,
    @OrderSequence(11)
    @SerializedName("ChequeDate")
    val cheque_date: String? = null,
    @OrderSequence(12)
    @SerializedName("ChequeAmount")
    val cheque_amount: BigDecimal? = null,
    @OrderSequence(13)
    @SerializedName("ChequeStatus")
    val cheque_status: String? = null,
    @OrderSequence(14)
    @SerializedName("ChequeStatusCode")
    val chequeStatusCode_: String? = null,
    @OrderSequence(15)
    @SerializedName("CitizenCardNo")
    val citizenCardNo_: String? = null,
    @OrderSequence(16)
    @SerializedName("SalesAmount")
    val str_sales_amount: BigDecimal? = null,
    @OrderSequence(17)
    @SerializedName("SalesDue")
    val sales_due: BigDecimal? = null,
    @OrderSequence(18)
    @SerializedName("NoticeReferenceNo")
    val noticeReferenceNo_: String? = null,
    @OrderSequence(19)
    @SerializedName("PenaltyAmount")
    val penalty_amount: BigDecimal? = null,
    @OrderSequence(20)
    @SerializedName("PenaltyDue")
    val penalty_due: BigDecimal? = null,
    @OrderSequence(21)
    @SerializedName("ProsecutionFees")
    val txt_prosecution_fees: BigDecimal? = null,
    @OrderSequence(22)
    @SerializedName("ProsecutionFeesDue")
    val prosecution_fees_due: BigDecimal? = null,
    @OrderSequence(23)
    @SerializedName("Sector")
    val sector_: String? = null,
    @OrderSequence(24)
    @SerializedName("Street")
    val street_: String? = null,
    @OrderSequence(25)
    @SerializedName("Zone")
    val zone_: String? = null,
    @OrderSequence(26)
    @SerializedName("CurrentDue")
    val current_due: BigDecimal? = null,
    @OrderSequence(27)
    @SerializedName("NetReceivable")
    val net_receivable: BigDecimal? = null,
    @OrderSequence(28)
    @SerializedName("TaxRuleBookCode")
    var taxRuleBookCode_: String? = "",
)