package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.util.getQuantity
import com.sgs.citytax.util.getString
import java.math.BigDecimal

data class SalesProductData(
    @SerializedName("ItemCode")
    var itemCode: String,
    @SerializedName("Item")
    var item: String,
    @SerializedName("unitcode", alternate = ["UnitCode"])
    var unitCode: String?,
    @SerializedName("unit", alternate = ["Unit"])
    var unit: String?,
    @SerializedName("InventoryAllowed")
    var inventoryAllowed: String?,
    @SerializedName("ValidityApplicable")
    var validityApplicable: String?,
    @SerializedName("ValidForMonths")
    var validForMonths: Int?,
    @SerializedName("unitprc")
    var unitPrice: BigDecimal?,
    @SerializedName("StockInHand")
    var stockInHand: BigDecimal = BigDecimal.ZERO,
    @SerializedName("allwfrctnlqty", alternate = ["AllowFractionalQuantity"])
    var allwfrctnlqty: String? = null
) {
    override fun toString(): String {
        val returnData = getString(R.string.item_name) + " : \n" + item + "\n\n" +
                getString(R.string.stock_in_hand) + " : " + getQuantity(stockInHand.toString())

        return  returnData
    }
}
