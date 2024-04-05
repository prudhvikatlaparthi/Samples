package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AssetMaintenanceResponse(
        @SerializedName("VU_AST_AssetMaintenance")
        var assetMaintenanceData: ArrayList<AssetMaintenanceData> = arrayListOf()
)