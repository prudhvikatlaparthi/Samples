package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class MethodReturn<T>(
        @SerializedName("ReturnValue")
        var returnValue: T? = null,
        @SerializedName("ReturnType")
        var returnType: Any? = null,
        @SerializedName("ReturnValue1")
        var returnValue1: T? = null,
        @SerializedName("ReturnValue2")
        var returnValue2: String? = null,
        @SerializedName("ReturnType1")
        var returnType1: String? = null,
        @SerializedName("ReturnType2")
        var returnType2: String? = null,
        @SerializedName("IsSuccess")
        var isSuccess: Boolean = false,
        var msg: String? = null,
        @SerializedName("Schema")
        var schema: String? = null,
        @SerializedName("UTCDate")
        var utcDate: String? = null,
        var token: String? = null
)