package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.DataTaxableMatter

data class GetEstimatedTaxForProduct(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerID: Int? = 0,
        @SerializedName("tskcode")
        var taskCode: String? = "",
        @SerializedName("entitypricingvoucherno")
        var entityPricingVoucherNo: String? = null,
        @SerializedName("strtdt")
        var startDate: String? = "",
        @SerializedName("dttaxablematter")
        var dataTaxableMatter: ArrayList<DataTaxableMatter>? = arrayListOf(),
        @SerializedName("isindividualtax")
        var isIndividualTax: Boolean? = false
)