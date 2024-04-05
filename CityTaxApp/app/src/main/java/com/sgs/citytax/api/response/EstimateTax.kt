package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VUCRMCustomerProductInterestLines

data class EstimateTax(
        @SerializedName("Head")
        val headList: HeadList,
        @SerializedName("Lines")
        val vucrmCustomerProductInterestLines: ArrayList<VUCRMCustomerProductInterestLines> = arrayListOf()
)