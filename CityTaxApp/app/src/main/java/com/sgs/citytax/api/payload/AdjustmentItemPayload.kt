package com.sgs.citytax.api.payload


import com.google.gson.annotations.SerializedName
import java.util.*

data class AdjustmentItemPayload(
    @SerializedName("acctid")
    val acctid: Int? = null,
    @SerializedName("adjdt")
    val adjdt: Date? = null,
    @SerializedName("adjid")
    val adjid: Int? = null,
    @SerializedName("adjtypid")
    val adjtypid: Int? = null,
    @SerializedName("binlocid")
    val binlocid: Int? = null,
    @SerializedName("crtd")
    val crtd: Any? = null,
    @SerializedName("crtddt")
    val crtddt: String? = null,
    @SerializedName("CustomProperties")
    val customProperties: Any? = null,
    @SerializedName("DeparturePreparationNo")
    val departurePreparationNo: Int? = null,
    @SerializedName("DesignFilePath")
    val designFilePath: Any? = null,
    @SerializedName("DesignSource")
    val designSource: Any? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("IsUpdateable")
    val isUpdateable: Boolean? = null,
    @SerializedName("ItemCode")
    val itemCode: String? = null,
    @SerializedName("mdfd")
    val mdfd: Any? = null,
    @SerializedName("mdfddt")
    val mdfddt: String? = null,
    @SerializedName("orgid")
    val orgid: Int? = null,
    @SerializedName("prodcode")
    val prodcode: Any? = null,
    @SerializedName("qty")
    val qty: Double? = null,
    @SerializedName("refno")
    val refno: Any? = null,
    @SerializedName("reftxntypcode")
    val reftxntypcode: Any? = null,
    @SerializedName("refvchrno")
    val refvchrno: Int? = null,
    @SerializedName("rmks")
    val rmks: String? = null,
    @SerializedName("TrolleyNo")
    val trolleyNo: Any? = null,
    @SerializedName("usrorgbrid")
    val usrorgbrid: Int? = null,
    @SerializedName("VariantCode")
    val variantCode: Any? = null
)