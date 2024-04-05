package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class DateFilter (
    @SerializedName("DtColumnName")
    var dateColumnName:String = "",
    @SerializedName("strtdt")
    var startDate:String = "",
    @SerializedName("enddt")
    var endDate:String = ""
)