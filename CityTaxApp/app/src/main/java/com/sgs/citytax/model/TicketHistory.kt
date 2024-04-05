package com.sgs.citytax.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class TicketHistory(
    @SerializedName("InvoiceTransactionTypeCode")
    var invoiceTransactionTypeCode: String? = "",
    @SerializedName("TransactionNo")
    var transactionNo: Int? = 0,
    @SerializedName("txntypcode")
    var transactiontypcode: String? = "",
    @SerializedName("prodcode")
    var prodcode: String? = "",
    @SerializedName("acctid")
    var accounttId: Int? = 0,
    @SerializedName("ImpoundmentType")
    var impoundmentType: String? = "",
    @SerializedName("ImpoundmentSubType")
    var impoundmentSubType: String? = "",
    @SerializedName("ViolationDetails")
    var violationDetails: String? = "",
    @SerializedName("InvoiceTransactionVoucherDate")
    var invoiceTransactionVoucherDate: String? = "",
    @SerializedName("vehno")
    var vehicleNo: String? = "",
    @SerializedName("Driver")
    var driver: String? = "",
    @SerializedName("VehicleOwner")
    var vehicleOwner: String? = "",
    @SerializedName("DrivingLicenseNo")
    var drivingLicenseNo: String? = "",
    @SerializedName("FineAmount")
    var fineAmount: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("netrec")
    var netReceivable: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("ViolationType")
    var violationType: String? = "",
    @SerializedName("ViolationClass")
    var violationClass: String? = "",
    @SerializedName("InvoiceTransactionVoucherNo")
    var invoiceTransactionVoucherNo: Int? = 0,
    @SerializedName("TransactionDue")
    var transactionDue: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("MinPayAmount")
    var minmumPayAmount: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("qty")
    var quantity: Int? = 0,
    var violationTypeID: Int? = 0,
    var mobileNo: String? = "",
    var violationSubTypeID: Int? = 0,
    @Expose(serialize = false, deserialize = false)
    var isLoading: Boolean = false,
    @SerializedName("ViolatorTypeCode")
    var violatorTypeCode: String? = "",
    @SerializedName("chqno")
    var chequeNumber: String? = "",
    @SerializedName("ChequeStatus")
    var chequeStatus: String? = "",
    @SerializedName("ChequeStatusCode")
    var chequeStatusCode: String? = "",
    @SerializedName("AmountToSettleByCheque")
    var amountToSettleByCheque: BigDecimal? = BigDecimal.ZERO,
    @SerializedName("bnkname")
    var chequeBankName: String? = "",
    @SerializedName("chqdt")
    var chequeDate: String? = "",
    @SerializedName("PaidQuantity")
    var paidQuantity: Int? = 0,
    @SerializedName("TaxNoticeReferenceNo")
    var noticeReferenceNo: String?  = "",
) : Parcelable