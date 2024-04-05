package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AgentLocations(
        @SerializedName("Agent")
        var agent: String? = null,
        @SerializedName("AgentType")
        var agentType: String? = null,
        @SerializedName("AgentTypeCode")
        var agentTypeCode: String,
        @SerializedName("AgentCode")
        var agentCode: String? = null,
        @SerializedName("Mobile")
        var mobile: String? = null,
        @SerializedName("Email")
        var email: String? = null,
        @SerializedName("Zone")
        var zone: String? = null,
        @SerializedName("Sector")
        var sector: String? = null,
        @SerializedName("ReportingManager")
        var reportingManager: String? = null,
        @SerializedName("MunicipalWalletBalance")
        var municipalWalletBalance: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CashInHand")
        var cashInHand: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CommissionEarned")
        var commissionEarned: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CommissionsDisbursed")
        var commissionsDisbursed: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CommissionsBalance")
        var commissionsBalance: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("DeviceID")
        var deviceID: String? = null,
        @SerializedName("LastPingTime")
        var lastPingTime: String? = null,
        @SerializedName("Latitude")
        var latitude: String? = null,
        @SerializedName("Longitude")
        var longitude: String? = null,
        @SerializedName("Color")
        var color: String? = null,
        @SerializedName("Legend")
        var legend: String? = null,
        @SerializedName("AssignedZoneCode")
        var assignedZoneCode: String = "",
        @SerializedName("AccountID")
        var agentAccountID: Int? = 0
) {

        //Swetha - Changes made when, removed Marker popup and replaced with Custom dialog - AgentLiveTracking - 10/3/2022
    /*fun getMarkerInfo(context: Context): String {
        *//*Inspector login - wallet balanace(No),commission earned(No),Cash In hand(Yes),Commission Disbursed(No),Commission Balance(No)

        Supervsior login - wallet balanace(No),commission earned(No),Cash In hand(Yes),Commission Disbursed(No),Commission Balance(No)

        Municipal Agent login - wallet balanace(No),commission earned(No),Cash In hand(Yes),Commission Disbursed(No),Commission Balance(No)

        Prepaid login - wallet balanace(Yes),commission earned(Yes),Cash In hand(No),Commission Disbursed(Yes),Commission Balance(Yes)

        PayPoints login - wallet balanace(Yes),commission earned(Yes),Cash In hand(No),Commission Disbursed(Yes),Commission Balance(Yes)
        *//*

        val zone: String = if (assignedZoneCode == Constant.AssignedZoneSectorCode.AZ.name) getString(R.string.all_zone)
        else if (assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) getString(R.string.no_zone)
        else zone ?: ""

        val sector: String = if (assignedZoneCode == Constant.AssignedZoneSectorCode.AZ.name) getString(R.string.all_sector)
        else if (assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) getString(R.string.no_sector)
        else sector ?: ""


        var markerInfo = context.resources.getString(R.string.agent_type) + " : ${agentType ?: ""}\n" +
                context.resources.getString(R.string.agent_code) + " : ${agentCode ?: ""}\n" +
                context.resources.getString(R.string.mobile) + " : ${mobile ?: ""}\n" +
                context.resources.getString(R.string.email) + " : ${email ?: ""}\n" +
                context.resources.getString(R.string.zone) + " : ${zone}\n" +
                context.resources.getString(R.string.sector) + " : ${sector}\n" +
                context.resources.getString(R.string.reporting_manager) + " : ${reportingManager ?: ""}\n"

        if (agentTypeCode == Constant.AgentTypeCode.ISP.name || agentTypeCode == Constant.AgentTypeCode.SPR.name|| agentTypeCode == Constant.AgentTypeCode.LEI.name||agentTypeCode == Constant.AgentTypeCode.LES.name) {
            markerInfo = markerInfo +
                    context.resources.getString(R.string.cash_in_hand) + " : ${formatWithPrecision(cashInHand)}"
        } else if (agentTypeCode == Constant.AgentTypeCode.TPA.name || agentTypeCode == Constant.AgentTypeCode.PPS.name) {
            markerInfo = markerInfo +
                    context.resources.getString(R.string.municipal_wallet_balance) + " : ${formatWithPrecision(municipalWalletBalance)}\n" +
                    context.resources.getString(R.string.commission_earned) + " : ${formatWithPrecision(commissionEarned)}\n" +
                    context.resources.getString(R.string.commission_disbursed) + " : ${formatWithPrecision(commissionsDisbursed)}\n" +
                    context.resources.getString(R.string.commission_balance) + " : ${formatWithPrecision(commissionsBalance)}"
        } else if (agentTypeCode == Constant.AgentTypeCode.MCA.name||agentTypeCode == Constant.AgentTypeCode.LEA.name
                || MyApplication.getPrefHelper().isParkingMunicipalAgent()|| MyApplication.getPrefHelper().isParkingThirdPartyAgent()) {
            markerInfo = markerInfo +
                    context.resources.getString(R.string.cash_in_hand) + " : ${formatWithPrecision(cashInHand)}"
        }

        return markerInfo
    }*/

    override fun toString(): String {
        return "AgentLocations(agent=$agent)"
    }
}
