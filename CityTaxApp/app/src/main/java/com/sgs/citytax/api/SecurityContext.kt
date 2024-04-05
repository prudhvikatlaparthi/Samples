package com.sgs.citytax.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.base.MyApplication
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SecurityContext(
    @SerializedName("loggeduserid")
    var loggedUserID: String = MyApplication.getPrefHelper().loggedInUserID,
    @SerializedName("domain")
    var domain: String = MyApplication.getPrefHelper().domain,
    @SerializedName("langcode")
    var languageCode: String? = if (MyApplication.getPrefHelper().language.isEmpty()) "FR" else MyApplication.getPrefHelper().language,
    @SerializedName("usrorgbrid")
    var userOrgBranchID: Int = MyApplication.getPrefHelper().userOrgBranchID,
    @SerializedName("rlcode")
    var roleCode: String = MyApplication.getPrefHelper().roleCode,
    @SerializedName("crncycode")
    var currencyCode: String = MyApplication.getPrefHelper().currencyCode,
    @SerializedName("usrorgid")
    var userOrgID: Int = MyApplication.getPrefHelper().userOrgID,
    @SerializedName("acctid")
    var accountID: Int = MyApplication.getPrefHelper().accountId,
    @SerializedName("lat")
    var latitude: String? = MyApplication.getPrefHelper().latitude,
    @SerializedName("long")
    var longitude: String? = MyApplication.getPrefHelper().longitude,
    @SerializedName("LogoutLatitude")
    var LogoutLatitude: String? = MyApplication.getPrefHelper().latitude,
    @SerializedName("LogoutLongitude")
    var LogoutLongitude: String? = MyApplication.getPrefHelper().longitude,
    @SerializedName("cultrcode")
    var cultrcode: String? = MyApplication.getPrefHelper().cultureCode
) : Parcelable
