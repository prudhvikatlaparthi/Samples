package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.InventoryStatus

data class InventoryStatusResponse(
        @SerializedName("Table")
        var inventoryStatus: ArrayList<InventoryStatus> = arrayListOf()
)