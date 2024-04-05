package com.sgs.citytax.util

import android.content.SharedPreferences
import android.os.Build
import com.sgs.citytax.R
import com.sgs.citytax.base.MyApplication
import java.util.UUID.randomUUID

class PrefHelper {

    //region Private

    private var sharedPref: SharedPreferences = MyApplication.getPreferences()
    private var editor: SharedPreferences.Editor = MyApplication.getEditor()

    private fun getPrefString(key: String): String = sharedPref.getString(key, "") ?: ""
    private fun getPrefString(key: Int): String = sharedPref.getString(getStringForPref(key), "") ?: ""
    private fun getPrefInt(key: String): Int = sharedPref.getInt(key, 0)
    private fun getPrefBoolean(key: String): Boolean = sharedPref.getBoolean(key, false)
    private fun getPrefBoolean(key: Int): Boolean = sharedPref.getBoolean(getStringForPref(key), false)
    private fun getPrefFloat(key: String): Float = sharedPref.getFloat(key, 0.0f)
    private fun setPref(key: String, value: String) = editor.putString(key, value).apply()
    private fun setPref(key: Int, value: String) = editor.putString(getStringForPref(key), value).apply()
    private fun setPref(key: String, value: Int) = editor.putInt(key, value).apply()
    private fun setPref(key: String, value: Boolean) = editor.putBoolean(key, value).apply()
    private fun setPref(key: Int, value: Boolean) = editor.putBoolean(getStringForPref(key), value).apply()
    private fun setPref(key: String, value: Float) = editor.putFloat(key, value).apply()

    //endregion

    //region Settings

    var language: String
        get() = getPrefString(R.string.pref_language)
        set(value) = setPref(R.string.pref_language, value)

    var isFromHistory: Boolean
        get() = getPrefBoolean(R.string.pref_history)
        set(value) = setPref(R.string.pref_history, value)

    var currency: String
        get() = getPrefString(R.string.pref_currency)
        set(value) = setPref(R.string.pref_currency, value)

    var isSearchEnabled: Boolean
        get() = getPrefBoolean(R.string.pref_search_mode)
        set(value) = setPref(R.string.pref_search_mode, value)

    var isScanEnabled: Boolean
        get() = getPrefBoolean(R.string.pref_scan_mode)
        set(value) = setPref(R.string.pref_scan_mode, value)

    var isCashEnabled: Boolean
        get() = getPrefBoolean(R.string.pref_payment_cash)
        set(value) = setPref(R.string.pref_payment_cash, value)

    var isOrangeWalletPaymentEnabled: Boolean
        get() = getPrefBoolean(R.string.pref_payment_orange_wallet)
        set(value) = setPref(R.string.pref_payment_orange_wallet, value)

    var printEnabled: Boolean
        get() = getPrefBoolean(R.string.pref_allow_print)
        set(value) = setPref(R.string.pref_allow_print, value)

    var qrCodeEnabled: Boolean
        get() = getPrefBoolean(R.string.pref_allow_qr_code)
        set(value) = setPref(R.string.pref_allow_qr_code, value)

    var showTaxNotice: Boolean
        get() = getPrefBoolean(R.string.pref_show_tax_notice)
        set(value) = setPref(R.string.pref_show_tax_notice, value)

    //endregion

    //region Agent
    var agentID: Int
        get() = getPrefInt("AgentID")
        set(value) = setPref("AgentID", value)
    var agentTypeID: Int
        get() = getPrefInt("AgentTypeID")
        set(value) = setPref("AgentTypeID", value)
    var agentSalutation: String
        get() = getPrefString("AgentSalutation")
        set(value) = setPref("AgentSalutation", value)
    var agentFName: String
        get() = getPrefString("agentFName")
        set(value) = setPref("agentFName", value)
    var agentMName: String
        get() = getPrefString("agentMName")
        set(value) = setPref("agentMName", value)
    var agentLName: String
        get() = getPrefString("agentLName")
        set(value) = setPref("agentLName", value)
    var agentEmail: String
        get() = getPrefString("AgentEmail")
        set(value) = setPref("AgentEmail", value)
    var agentMobile: String
        get() = getPrefString("AgentMobile")
        set(value) = setPref("AgentMobile", value)
    var agentContryCode: String
        get() = getPrefString("ContryCode")
        set(value) = setPref("ContryCode", value)
    var agentPassword: String
        get() = getPrefString("AgentPassword")
        set(value) = setPref("AgentPassword", value)
    var parentAgentID: Int
        get() = getPrefInt("ParentAgentID")
        set(value) = setPref("ParentAgentID", value)
    var agentUserID: String
        get() = getPrefString("AgentUserID")
        set(value) = setPref("AgentUserID", value)
    var agentOwnerOrgBranchID: Int
        get() = getPrefInt("AgentOwnerOrgBranchID")
        set(value) = setPref("AgentOwnerOrgBranchID", value)
    var agentType: String
        get() = getPrefString("AgentType")
        set(value) = setPref("AgentType", value)
    var agentTypeCode: String
        get() = getPrefString("AgentTypeCode")
        set(value) = setPref("AgentTypeCode", value)
    var agentBranch: String
        get() = getPrefString("AgentBranch")
        set(value) = setPref("AgentBranch", value)
    var agentName: String
        get() = getPrefString("AgentName")
        set(value) = setPref("AgentName", value)
    var parentAgentName: String
        get() = getPrefString("ParentAgentName")
        set(value) = setPref("ParentAgentName", value)
    var agentFromDate: String
        get() = getPrefString("AgentFromDate")
        set(value) = setPref("AgentFromDate", value)
    var agentToDate: String
        get() = getPrefString("AgentToDate")
        set(value) = setPref("AgentToDate", value)
    var agentTargetAmount: String
        get() = getPrefString("AgentTargetAmount")
        set(value) = setPref("AgentTargetAmount", value)
    var agentCollectionAmount: String
        get() = getPrefString("AgentCollectionAmount")
        set(value) = setPref("AgentCollectionAmount", value)
    var agentIsPrepaid: Boolean
        get() = getPrefBoolean("AgentIsPrepaid")
        set(value) = setPref("AgentIsPrepaid", value)
    var agentAllowSales: String
        get() = getPrefString("AgentAllowSales")
        set(value) = setPref("AgentAllowSales", value)

