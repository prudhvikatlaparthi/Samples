package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.LicenseRenewalModel
import com.sgs.citytax.model.TransactionHistoryGenModel

data class LicenseRenewalResp(

        @SerializedName("LicenseRenewalHistory")
        var transactions: ArrayList<LicenseRenewalModel> = arrayListOf(),
        @SerializedName("PageSize")
        var pageSize: Int? = 10,
        @SerializedName("PageIndex")
        var pageIndex: Int? = 1,
        @SerializedName("TotalRecords")
        var totalRecords: Int? = 0
)
