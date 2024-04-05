package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPendingViolationImpoundmentList(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("pageindex")
        var pageIndex: Int? = 0,
        @SerializedName("pagesize")
        var pageSize: Int? = 0,
        @SerializedName("filtertype")
        var filterType: String? = "",
        @SerializedName("typeid")
        var typeID: Int? = 0,
        @SerializedName("subtypeid")
        var subTypeID: Int? = 0,
        @SerializedName("vehiclefilter")
        var vehicleFilter: String? = "",
        @SerializedName("Vehiclefilterstring")
        var vehicleFilterString: String? = "",
        @SerializedName("mobilefilter")
        var mobileFilter: String? = "",
        @SerializedName("mobilefilterstring")
        var mobileFilterString: String? = ""
)