    var allowPropertyTaxCollection: String
        get() = getPrefString("AllowPropertyTaxCollection")
        set(value) = setPref("AllowPropertyTaxCollection", value)

    var allowCombinedPayoutRequest: Boolean
        get() = getPrefBoolean("IsAllowCombinedPayoutRequest")
        set(value) = setPref("IsAllowCombinedPayoutRequest", value)
    //endregion

    var copyrightReport: String
        get() = getPrefString("CopyrightReport")
        set(value) = setPref("CopyrightReport", value)

    var loggedInUserID: String
        get() = getPrefString("LoggedInUserID")
        set(value) = setPref("LoggedInUserID", value)

    var domain: String
        get() = getPrefString("Domain")
        set(value) = setPref("Domain", value)

    var userOrgBranchID: Int
        get() = getPrefInt("UserOrgBranchID")
        set(value) = setPref("UserOrgBranchID", value)

    var roleCode: String
        get() = getPrefString("RoleCode")
        set(value) = setPref("RoleCode", value)

    var currencyCode: String
        get() = getPrefString("CurrencyCode")
        set(value) = setPref("CurrencyCode", value)

    var currencySymbol: String
        get() = getPrefString("CurrencySymbol")
        set(value) = setPref("CurrencySymbol", value)

    var currencyPrecision: Int
        get() = getPrefInt("currencyPrecision")
        set(value) = setPref("currencyPrecision", value)

    var userOrgID: Int
        get() = getPrefInt("UserOrgID")
        set(value) = setPref("UserOrgID", value)

    var backOfficeVersion: Float
        get() = getPrefFloat("UserOrgID")
        set(value) = setPref("UserOrgID", value)

    var isFirstAPICallDone: Boolean
        get() = getPrefBoolean("IsFirstAPICallDone")
        set(value) = setPref("IsFirstAPICallDone", value)

    var isAdminUser: Boolean
        get() = getPrefBoolean("IsAdminUser")
        set(value) = setPref("IsAdminUser", value)

    var IsApprover: String
        get() = getPrefString("IsApprover")
        set(value) = setPref("IsApprover", value)

    var dynamicToken: String
        get() = getPrefString("DynamicToken")
        set(value) = setPref("DynamicToken", value)

    var secretKey: String
        get() = getPrefString("SecretKey")
        set(value) = setPref("SecretKey", value)

    var accountId: Int
        get() = getPrefInt("AccountID")
        set(value) = setPref("AccountID", value)

    var accountName: String
        get() = getPrefString("AccountName")
        set(value) = setPref("AccountName", value)

    var loginCount: Int
        get() = getPrefInt("LoginCount")
        set(value) = setPref("LoginCount", value)

    var agentCollectionFromDate: String
        get() = getPrefString("AgentCollectionFromDate")
        set(value) = setPref("AgentCollectionFromDate", value)

    var agentCollectionToDate: String
        get() = getPrefString("AgentCollectionToDate")
        set(value) = setPref("AgentCollectionToDate", value)

    var salesHistoryFromDate : String
        get() = getPrefString("FromDate")
        set(value) = setPref("FromDate",value)

    var salesHistoryToDate : String
        get() = getPrefString("ToDate")
        set(value) = setPref("ToDate",value)


    var creditBalanceFromDate: String
        get() = getPrefString("CreditBalanceFromDate")
        set(value) = setPref("CreditBalanceFromDate", value)

    var creditBalanceToDate: String
        get() = getPrefString("CreditBalanceToDate")
        set(value) = setPref("CreditBalanceToDate", value)

