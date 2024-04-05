package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaskCode

data class TaxPayerResponse(
        @SerializedName("acctid", alternate = ["AccountID"])
        var accountID: Int = 0,
        @SerializedName("OrganizationID")
        var organizationID: Int = 0,
        @SerializedName("TaskCodes")
        var taskCodeList: List<TaskCode>? = arrayListOf(),
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0
)