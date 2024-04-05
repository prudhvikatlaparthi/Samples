package com.sgs.citytax.api.payload

import android.os.Build
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.base.MyApplication

data class UpdateConnectedDevice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("connecteddevice")
        var connectedDevice: List<ConnectedDevice>? = null
)

data class ConnectedDevice(
        @SerializedName("dvccode")
        var deviceCode: String = MyApplication.getPrefHelper().serialNumber,
        @SerializedName("dvcid")
        var deviceID: String = MyApplication.getPrefHelper().serialNumber,
        @SerializedName("dvcname")
        var deviceName: String = Build.MODEL,
        @SerializedName("dvctyp")
        var deviceType: String = "M",
        @SerializedName("lastpngtime")
        var lastPingTime: String = "",
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int = MyApplication.getPrefHelper().userOrgBranchID,
        @SerializedName("ver")
        var version: String = BuildConfig.VERSION_CODE.toString(),
        @SerializedName("appcd")
        var appcode: String = "AGENTAPP",
        @SerializedName("lat")
        var latitude: Double = 0.0,
        @SerializedName("long")
        var longitude: Double = 0.0,
        var loggedInUserID: String = MyApplication.getPrefHelper().loggedInUserID
)