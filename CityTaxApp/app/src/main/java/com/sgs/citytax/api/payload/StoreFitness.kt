package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class StoreFitness(
        @SerializedName("FitnessID")
        var fitnessID: Int? = 0,
        @SerializedName("FitnessTypeID")
        var fitnessTypeID: Int? = 0,
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("FitnessNo")
        var fitnessNo: Int? = 0,
        @SerializedName("FitnessDate")
        var fitnessDate: String? = null,
        @SerializedName("Vendor")
        var vendor: String? = null,
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