    var superiorTo: String
        get() = getPrefString("SuperiorTo")
        set(value) = setPref("SuperiorTo", value)

    var appSessionTimeOut: Int
        get() = getPrefInt("AppSessionTimeOut")
        set(value) = setPref("AppSessionTimeOut", value)

    var latitude: String
        get() = getPrefString("Latitude")
        set(value) = setPref("Latitude", value)

    var longitude: String
        get() = getPrefString("Longitude")
        set(value) = setPref("Longitude", value)

    var cultureCode: String
        get() = getPrefString("CultureCode")
        set(value) = setPref("CultureCode", value)

    //Added the serial number need to change for 10 device
    var serialNumber: String
//        get() = "1234512345"
        get() = getPrefString("SERIAL_NUMBER")
        set(value) = setPref("SERIAL_NUMBER", value)

    var isRightSide: Boolean
        get() = getPrefBoolean("IsRightSide")
        set(value) = setPref("IsRightSide", value)

    var isSymbolAtRight:Boolean
        get() = getPrefBoolean("SymbAtRight")
        set(value) = setPref("SymbAtRight", value)

    var parkingPlace:String
        get() = getPrefString("ParkingPlace")
        set(value) = setPref("ParkingPlace", value)

    var parkingPlaceID:Int
        get() = getPrefInt("ParkingPlaceID")
        set(value) = setPref("ParkingPlaceID", value)

    var allowParking:String
        get() = getPrefString("AllowParking")
        set(value) = setPref("AllowParking", value)

    var allowParkingCount:Int
        get() = getPrefInt("AllowParkingCount")
        set(value) = setPref("AllowParkingCount", value)

    var assignedZoneCode:String
        get() = getPrefString("AssignedZoneCode")
        set(value) = setPref("AssignedZoneCode", value)


    var authUniqueKey:String
        get() = getPrefString("AuthUniqueKey")
        set(value) = setPref("AuthUniqueKey", value)

    var jedisLogoutEntryID: String
        get() = getPrefString("JedisLogoutEntryID")
        set(value) = setPref("JedisLogoutEntryID", value)


    var jedisConnectionHost: String
        get() = getPrefString("JedisConnectionHost")
        set(value) = setPref("JedisConnectionHost", value)

    var jedisConnectionPort: Int
        get() = getPrefInt("JedisConnectionPort")
        set(value) = setPref("JedisConnectionPort", value)

    var jedisConnectionPassword: String
        get() = getPrefString("JedisConnectionPassword")
        set(value) = setPref("JedisConnectionPassword", value)

    var isServiceRunning: Boolean
        get() = getPrefBoolean("IsServiceRunning")
        set(value) = setPref("IsServiceRunning", value)

    var logUploadTime: String
        get() = getPrefString("LogUploadTime")
        set(value) = setPref("LogUploadTime", value)

    @Synchronized
    fun getUUID(): String {
        var id = getPrefString("UUID")
        if (id.isEmpty()) {
            id = randomUUID().toString()
            setPref("UUID", id)
        }
        return id
    }

    fun getStaticToken(): String {
        return "P8eLNZ2WL+w=Z*ca"
    }

    fun getDeviceName(): String {
        return Build.MODEL + " " + Build.BRAND
    }

    fun isSupervisorOrInspector() = isInspector() || isSupervisor()
    fun isInspector() = agentTypeCode == Constant.AgentTypeCode.ISP.name
    fun isSupervisor() = agentTypeCode == Constant.AgentTypeCode.SPR.name
    fun isAssociation() = agentTypeCode == Constant.AgentTypeCode.ASO.name
    fun isAssociationAgent() = agentTypeCode == Constant.AgentTypeCode.ASA.name
    fun isPayPoint() = agentTypeCode == Constant.AgentTypeCode.PPS.name
    fun isMunicipalAgent() = agentTypeCode == Constant.AgentTypeCode.MCA.name
    fun isParkingMunicipalAgent() = agentTypeCode == Constant.AgentTypeCode.PMA.name
    fun isParkingThirdPartyAgent() = agentTypeCode == Constant.AgentTypeCode.PTA.name
    fun isThirdPartyAgent() = agentTypeCode == Constant.AgentTypeCode.TPA.name
    fun isLawEnforceAgent() = agentTypeCode == Constant.AgentTypeCode.LEA.name
    fun isLawEnforeInspector() = agentTypeCode == Constant.AgentTypeCode.LEI.name
    fun isLawEnforeSupervisor() = agentTypeCode == Constant.AgentTypeCode.LES.name
    fun isSales() = agentTypeCode == Constant.AgentTypeCode.SLA.name

    fun getAsPerAgentType(): Boolean {
        return MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.MCA.name ||
                MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.TPA.name ||
                MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASO.name ||
                MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASA.name ||
                MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.SPR.name||
                MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ISP.name
    }

}