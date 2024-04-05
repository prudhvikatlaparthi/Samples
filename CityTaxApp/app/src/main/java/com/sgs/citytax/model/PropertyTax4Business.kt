package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PropertyTax4Business(
        @SerializedName("SycotaxID")
        var sycotaxID: String? = "",
        @SerializedName("PropertySycoTaxID")
        var propertySycoTaxID: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("RegistrationDate")
        var registrationDate: String? = "",
        @SerializedName("PropertyType")
        var propertyType: String? = "",
        @SerializedName("sts")
        var status: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("prodcode")
        var prodcode: String? = "",
        @SerializedName("proprtyid")
        var proprtyId: Int? = 0,
        @SerializedName("PropertyTypeID")
        var propertyTypeID: Int? = 0,
        @SerializedName("EstimatedTax")
        var estimatedTax: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("IsInvoiceGenerated")
        var isInvoiceGenerated: Boolean? = false,
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("DocDetails")
        var documentDetails: DocDetails?  = null
//        @SerializedName("OwnerDetails")
//        var ownerDetails: BusinessPropertyOwners? = null
)