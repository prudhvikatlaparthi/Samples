package com.sgs.citytax.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VUCRMCustomerProductInterestLines(
        @SerializedName("TaxInvoiceLineID")
        var taxInvoiceLineID: Int? = 0,
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0,
        @SerializedName("EntityName")
        var entityName: String? = null,
        @SerializedName("vchrno")
        var vchrno: Int? = 0,
        @SerializedName("AttributeName")
        var attributeName: String? = null,
        @SerializedName("TaxableMatterName")
        var taxableMatterName: String? = null,
        @SerializedName("TaxableMatter")
        var taxableMatter: Double? = 0.0,
        @SerializedName("taxrt")
        var taxrt: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("unitcode")
        var unitcode: String? = null,
        @SerializedName("PaymentCycleID")
        var paymentCycleID: Int? = 0,
        @SerializedName("BillingCycleID")
        var billingCycleID: Int? = 0,
        @SerializedName("TaxPeriod")
        var taxPeriod: Int? = 0,
        @SerializedName("TaxAmount")
        var taxAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("crtd")
        var crtd: String? = null,
        @SerializedName("crtddt")
        var crtddt: String? = null,
        @SerializedName("mdfd")
        var mdfd: String? = null,
        @SerializedName("mdfddt")
        var mdfddt: String? = null,
        @SerializedName("OccupancyName")
        var occupancyName: String? = null,
        @SerializedName("DesignFilePath")
        var designFilePath: String? = null,
        @SerializedName("IsUpdateable")
        var isUpdateable: Boolean,
        @SerializedName("CustomProperties")
        var customProperties: String? = null,
        @SerializedName("DesignSource")
        var designSource: String? = null,
        var productCode: String? = "",
        var status: String? = "",
        var active: String? = "",
        var product: String? = "",
        @SerializedName("BillingCycleName")
        var billingCycleName: String? = "",
        @SerializedName("TaxableElement")
        var taxableElement: String? = "",
        @SerializedName("strtdt")
        var taxStartDate: String? = "",
        @SerializedName("Applied")
        var Applied: String? = "",
        @SerializedName("TurnOver")
        var turnOver: String? = "",
        var taxRuleBookCode:String?= "",
        @SerializedName("Market")
        var market: String? = ""
)