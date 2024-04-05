package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaxNoticeDetail

data class TrackOnTaxNotice(
        @SerializedName("TaxNoticeDetails")
        var taxNoticeDetails: List<TaxNoticeDetail> = arrayListOf()
)