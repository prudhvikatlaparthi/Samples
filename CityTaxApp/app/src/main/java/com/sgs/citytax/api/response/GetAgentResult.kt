package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CRMAgents

data class GetAgentResult(
        @SerializedName("VU_CRM_Agents")
        val crmAgents: ArrayList<CRMAgents>? = arrayListOf()
)