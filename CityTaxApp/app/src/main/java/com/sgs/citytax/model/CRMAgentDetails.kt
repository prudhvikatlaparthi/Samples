package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMAgentDetails(
        @SerializedName("AgentName")
        var agentname: String? = null,
        @SerializedName("brname")
        var branchname: String? = null,
        @SerializedName("AgentType")
        var agenttype: String? = null,
        @SerializedName("email")
        var email: String? = null,
        @SerializedName("mob")
        var mobile: String? = null,
        @SerializedName("saluttn")
        var salutation: String? = null,
        @SerializedName("frstname")
        var firstname: String? = null,
        @SerializedName("mddlename")
        var middlename: String? = null,
        @SerializedName("lastname")
        var lastname: String? = null,
        @SerializedName("ParentAgentID")
        var parentagentid: Int = 0,
        @SerializedName("AgentUserID")
        var agentUserid: String? = null,
        @SerializedName("ParentAgentName")
        var parentagentname: String? = null,
        @SerializedName("AgentID")
        var agentid: Int = 0,
        @SerializedName("AgentTypeID")
        var agenttypeid: Int = 0,
        @SerializedName("ownrorgbrid")
        var ownerorgbranchid: Int = 0,
        @SerializedName("pwd")
        var password: String? = "",
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = "",
        @SerializedName("tgtamt")
        var targetAmount: String? = "",
        @SerializedName("telcode")
        var telephonicCode: String? = "",
        var CollectionAmount: String? = "",
        var AssociationCollection: String? = "",
        @SerializedName("HotelDesFinanceID")
        var hotelDesFinanceID: Int? = 0,


        var CommissionEarned: String? = "",
        var CommissionDisbursed: String? = "",
        var CommissionBalance: String? = "",
        @SerializedName("stscode", alternate = ["StatusCode"])
        var statusCode: String? = "",
        @SerializedName("sts", alternate = ["Status"])
        var status: String? = null,
        @SerializedName("AgentCode")
        var agentCode: String? = null,
        @SerializedName("rmks")
        var remarks: String? = null,
        @SerializedName("VerifiedByUserID")
        var verifiedByUserID: String? = "",
        @SerializedName("CreatedByAccountID")
        var createdByAccountID: Int = 0,
        @SerializedName("AssignedZoneCode")
        var assignedZoneCode: String? = null,
) {
        override fun toString(): String {
                return agentname.toString()
        }
}