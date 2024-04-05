package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CorporateTurnoverResponse(
        @SerializedName("CorporateTurnoverID")
        var corporateTurnoverID: Int? = 0,
        @SerializedName("RentalID")
        var RentalID: Int? = 0
)