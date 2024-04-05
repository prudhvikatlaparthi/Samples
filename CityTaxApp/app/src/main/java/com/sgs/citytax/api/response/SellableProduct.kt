package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.getQuantity
import com.sgs.citytax.util.getString
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

data class SellableProduct(

        /* *
         {
            "prodcode": "7003_Food_Category1",
            "prod": "Catégorie I (kiosque, cafétéria)",
            "unitcode": "EA",
            "unit": "Each",
            "photo": null,
            "ValidForMonths": 0,
            "PricingRuleID": 104,
            "StoclInHand": 0.0,
            "unitprc": 2000.0
        }
         * */
        @SerializedName("prod")
        var product: String,
        @SerializedName("prodcode")
        var productCode: String,
        @SerializedName("unitcode")
        var unitCode: String?,
        var unit: String?,
        var photo: String?,
        @SerializedName("ValidForMonths")
        var validForMonths: Int?,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int?,
        @SerializedName("unitprc")
        var unitPrice: BigDecimal?,
        @SerializedName("StoclInHand")
        var stockInHand : BigDecimal? = ZERO,
        @SerializedName("prodtypcode")
        var productTypeCode:String?

) {
    override fun toString(): String {
        var returnData=""
        if(productTypeCode==Constant.SalesTaxType.F.name)
            returnData= getString(R.string.product_name) + " : \n" + product
        else{
            returnData = getString(R.string.product_name) + " : \n" + product + "\n\n" + getString(R.string.stock_in_hand) + " : " + getQuantity(stockInHand.toString())
        }
        return  returnData
    }
}