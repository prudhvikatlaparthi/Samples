package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Line(
    @SerialName("Description")
    val description: String?,
    @SerialName("DiscountRate")
    val discountRate: Int?,
    @SerialName("DiscountRuleID")
    val discountRuleID: Int?,
    @SerialName("DiscountRuleID2")
    val discountRuleID2: Int?,
    @SerialName("DiscountRuleID3")
    val discountRuleID3: Int?,
    @SerialName("ExtendedPrice")
    val extendedPrice: Int?,
    @SerialName("FixedPrice")
    val fixedPrice: String?,
    @SerialName("ID")
    val iD: Int?,
    @SerialName("Instructions")
    val instructions: String?,
    @SerialName("ItemsSerialNos")
    val itemsSerialNos: String?,
    @SerialName("LineDiscount")
    val lineDiscount: Int?,
    @SerialName("LineDiscount2")
    val lineDiscount2: Int?,
    @SerialName("LineDiscount3")
    val lineDiscount3: Int?,
    @SerialName("LinePrice")
    val linePrice: Int?,
    @SerialName("LineStateIDOfSupply")
    val lineStateIDOfSupply: Int?,
    @SerialName("LineTax1")
    val lineTax1: Int?,
    @SerialName("LineTax2")
    val lineTax2: Int?,
    @SerialName("LineTax3")
    val lineTax3: Int?,
    @SerialName("LineTaxGroupID")
    val lineTaxGroupID: Int?,
    @SerialName("ListPrice")
    val listPrice: Int?,
    @SerialName("OrgID")
    val orgID: Int?,
    @SerialName("PriceListID")
    val priceListID: Int?,
    @SerialName("ProductCode")
    val productCode: String?,
    @SerialName("Quantity")
    val quantity: Int?,
    @SerialName("Remarks")
    val remarks: String?,
    @SerialName("SalesHeaderID")
    val salesHeaderID: Int?,
    @SerialName("SalesHeaderLineID")
    val salesHeaderLineID: Int?,
    @SerialName("Tax1ID")
    val tax1ID: Int?,
    @SerialName("Tax1Rate")
    val tax1Rate: Int?,
    @SerialName("Tax2ID")
    val tax2ID: Int?,
    @SerialName("Tax2Rate")
    val tax2Rate: Int?,
    @SerialName("Tax3ID")
    val tax3ID: Int?,
    @SerialName("Tax3Rate")
    val tax3Rate: Int?,
    @SerialName("TaxGLCode1")
    val taxGLCode1: Int?,
    @SerialName("TaxGLCode2")
    val taxGLCode2: Int?,
    @SerialName("TaxGLCode3")
    val taxGLCode3: Int?,
    @SerialName("TaxInclusive")
    val taxInclusive: String?,
    @SerialName("UnitPrice")
    val unitPrice: Int?,
    @SerialName("VariantCode")
    val variantCode: String?
)