package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class VuCrmIndividualTaxes(
        @SerializedName("VoucherNo")
        var VoucherNo: Int,
        @SerializedName("AccountID")
        var AccountID: Int,
        @SerializedName("TypeName")
        var TypeName: String,
        @SerializedName("SycotaxID")
        var SycotaxID: String,
        @SerializedName("ProductCode")
        var ProductCode: String,
        @SerializedName("Product")
        var Product: String,
        @SerializedName("PricingRuleID")
        var PricingRuleID: Int,
        @SerializedName("PaymentCycleID")
        var PaymentCycleID: Int,
        @SerializedName("BillingCycleID")
        var BillingCycleID: Int,
        @SerializedName("TaxRuleBookCode")
        var TaxRuleBookCode: String,
        @SerializedName("TaxRuleBookID")
        var TaxRuleBookID: Int,
        @SerializedName("MultiInvoice")
        var MultiInvoice: String,
        @SerializedName("IsDefault")
        var IsDefault: String
){
        override fun toString(): String {
                return "$Product \n$SycotaxID"
        }
}