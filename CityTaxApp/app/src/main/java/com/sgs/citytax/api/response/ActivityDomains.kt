package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CRMActivityDomain
import com.sgs.citytax.model.TaskCode

data class ActivityDomains(
        @SerializedName("ActivityDomainList")
        var activityDomains: List<CRMActivityDomain>? = arrayListOf(),
        @SerializedName("TaskCodeList")
        var taskCodes: List<TaskCode>? = arrayListOf()
)