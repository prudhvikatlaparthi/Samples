package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class INVStockAllocation(
    @SerializedName("allocdt")
    var date : Date? = null,
    @SerializedName("FromAccountID")
    var fromAccountId : Int? = 0,
    @SerializedName("ToAccountID")
    var toAccountID : Int? = 0,
    @SerializedName("ItemCode")
    var itemCode : String? = "",
    @SerializedName("qty")
    var quantity : Double? = null,
    @SerializedName("rmks")
    var remarks : String? = "",
)
