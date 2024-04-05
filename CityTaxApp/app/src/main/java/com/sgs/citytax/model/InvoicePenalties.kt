package com.sgs.citytax.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class InvoicePenalties(
        @SerializedName("PenaltyID")
        var penaltyID: Int? = 0,
        @SerializedName("PenaltyDate")
        var penaltyDate: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("PenaltyAmount")
        var penaltyAmount: BigDecimal? = null,
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = null,
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = ""
) : Parcelable