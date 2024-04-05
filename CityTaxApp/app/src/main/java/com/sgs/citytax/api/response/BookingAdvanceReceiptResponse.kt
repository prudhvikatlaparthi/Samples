package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.BookingAdvanceReceiptTable

data class BookingAdvanceReceiptResponse(
        @SerializedName("Table")
        var bookingAdvanceReceiptTable:ArrayList<BookingAdvanceReceiptTable> = arrayListOf(),
        @SerializedName("OrgData")
        val orgData: List<OrgData>? = null
)