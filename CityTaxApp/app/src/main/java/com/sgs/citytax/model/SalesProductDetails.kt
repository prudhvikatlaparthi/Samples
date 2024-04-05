package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger

data class SalesProductDetails(
    @SerializedName("ItemCode")
    var itemCode: String? = "",
    @SerializedName("Item")
    var item: String? = "",
    @SerializedName("exprydt")
    var expryDarte: String? = "",
    @SerializedName("qty")
    var quantity: BigDecimal = BigDecimal.ZERO,
    @SerializedName("unitprc")
    var unitPrice:  Double? = 0.0,
    @SerializedName("Total")
    var total:  Double? = 0.0,
    @SerializedName("days_cnt")
    val daysCnt: BigInteger? = BigInteger.ZERO,
    @SerializedName("noofperns")
    val noOfPerns: BigInteger? =  BigInteger.ZERO

)