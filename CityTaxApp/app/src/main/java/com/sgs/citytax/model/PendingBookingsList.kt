package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PendingBookingsList(
        @SerializedName("BookingRequestID")
        var bookingRequestId: Int? = 0,
        @SerializedName("BookingRequestDate")
        var bookingRequestDate: String? = "",
        @SerializedName("Customer")
        var customer: String? = "",
        @SerializedName("brname")
        var branchName: String? = "",
        @Transient
        var isLoading: Boolean = false
)