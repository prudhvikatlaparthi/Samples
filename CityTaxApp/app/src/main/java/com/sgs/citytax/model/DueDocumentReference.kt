package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DueDocumentReference(
        @SerializedName("duenoticeid")
        var duenoticeid: Int? = 0,
        @SerializedName("filenameWithExt")
        var filenameWithExt: String? = "",
        @SerializedName("fileData")
        var data: String? = ""
)