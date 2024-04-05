package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AuthenticateUserResponse(
        var domain: String,
        @SerializedName("CurrencySymbol")
        var currencySymbol: String,
        @SerializedName("crncycode")
        var currencyCode: String,
        @SerializedName("cultrcode")
        var cultrCode: String,
        @SerializedName("OrganizationCurrencyPrecision")
        var currencyPrecision: Int,
        @SerializedName("rlcode")
        var roleCode: String,
        @SerializedName("loggeduserid")
        var loggedUserId: String,
        @SerializedName("AdminUser")
        var adminUser: Boolean = false,
        @SerializedName("usrorgid")
        var userOrgId: Int,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int,
        @SerializedName("AgentDetails")
        var agentDetails: AgentDetails = AgentDetails(),
        @SerializedName("AppSessionTimeOut")
        var appSessionTimeOut: Int,
        @SerializedName("crncy")
        var currency: String,
        @SerializedName("CopyrightReport")
        var copyrightReport: String,
        @SerializedName("istrl")
        var rightSide:Boolean=false,
        @SerializedName("symbatryt")
        var currencySymbolAtRight:Boolean=false,
        @SerializedName("LoginCounts")
        var loginCounts: Int = 0,
        @SerializedName("AuthenticatorSecretKey")
        var authSecertKey: String? = "",
        @SerializedName("LastStreamEntryID")
        var lastStreamEntryID: String? = ""
)