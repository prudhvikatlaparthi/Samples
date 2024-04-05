package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TaxableMatterList(
        @SerializedName("Revenue")
        var revenue: BigDecimal? = null,
        @SerializedName("ShowCount")
        var showCount: Int? = null,
        @SerializedName("RoomNight")
        var roomNights: Int? = null
)