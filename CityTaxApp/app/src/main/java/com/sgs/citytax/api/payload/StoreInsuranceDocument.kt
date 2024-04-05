package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

data class StoreInsuranceDocument(
        @SerializedName("InsuranceID")
        var insuranceID: Int? = 0,
        @SerializedName("InsuranceTypeID")
        var insuranceTypeID: Int? = 0,
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("InsuranceNo")
        var insuranceNo: BigInteger? = null,
        @SerializedName("InsuranceDate")
        var insuranceDate: String? = null,
        @SerializedName("Insurer")
        var insurer: String? = null,
        @SerializedName("4rmdt")
        var fromDate: String? = null,
        @SerializedName("exprydt")
        var expiryDate: String? = null,
        @SerializedName("Cost")
        var cost: String? = null,
        @SerializedName("InvoiceReference")
        var invoiceReference: String? = null,
        @SerializedName("docid")
        var docid: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = null
)