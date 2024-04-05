package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class UMXUserOrganizations(
        @SerializedName("UMX_UserOrganizations")
        var list: ArrayList<UMXUserOrganizationsData> = arrayListOf()
)

data class UMXUserOrganizationsData(
        @SerializedName("caption")
        var caption: String? = null,
        @SerializedName("LoginLogo")
        var LoginLogo: Int? = null,
        @SerializedName("PortalLogo")
        var PortalLogo: Int? = null,
        @SerializedName("logo")
        var logo: Int? = null,
        @SerializedName("LoginLogoAWSPath")
        var LoginLogoAWSPath: String? = null,
        @SerializedName("AndroidLogoAWSPath")
        var androidLogoAWSPath: String? = null,
        @SerializedName("PortalLogoAWSPath")
        var PortalLogoAWSPath: String = "",
        @SerializedName("LogoAWSPath")
        var LogoAWSPath: String = "",
        @SerializedName("MunicipalLogoAWSPath")
        var municipalLogoAWSPath : String? = null

)