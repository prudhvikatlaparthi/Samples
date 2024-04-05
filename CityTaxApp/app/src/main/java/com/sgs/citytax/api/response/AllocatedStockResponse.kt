package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AllocatedStock

data class AllocatedStockResponse(
        @SerializedName("Table")
        var allocatedStock: ArrayList<AllocatedStock> = arrayListOf()
)