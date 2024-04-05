package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName

data class GetMessageConnectionStringResponse(
    @SerializedName("ConnectTimeout")
    val connectTimeout: String?,
    @SerializedName("Connection")
    val connection: String?,
    @SerializedName("PublicConnection")
    val publicConnection: String?,
    @SerializedName("Enabled")
    val enabled: String?,
    @SerializedName("KeyExpiry")
    val keyExpiry: String?,
    @SerializedName("Password")
    val password: String?,
    @SerializedName("SyncTimeout")
    val syncTimeout: String?
)