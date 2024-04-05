package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class ImpondmentMapFilterData (
    @SerializedName("VehicleNo")
    var vehicleNo:String?=null,
    @SerializedName("Phone")
    var phone:String?=null,
    @SerializedName("ViolationType")
    var violationType:String?=null,
    @SerializedName("ViolationSubType")
    var violationSubType:String?=null,
    @SerializedName("ImpoundmentType")
    var impoundmentType:String?=null,
    @SerializedName("ImpoundmentSubType")
    var impoundmentSubType:String?=null
    )