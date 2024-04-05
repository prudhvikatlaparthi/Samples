package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class LicensePayloadData(
        @SerializedName("LicenseID")
        var licenseId: Int? = 0,
        @SerializedName("orgzid")
        var organisationId: Int? = 0,
        @SerializedName("LicenseRequestID")
        var licenseRequestId: Int? = 0,
        @SerializedName("licnsno")
        var licenseNo: String? = "",
        @SerializedName("LicenseCategoryID")
        var licenseCategoryId: Int? = 0,
        @SerializedName("IssuanceDate")
        var issuanceDate: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @SerializedName("CancelledByUserID")
        var cancelledByUserId: String? = "",
        @SerializedName("CancellationDate")
        var cancellationDate: String? = null,
        @SerializedName("CancellationRemarks")
        var cancellationRemarks: String? = null,
        @SerializedName("AuthorizedBeverages")
        var authorisedBevarages: String? = null,
        @SerializedName("ValidTillDate")
        var validTillDate: String? = ""
)