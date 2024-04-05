package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.util.Constant

data class GenerateCustomerAllTaxes(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerID: Int? = 0,
        @SerializedName("receiptcode")
        var receiptCode: String = Constant.ReceiptCode.TaxNoticeReceipt.code
)