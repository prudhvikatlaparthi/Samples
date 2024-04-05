package com.pru.dynamicapp.models
import com.google.gson.annotations.SerializedName


data class ViewItem(
    @SerializedName("access")
    val access: Boolean? = null,
    @SerializedName("className")
    val className: String? = null,
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("required")
    val required: Boolean? = null,
    @SerializedName("subtype")
    val subtype: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("child")
    val child: String? = null
)