package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class OutstandingWaiveOffReceiptDetails(
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("BusinessName",alternate =  ["PropertyName"])
        var businessName: String? = "",
        @SerializedName("SycotaxID",alternate = ["SycoTaxID","PropertySycotaxID"])
        var sycoTaxId: String? = "",
        @SerializedName("Owners",alternate = ["PropertOwners"])
        var businessOwner: String? = "",
        @SerializedName("prodtypcode")
        var prodTypeCode: String? = "",
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
        var city: String = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("OutstandingType")
        var outstandingType: String? = "",
        @SerializedName("InitialOutstandingID")
        var initialOutStandingId: Int? = 0,
        @SerializedName("Year")
        var year: Int? = 0,
        @SerializedName("OutstandingAmount")
        var outstandingAmount: Double? = 0.0,
        @SerializedName("OutstandingDueAmount")
        var outstandingDueAmount: Double? = 0.0,
        @SerializedName("OutstandingWaveOffID")
        var outstandingWaiveOffId: Int? = 0,
        @SerializedName("OutstandingWaveOffDate")
        var outstandingWaiveOffDate: String? = "",
        @SerializedName("WaveOffAmount")
        var waiveOffAmount: Double? = 0.0,
        @SerializedName("WaveOffBy")
        var waivedOffBy: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("DueAfterWaveOff")
        var dueAfterWaveOff: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PropertyOwnerIDSycoTax")
        var propertyOwnerIDSycoTax:String?="",
        @SerializedName("IDCardNumbers")
        var iDCardNumbers:String?=""
)