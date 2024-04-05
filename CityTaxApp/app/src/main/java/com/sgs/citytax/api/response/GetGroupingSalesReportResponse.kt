package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class GetGroupingSalesReportResponse(
    @SerializedName("Table")
    val headerT: List<HeaderT>? = null,
    @SerializedName("Table1")
    val headerT1: List<HeaderT1>? = null,
    @SerializedName("Table2")
    val headerT2: List<HeaderT2>? = null,
    @SerializedName("Table3")
    val headerT3: List<HeaderT3>? = null
)

data class HeaderT(
    @SerializedName("qty")
    val qty: BigDecimal? = null,
    @SerializedName("saldt")
    val saldt: String? = null,
    @SerializedName("SalesAmount")
    val salesAmount: BigDecimal? = null
)

data class HeaderT1(
    @SerializedName("ParentItemCategory")
    val parentItemCategory: String? = null,
    @SerializedName("ParentItemCategoryCode")
    val parentItemCategoryCode: String? = null,
    @SerializedName("qty")
    val qty: BigDecimal? = null,
    @SerializedName("saldt")
    val saldt: String? = null,
    @SerializedName("SalesAmount")
    val salesAmount: BigDecimal? = null
)

data class HeaderT2(
    @SerializedName("ItemCategory")
    val itemCategory: String? = null,
    @SerializedName("ItemCategoryCode")
    val itemCategoryCode: String? = null,
    @SerializedName("ParentItemCategoryCode")
    val parentItemCategoryCode: String? = null,
    @SerializedName("qty")
    val qty: BigDecimal? = null,
    @SerializedName("saldt")
    val saldt: String? = null,
    @SerializedName("SalesAmount")
    val salesAmount: BigDecimal? = null
)

data class HeaderT3(
    @SerializedName("AgentAccountID")
    val agentAccountID: Int? = null,
    @SerializedName("Citizen")
    val citizen: String? = null,
    @SerializedName("CitizenSycoTaxID")
    val citizenSycoTaxID: String? = null,
    @SerializedName("Item")
    val item: String? = null,
    @SerializedName("ItemCategory")
    val itemCategory: String? = null,
    @SerializedName("ItemCategoryCode")
    val itemCategoryCode: String? = null,
    @SerializedName("ItemCode")
    val itemCode: String? = null,
    @SerializedName("ParentItemCategory")
    val parentItemCategory: String? = null,
    @SerializedName("ParentItemCategoryCode")
    val parentItemCategoryCode: String? = null,
    @SerializedName("Price")
    val price: BigDecimal? = null,
    @SerializedName("qty")
    val qty: BigDecimal? = null,
    @SerializedName("saldt")
    val saldt: String? = null,
    @SerializedName("SalesAmount")
    val salesAmount: BigDecimal? = null,
    @SerializedName("SalesByAgent")
    val salesByAgent: String? = null,
    @SerializedName("SalesByAgentCode")
    val salesByAgentCode: String? = null
)