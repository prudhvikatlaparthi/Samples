package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.base.MyApplication

data class CommissionDetails(
        @SerializedName("advdt")
        var advanceDate: String? = "",
        @SerializedName("acctid")
        var accountId: Int? = 0,
        @SerializedName("refno")
        var referenceNo: String? = "",
        @SerializedName("refdt")
        var referanceDate: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "Requesting for Commission Payout",
        @SerializedName("netpaybl")
        var netPayable: Double? = 0.0,
        @SerializedName("usrorgbrid")
        var userOrgBranchId: Int? = MyApplication.getPrefHelper().userOrgBranchID,
        @SerializedName("partyglcode")
        var partyGlCode: String? = "",
        @SerializedName("advpdid")
        var advancePaidId: Int? = 0,
        @SerializedName("apprvddt")
        var approvedDate: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("approvedbyaccountid")
        var approvedByAccountId: Int? = 0,
        @SerializedName("isselfrecharge")
        var isSelfRecharge: String? = "Y"

)