package com.sgs.citytax.api.payload


import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

data class LstSalesItem(
    @SerializedName("exprydt")
    val exprydt: Date?,
    @SerializedName("ItemCode")
    val itemCode: String?,
    @SerializedName("lnprc")
    val lnprc: BigDecimal?,
    @SerializedName("qty")
    val qty: BigDecimal?,
    @SerializedName("unitprc")
    val unitprc: BigDecimal?,
    @SerializedName("prodcode")
    val prodCode: String? = null,
    @SerializedName("days_cnt")
    val daysCnt: BigInteger? = null,
    @SerializedName("noofperns")
    val noOfPerns: BigInteger? = null
)