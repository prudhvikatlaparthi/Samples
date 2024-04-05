package com.sgs.citytax.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaxDueList (
    @SerializedName("DueNoticeID")
    var dueNoticeID: Int? = null,
    @SerializedName("DueNoticeDate")
    var dueNoticeDate: String? = "",
    @SerializedName("RequestNo")
    var requestNo: Int? = null,
    @SerializedName("NoticeReferenceNo")
    var noticeReferenceNo: String? = "",
    @SerializedName("AccountID")
    var accountID: Int? = 0
): Parcelable {


    override fun toString(): String {
        return noticeReferenceNo.toString()
    }


}