package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ProportionalDutyOnRental(
        @SerializedName("RentalID")
        var rentalID: Int? = null,
        @SerializedName("CorporateTurnoverID")
        var corporateTurnoverID: Int? = null,
        @SerializedName("RentalStartDate")
        var rentalStartDate: String? = null,
        @SerializedName("RentPerRateCycle")
        var rentPerRateCycle: BigDecimal? = BigDecimal.ZERO
)