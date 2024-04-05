package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PaymentCycle
import com.sgs.citytax.model.Tenure
import java.math.BigDecimal

data class BookingTenureBookingAdvanceResponse(
        @SerializedName("BookingAdvance")
        var bookingAdvance: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("Tenure")
        var tenures: List<Tenure>? = arrayListOf(),
        @SerializedName("paymentcycle")
        var paymentCycles: List<PaymentCycle>? = arrayListOf(),
        @SerializedName("SecurityDeposit")
        var securityDeposit: BigDecimal? = BigDecimal.ZERO
)