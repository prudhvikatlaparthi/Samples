package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class SalesListResponse (
    @SerializedName("ReturnValue")
    val salesList: MutableList<SalesListReturn> = mutableListOf(),
    @SerializedName("ReturnType")
    var returnType: String? = "",
    @SerializedName("IsSuccess")
    var isSuccess: Boolean? = false,
    @SerializedName("msg")
    var msg: String? = "",
    @SerializedName("Schema")
    var schema: String? = "",
    @SerializedName("UTCDate")
    var utcDate: String? = "",
    @SerializedName("token")
    var token: String? = ""
)