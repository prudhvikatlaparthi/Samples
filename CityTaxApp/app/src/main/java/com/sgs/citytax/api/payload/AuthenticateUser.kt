package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.util.getIPAddress

data class AuthenticateUser(
        var domain: String? = null,
        @SerializedName("username")
        var userName: String? = null,
        var password: String? = null,
        @SerializedName("ip")
        var ipAddress: String? = getIPAddress(),
        @SerializedName("langcode")
        var languageCode: String? = if (MyApplication.getPrefHelper().language.isEmpty()) "FR" else MyApplication.getPrefHelper().language,
        var token: String? = null,
        @SerializedName("sessionid")
        var sessionID: String? = null,
        @SerializedName("skipweblogincheck")
        var isSkipWebLoginCheck: Boolean = true,
        @SerializedName("long")
        var longitude: String? = null,
        @SerializedName("lat")
        var latitude: String? = null,
        @SerializedName("getkey")
        var getKey: Boolean = !MyApplication.getPrefHelper().isFirstAPICallDone,
        @SerializedName("dvccode")
        var deviceCode: String = MyApplication.getPrefHelper().serialNumber,
        @SerializedName("ver")
        var version: String = BuildConfig.VERSION_CODE.toString(),
        @SerializedName("appcd")
        var appcode: String = "AGENTAPP"
)
