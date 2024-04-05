package com.sgs.citytax.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HandoverDueNoticesList(
        @SerializedName("DueNoticeID")
        var dueNoticeID: Int? = 0,
        @SerializedName("DueNoticeDate")
        var dueNoticeDate: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo : String ?= null,
        @SerializedName("DueNoticeType")
        var dueNoticeType : String ?= null,
        @SerializedName("StatusCode")
        var statusCode: String? = "",
        @SerializedName("status")
        var status: String? = "",
        @SerializedName("AccountID")
        var accountID: Int? = 0,
        @SerializedName("HandoverDate")
        var handoverDate: String? = "",
        @SerializedName("DocumentID")
        var documentID: Int? = 0,
        @SerializedName("Year")
        var year: Int? = 0

)