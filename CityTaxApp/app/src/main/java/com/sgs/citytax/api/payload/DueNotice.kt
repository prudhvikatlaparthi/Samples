package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DueNotice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("duenoticeid")
        var duenoticeid: Int? = 0,
        @SerializedName("filenameWithExt")
        var filenameWithExt: String? = "",
        @SerializedName("fileData")
        var fileData: String? = "",
        @SerializedName("reprtingdatetime")
        var reportingDateTime: String? = "",
        @SerializedName("recipntname")
        var recipientName: String? = "",
        @SerializedName("recipntmob")
        var mobileNumber: String? = ""
)