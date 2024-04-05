package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ServiceRequestTable(
        @SerializedName("cmts")
        val comments: String,
        @SerializedName("docid")
        val documentID: String,
        @SerializedName("CommentDate")
        var commentDate: String? = "",
        @SerializedName("ModifiedByName")
        val modifiedByName: String,
        @SerializedName("AWSPath")
        val aWSPath: String?
)