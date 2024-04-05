package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCOMPropertyVerificationRequests(
        @SerializedName("PropertyVerificationRequestID")
        var propertyVerificationRequestId: Int? = 0,
        @SerializedName("PropertyVerificationRequestDate")
        var verificationRequestDate: String? = "",
        @SerializedName("PlanningPermissionRequestID")
        var planningPermissionRequestId: Int? = 0,
        @SerializedName("PropertyVerificationTypeID")
        var propertyVerificationTypeId: Int? = 0,
        @SerializedName("StatusCode")
        var statusCode: String? = "",
        @SerializedName("Description")
        var description: String? = "",
        @SerializedName("DocumentVerificationByUserID")
        var documentVerificationByUserID: String? = "",
        @SerializedName("DocumentVerificationStatusCode")
        var documentVerificationStatusCodde: String? = "",
        @SerializedName("DocumentVerificatioDate")
        var documentVerificationDate: String? = "",
        @SerializedName("PhysicalVerificatioDate")
        var physicalVerificationDate: String? = "",
        @SerializedName("Status")
        var status: String? = "",
        @SerializedName("PropertyName")
        var propertyName: String? = "",
        @SerializedName("PropertySycotaxID")
        var propertySycoTaxID: String? = "",
        @SerializedName("PropertyVerificationType")
        var verificationType: String? = "",
        @SerializedName("Owner")
        var owner: String? = "",
        @SerializedName("DocumentVerificationByUser")
        var documentVerifiedByUSer: String? = "",
        @SerializedName("PhysicalVerificationByUser")
        var physicalVerificationByUser: String? = "",
        @SerializedName("DocumentVerificationStatus")
        var documentVerificationStatus: String? = "",
        @SerializedName("PhysicalVerificationStatus")
        var physicalVerificationStatus: String? = "",
        @SerializedName("PropertyType")
        var propertyType: String? = "",
        @SerializedName("City")
        var city: String? = "",
        @SerializedName("Country")
        var country: String? = "",
        @SerializedName("State")
        var state: String? = "",
        @SerializedName("ZipCode")
        var zipCode: String? = "",
        @SerializedName("DoorNo")
        var doorNo: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("Sector")
        var sector: String? = "",
        @SerializedName("Zone")
        var zone: String? = "",
        @SerializedName("plot")
        var plot: String? = "",
        @SerializedName("ValidUptoDate")
        var validUptoDate: String? = "",
        @SerializedName("ApprovedByUserID")
        var approvedByUserId: String? = "",
        @SerializedName("ApprovedDate")
        var approvedDate: String? = "",
        @SerializedName("ApprovedPeriod")
        var approvedPeriod: String? = "",
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = null,
        @SerializedName("CitizenCardNo")
        var citizenCardNo: String? = null,
        @SerializedName("CitizenSycotaxID")
        var citizenSycotaxID: String? = null
)