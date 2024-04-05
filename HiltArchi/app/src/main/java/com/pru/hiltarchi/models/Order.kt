package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    @SerialName("DueDate")
    val dueDate: String?,
    @SerialName("ID")
    val iD: Int?,
    @SerialName("IsPickup")
    val isPickup: String?,
    @SerialName("OrgID")
    val orgID: Int?,
    @SerialName("SalesHeaderID")
    val salesHeaderID: Int?,
    @SerialName("SalesOrderDate")
    val salesOrderDate: String?,
    @SerialName("SalesOrderNo")
    val salesOrderNo: Int?,
    @SerialName("SalesPriority")
    val salesPriority: String?,
    @SerialName("SalesQuotationNo")
    val salesQuotationNo: Int?,
    @SerialName("StatusCode")
    val statusCode: String?
)