package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class BusinessMapFilterdata (
       @SerializedName("SycoTaxID")
       var sycoTaxID:String?=null ,
       @SerializedName("BusinessName")
       var businessName:String?=null,
       @SerializedName("Phone")
       var phone:String?=null,
       @SerializedName("Email")
       var email:String?=null,
       @SerializedName("TaxType")
       var taxType:String?=null,
       @SerializedName("TaxSubType")
       var taxSubType:String?=null,
       @SerializedName("YearOfOnboard")
       var yearOfOnboard:String?=null,
       @SerializedName("MonthOfOnboard")
       var monthOfOnboard:String?=null,
       @SerializedName("Zone")
       var zone:String?=null,
       @SerializedName("Sector")
       var sector:String?=null,
       @SerializedName("ActivityDomain")
       var activityDomain:String?=null,
       @SerializedName("ActivityClass")
       var activityClass:String?=null
        )