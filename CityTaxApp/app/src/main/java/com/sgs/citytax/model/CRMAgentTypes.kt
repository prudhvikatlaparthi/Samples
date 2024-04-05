package com.sgs.citytax.model

data class CRMAgentTypes(
        var AgentTypeID: Int? = 0,
        var AgentType: String? = ""
) {
    override fun toString(): String {
        return AgentType.toString()
    }
}