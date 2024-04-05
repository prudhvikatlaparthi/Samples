package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName

data class CRMAgentTransactionDetail(
        @SerializedName("AccountTypeCode")
        val accountTypeCode: String?,
        @SerializedName("Amount")
        val amount: Double?,
        @SerializedName("ChequeStatus")
        val chequeStatus: String?,
        @SerializedName("ChequeStatusCode")
        val chequeStatusCode: String?,
        @SerializedName("CollectedBy")
        val collectedBy: String?,
        @SerializedName("CollectionType")
        val collectionType: String?,
        @SerializedName("Commission")
        val commission: Double? = 0.0,
        @SerializedName("CommissionBalance")
        val commissionBalance: Double? = 0.0,
        @SerializedName("CustomerName")
        val customerName: String?,
        @SerializedName("Date")
        val date: String?,
        @SerializedName("IsAdminUser")
        val isAdminUser: String?,
        @SerializedName("PaymentMode")
        val paymentMode: String?,
        @SerializedName("Product")
        val product: String?,
        @SerializedName("ProductCode")
        val productCode: String?,
        @SerializedName("ProductTypeCode")
        val productTypeCode: String?,
        @SerializedName("ReceivedBy")
        val receivedBy: String?,
        @SerializedName("ReceivedByAccountID")
        val receivedByAccountID: Double? = 0.0,
        @SerializedName("TaxPayer")
        val taxPayer: String?,
        @SerializedName("TaxType")
        val taxType: String?,
        @SerializedName("VehicleNo")
        val vehicleNo: String?,
        @SerializedName("VoucherNo")
        val voucherNo: String?
)