package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PenaltyWaiveOffReceiptChangeDetails(
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("BusinessName", alternate = ["PropertyName"])
        var businessName: String? = "",
        @SerializedName("SycotaxID", alternate = ["SycoTaxID","PropertySycoTaxID"])
        var sycoTaxId: String? = "",
        @SerializedName("BusinessOwnerID")
        var businessOwnerId: Int? = 0,
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("sec")
        var sector: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferanceNo: String? = "",
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("TaxAmount")
        var taxAmount: Double? = 0.0,
        @SerializedName("InvoiceDue")
        var invoiceDue: Double? = 0.0,
        @SerializedName("WaveOffID")
        var waiveOffId: Int? = 0,
        @SerializedName("WaveOffDate")
        var waiveOffDate: String? = "",
        @SerializedName("WaveOffAmount")
        var waiveOffAmount: Double? = 0.0,
        @SerializedName("WaveOffBy")
        var waiveOffBy: String? = "",
        @SerializedName("rmks")
        var remarks:String?="",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("Owners",alternate = ["PropertyOwners"])
        var businessOwners:String?="",
        @SerializedName("prodtypcode")
        var prodtypcode:String?="",
        @SerializedName("PropertyOwnerIDSycoTax")
        var propertyOwnerIDSycoTax:String?="",
        @SerializedName("IDCardNumbers")
        var iDCardNumbers:String?=""
)