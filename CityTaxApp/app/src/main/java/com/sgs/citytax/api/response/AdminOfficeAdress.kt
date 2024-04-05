package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AdminOfficeAdress(
    @SerializedName("CityCode")
    val cityCode: String? = null,
    @SerializedName("cntry")
    val cntry: String? = null,
    @SerializedName("cntrycode")
    val cntrycode: String? = null,
    @SerializedName("cty")
    val cty: String? = null,
    @SerializedName("ctyid")
    val ctyid: Int? = null,
    @SerializedName("sec")
    val sec: String? = null,
    @SerializedName("SectorID")
    val sectorID: Int? = null,
    @SerializedName("st")
    val st: String? = null,
    @SerializedName("stid")
    val stid: Int? = null,
    @SerializedName("zn")
    val zn: String? = null,
    @SerializedName("znid")
    val znid: Int? = null,
    @SerializedName("telcode")
    var telephoneCode: Int? = null,
    @SerializedName("ProsecutionFees")
    var prosecutionFees: BigDecimal? = null,
    @SerializedName("PenaltyPercentage")
    var penaltyPercentage: BigDecimal? = null,
)