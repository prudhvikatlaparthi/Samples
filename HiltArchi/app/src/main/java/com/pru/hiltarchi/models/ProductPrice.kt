package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductPrice(
    @SerialName("discntamt")
    val discntamt: String?= null,
    @SerialName("discntname")
    val discntname: String?= null,
    @SerialName("discntruleid")
    val discntruleid: Int?= null,
    @SerialName("DiscountName2")
    val discountName2: String?= null,
    @SerialName("DiscountName3")
    val discountName3: String?= null,
    @SerialName("DiscountRuleID2")
    val discountRuleID2: Int?= null,
    @SerialName("DiscountRuleID3")
    val discountRuleID3: Int?= null,
    @SerialName("extdprc")
    val extdprc: Int?= null,
    @SerialName("fxdprc")
    val fxdprc: String?= null,
    @SerialName("LineDiscount2")
    val lineDiscount2: Int?= null,
    @SerialName("LineDiscount3")
    val lineDiscount3: Int?= null,
    @SerialName("listprc")
    val listprc: Int?= null,
    @SerialName("lndiscnt")
    val lndiscnt: Int?= null,
    @SerialName("lnprc")
    val lnprc: Int?= null,
    @SerialName("lntax1")
    val lntax1: Int?= null,
    @SerialName("lntax2")
    val lntax2: Int?= null,
    @SerialName("lntax3")
    val lntax3: Int?= null,
    @SerialName("Tax1Name")
    val tax1Name: String?= null,
    @SerialName("tax1id")
    val tax1id: Int?= null,
    @SerialName("tax1rt")
    val tax1rt: Int?= null,
    @SerialName("Tax2Name")
    val tax2Name: String?= null,
    @SerialName("tax2id")
    val tax2id: Int?= null,
    @SerialName("tax2rt")
    val tax2rt: Int?= null,
    @SerialName("Tax3Name")
    val tax3Name: String?= null,
    @SerialName("tax3id")
    val tax3id: Int?= null,
    @SerialName("tax3rt")
    val tax3rt: Int?= null,
    @SerialName("taxglcode1")
    val taxglcode1: Int?= null,
    @SerialName("taxglcode2")
    val taxglcode2: Int?= null,
    @SerialName("taxglcode3")
    val taxglcode3: Int?= null,
    @SerialName("taxgrpid")
    val taxgrpid: Int?= null,
    @SerialName("taxincl")
    val taxincl: String?= null,
    @SerialName("unitprc")
    val unitprc: Int? = null
)