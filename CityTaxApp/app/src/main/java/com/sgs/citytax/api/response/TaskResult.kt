package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName


data class TaskResult(
        @SerializedName("VU_CRM_ServiceRequests")
        val serviceRequests: ArrayList<VUCRMServiceRequest>? = arrayListOf()
)