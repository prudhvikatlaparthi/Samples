package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

class BusinessDues {
    @SerializedName("BusinessDueSummary")
    val businessDueSummary: ArrayList<BusinessDueSummary> = arrayListOf()
}
