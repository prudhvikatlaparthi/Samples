package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class LicenseRenewalPayload(
        var context : SecurityContext = SecurityContext(),
        @SerializedName("licenseid")
        var licenseId : Int ?= 0,
        @SerializedName("pageindex")
        var pageindex : Int ?= 0,
        @SerializedName("pagesize")
        var pagesize : Int ?= 10
)