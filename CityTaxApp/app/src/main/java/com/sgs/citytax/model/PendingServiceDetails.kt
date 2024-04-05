package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PendingServiceDetails(
        @SerializedName("ServiceTypeID")
        var serviceTypeId: Int? = 0,
        @SerializedName("ServiceSubTypeID")
        var serviceSubTypeId: Int? = 0,
        @SerializedName("area")
        var area: Double? = 0.0,
        @SerializedName("AdvanceAmount")
        var advanceAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("EstimatedAmount")
        var estimatedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("cntrycode")
        var countryCode: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("unitcode")
        var unitCode: String? = "",
        @SerializedName("stid")
        var stateId: Int? = 0,
        @SerializedName("Status")
        var status: String? = "",
        @SerializedName("TotalAmount")
        var totalAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("recdamt")
        var receivedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ServiceRequestDate")
        var serviceRequestDate: String? = "",
        @SerializedName("ServiceRequestNo")
        var serviceRequestNumber: Int? = 0,
        @SerializedName("AccountName")
        var accountName: String? = "",
        @SerializedName("prodcode")
        var productCode:String?="",
        @SerializedName("AccountID")
        var accountId:Int? = 0,
        var isLoading: Boolean = false,
        @SerializedName("AssignTo3rdParty")
        var assignTo3rdParty: String? = null,
        @SerializedName("CommissionPercentage")
        var commissionPercentage: BigDecimal? = BigDecimal.ZERO,
)