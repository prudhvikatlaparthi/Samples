package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.BookingRequestDetails
import com.sgs.citytax.model.BookingRequestReceiptDetails

data class BookingRequestReceiptResponse(
        @SerializedName("Table")
        var bookingRequestReceiptDetails: ArrayList<BookingRequestReceiptDetails> = arrayListOf(),
        @SerializedName("Table1")
        var bookingRequestDetails: ArrayList<BookingRequestDetails> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)