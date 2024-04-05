package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InventoryStatus(
        @SerializedName("acctname")
        var accountName: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("unit")
        var unit: String? = "",
        @SerializedName("StockInHand")
        var stockInHand: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ItemCode")
        var itemCode: String? = "",
        @SerializedName("Item")
        var item: String? = ""
)