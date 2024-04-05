package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BusinessDueSummary(
        @SerializedName("CurrentInvoiceDue")
        var currentInvoiceDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("InitialOutstandingCurrentYearDue")
        var initialOutstandingCurrentYearDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CurrentYearDue")
        var currentYearDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CurrentYearPenaltyDue")
        var currentYearPenaltyDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AnteriorYearDue")
        var anteriorYearDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("AnteriorYearPenaltyDue")
        var anteriorYearPenaltyDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PreviousYearDue")
        var previousYearDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PreviousYearPenaltyDue")
        var previousYearPenaltyDue: BigDecimal? = BigDecimal.ZERO
)