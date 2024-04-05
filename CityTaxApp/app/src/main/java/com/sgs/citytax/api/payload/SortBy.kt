package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class SortBy (
    @SerializedName("colname")
    var colname:String? = "",
    @SerializedName("IsASC")
    var IsASC:Boolean? = false
)