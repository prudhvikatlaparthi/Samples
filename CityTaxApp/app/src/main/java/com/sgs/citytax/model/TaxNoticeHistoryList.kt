package com.sgs.citytax.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TaxNoticeHistoryList(
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("prodcode",alternate = ["ProductCode"])
        var productCode: String? = "",
        @SerializedName("usrorgbrid")
        var userOrgBranchId: Int? = 0,
        @SerializedName("duedt",alternate = ["DueDate"])
        var dueDate: String? = "",
        @SerializedName("stscode",alternate = ["StatusCode"])
        var statusCode: String? = "",
        @SerializedName("subtot",alternate = ["SubTotal"])
        var subTotal: Double? = 0.0,
        @SerializedName("rndngmthdid")
        var roundingMethodId: Int? = 0,
        @SerializedName("rndng",alternate = ["Rounding"])
        var rounding: Double? = 0.0,
        @SerializedName("netrec",alternate = ["NetReceivable"])
        var netReceivable: Double? = 0.0,
        @SerializedName("refno")
        var referanceNo: String? = "",
        @SerializedName("desc",alternate = ["Description"])
        var description: String? = "",
        @SerializedName("custprodintid")
        var customerProductId: Int? = 0,
        @SerializedName("recdamt",alternate = ["ReceivedAmount"])
        var receivedAmount: Double? = 0.0,
        @SerializedName("acctname",alternate = ["AccountName"])
        var accountName: String? = "",
        @SerializedName("prod",alternate = ["Product"])
        var product: String? = "",
        @SerializedName("CurrentDue")
        var currentDue: Double? = 0.0,
        @SerializedName("email",alternate = ["Email"])
        var email: String? = "",
        @SerializedName("mob",alternate = ["Mobile"])
        var mobileNo: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxId: String = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode:String?="",
        @SerializedName("TaxSubType")
        var taxSubType:String?="",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID : String ?= null,
        @SerializedName("AccountID")
        var accountID : String ?= null,
        @SerializedName("UserOrgBranchID")
        var userOrgBranchID : String ?= null,
        @SerializedName("RoundingMethodID")
        var roundingMethodID : String ?= null,
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo : String ?= null,
        @SerializedName("CustomerProductInterestID")
        var customerProductInterestID : String ?= null,
        @SerializedName("Latitude")
        var latitude : String ?= null,
        @SerializedName("Longitude")
        var longitude : String ?= null,
        @SerializedName("Remarks")
        var remarks : String ?= null,
        @SerializedName("ValidUptoDate")
        var validUptoDate : String ?= null,
        @SerializedName("InventoryAccountID")
        var inventoryAccountID : String ?= null,
        @SerializedName("TransactionVoucherNo")
        var transactionVoucherNo : String ?= null,
        @SerializedName("VoucherNo")
        var voucherNo : String ?= null,
        @SerializedName("Zone")
        var zone : String ?= null,
        @SerializedName("Sector")
        var sector : String ?= null,
        @SerializedName("sts")
        var status : String ?= null,
        @SerializedName("ProductTypeCode")
        var productTypeCode : String ?= null,
        @SerializedName("MobileWithCode")
        var mobileWithCode : String ?= null,
        @SerializedName("BillingCycleID")
        var billingCycleID : String ?= null,
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false
)