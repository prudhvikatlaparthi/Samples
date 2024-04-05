package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AppReceiptPrint(
        @SerializedName("ReceiptCode")
        var receiptCode: String? = "",
        @SerializedName("PrintDateTime")
        var printDateTime: String? = "",
        @SerializedName("prykeyval")
        var primaryKeyValue: Int? = 0
)