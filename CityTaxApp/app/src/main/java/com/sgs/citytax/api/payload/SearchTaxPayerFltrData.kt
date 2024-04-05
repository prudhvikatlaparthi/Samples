package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class SearchTaxPayerFltrData (
    @SerializedName("SycoTaxID")
    var SycoTaxID:String? = null,
    @SerializedName("BusinessName")
    var BusinessName:String? = null,
    @SerializedName("Email")
    var Email:String? = null,
    @SerializedName("Phone")
    var Phone:String? = null,
    @SerializedName("TaxType")
    var TaxType:String? = null,
    @SerializedName("TaxSubType")
    var TaxSubType:String? = null,
    @SerializedName("YearOfOnboard")
    var YearOfOnboard:String? = null,
    @SerializedName("MonthOfOnboard")
    var MonthOfOnboard:String? = null,
    @SerializedName("Zone")
    var Zone:String? = null,
    @SerializedName("Sector")
    var Sector:String? = null,
    @SerializedName("ActivityDomain")
    var ActivityDomain:String? = null,
    @SerializedName("ActivityClass")
    var ActivityClass:String? = null
)