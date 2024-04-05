package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMAdvertisementTypes(
        @SerializedName("AdvertisementTypeID")
        var advertisementTypeId:Int?=0,
        @SerializedName("AdvertisementTypeName")
        var advertisementTypeName:String?="",
        @SerializedName("AdvertisementTypeCode")
        var advertisementTypeCode:String?="",
        @SerializedName("unitcode")
        var unitcode: String? = "",
        @SerializedName("MinArea")
        var minArea: String? = "",
        @SerializedName("MaxArea")
        var maxArea: String? = ""
)
{
    override fun toString(): String {
        return advertisementTypeName.toString()
    }
}