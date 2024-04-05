package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class SaveAgreement(
    var context: SecurityContext = SecurityContext(),
    @SerializedName("dueagreements")
    var dueagreements: Dueagreements? = null

)

data class Dueagreements(
    @SerializedName("DueAgreementID")
    var dueAgreementID: Int? = 0,
    @SerializedName("DueAgreementDate")
    var dueAgreementDate: String? = null,
    @SerializedName("DueNoticeID")
    var dueNoticeID: Int? = 0,
    @SerializedName("docid")
    var docid: Int? = 0,
    @SerializedName("ValidUptoDate")
    var validup2dt: String? = null,
    @SerializedName("refno")
    var refno: String? = null,
    @SerializedName("rmks")
    var rmks: String? = null,
    @SerializedName("LegalAgreementNo")
    var legalAgreementNo: String? = null,
    @SerializedName("stscode")
    var stscode: String? = null,
    @SerializedName("FileNameWithExt")
    var fileNameWithExt: String? = null,
    @SerializedName("filedata")
    var filedata: String? = null
)