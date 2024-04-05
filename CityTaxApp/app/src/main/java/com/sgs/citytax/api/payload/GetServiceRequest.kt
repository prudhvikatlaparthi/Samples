package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext


data class GetServiceRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("svcreqno")
        var serviceRequestNo: String? = null,
        @SerializedName("acctid")
        var accountID: String? = null,
        @SerializedName("isservicetax")
        var isServiceTax: Boolean? = null
)