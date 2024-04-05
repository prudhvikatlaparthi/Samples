package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class InventoryProductsDetails (
    @SerializedName("ItemCode")
    val itemCode: String? = null,
    @SerializedName("Item")
    val item: String? = null,
    @SerializedName("InventoryAllowed")
    val inventoryAllowed: String? = null,
    @SerializedName("Product")
    val product: String? = null,
    @SerializedName("ProductCode")
    val productCode: String? = null,
    @SerializedName("ProductTypeCode")
    val productTypeCode: String? = null,
    @SerializedName("Unit")
    val unit: String? = null,
    @SerializedName("AllowFractionalQuantity")
    var allwfrctnlqty: String? = null
) {
    override fun toString(): String {
        return "$itemCode\n$item"
    }
}