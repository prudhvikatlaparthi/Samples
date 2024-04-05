package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.AppReceiptPrint

data class InsertPrintRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("app_receiptprint")
        var appReceiptPrint: AppReceiptPrint? = null
)