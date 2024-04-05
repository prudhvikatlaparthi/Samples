package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

class AgentDetails {
    @SerializedName("AgentID")
    var agentID: Int = 0
        get() = field ?: 0
    @SerializedName("AgentTypeID")
    var agentTypeID: Int = 0
        get() = field ?: 0
    @SerializedName("saluttn")
    var salutation: String = ""
        get() = field ?: ""
    @SerializedName("frstname")
    var firstName: String = ""
        get() = field ?: ""
    @SerializedName("mddlename")
    var middleName: String = ""
        get() = field ?: ""
    @SerializedName("lastname")
    var lastName: String = ""
        get() = field ?: ""
    @SerializedName("ParentAgentID")
    var parentAgentID: Int = 0
        get() = field ?: 0
    @SerializedName("AgentUserID")
    var agentUserID: String = ""
        get() = field ?: ""
    @SerializedName("ownrorgbrid")
    var ownerOrgBranchID: Int = 0
        get() = field ?: 0
    @SerializedName("email")
    var email: String = ""
        get() = field ?: ""
    @SerializedName("mob")
    var mobile: String = ""
        get() = field ?: ""
    @SerializedName("telcode")
    var telcode: String = ""
        get() = field ?: ""
    @SerializedName("pwd")
    var password: String = ""
        get() = field ?: ""
    @SerializedName("AgentType")
    var agentType: String = ""
        get() = field ?: ""
    @SerializedName("brname")
    var branchName: String = ""
        get() = field ?: ""
    @SerializedName("AgentName")
    var agentName: String = ""
        get() = field ?: ""
    @SerializedName("ParentAgentName")
    var parentAgentName: String = ""
        get() = field ?: ""
    @SerializedName("4rmdt")
    var fromDate: String = ""
        get() = field ?: ""
    @SerializedName("2dt")
    var toDate: String = ""
        get() = field ?: ""
    @SerializedName("tgtamt")
    var targetAmount: Double = 0.0
        get() = field ?: 0.0
    @SerializedName("CollectionAmount")
    var collectionAmount: Double = 0.0
        get() = field ?: 0.0
    @SerializedName("acctid")
    var accountID: Int = 0
        get() = field ?: 0
    @SerializedName("acctname")
    var accountName: String = ""
        get() = field ?: ""
    @SerializedName("SuperiorTo")
    var superiorTo: String = ""
        get() = field ?: ""
    @SerializedName("Prepaid")
    var prepaid: Char = 'N'
        get() = field ?: 'N'
    @SerializedName("AgentTypeCode")
    var agentTypeCode: String = ""
        get() = field ?: ""
    @SerializedName("AllowSales")
    var allowSales: String = ""
        get() = field ?: ""

    @SerializedName("IsAllowCombinedPayoutRequest")
    var allowCombinedPayoutRequest: Char = 'N'
        get() = field ?: 'N'

    @SerializedName("AllowParking")
    var allowParking: String = "N"
        get() = field ?: "N"
    @SerializedName("AllowParkingCounts")
    var allowParkingCounts: Int = 0
        get() = field ?: 0

    @SerializedName("AllowPropertyTaxCollection")
    var allowPropertyTaxCollection: String = ""
        get() = field ?: ""

    @SerializedName("AssignedZoneCode")
    var assignedZoneCode: String = ""
        get() = field ?: ""
    @SerializedName("IsApprover")
    var isApprover: String = ""
        get() = field ?: ""
}
