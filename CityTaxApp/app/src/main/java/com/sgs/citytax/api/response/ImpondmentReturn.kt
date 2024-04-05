package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.R
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.getQuantity
import com.sgs.citytax.util.getString
import java.math.BigDecimal

data class ImpondmentReturn(
        @SerializedName("Rank")
        var rank: Int? = 0,
        @SerializedName("TaxNoticeNo")
        var TaxNoticeNo: String? = "",
        @SerializedName("txntypcode")
        var transactiontypcode: String? = "",
        @SerializedName("TransactionNo")
        var transactionNo: Int? = 0,
        @SerializedName("txndt")
        var transactiondate: String? = "",
        @SerializedName("acctid")
        var accounttId: Int? = 0,
        @SerializedName("prodcode")
        var prodcode: String? = "",
        @SerializedName("amt")
        var amount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("MaxInstallmentNo")
        var maxInstallmentNo: Int? = 0,
        @SerializedName("MinInstalledAmount")
        var minInstalledAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("MinPayAmount")
        var minmumPayAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("typ")
        var type: String? = "",
        @SerializedName("prodtypcode")
        var prodtypcode: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("InvoiceTransactionTypeCode")
        var invoiceTransactionTypeCode: String? = "",
        @SerializedName("InvoiceTransactionVoucherNo")
        var invoiceTransactionVoucherNo: Int? = 0,
        @SerializedName("InvoiceTransactionVoucherDate")
        var invoiceTransactionVoucherDate: String? = "",
        @SerializedName("usrorgbrid")
        var usrorgbrid: Int? = 0,
        @SerializedName("PoliceStation")
        var policeStation: String? = "",
        @SerializedName("ViolationTypeID")
        var violationTypeID: Int? = 0,
        @SerializedName("ViolationType")
        var violationType: String? = "",
        @SerializedName("ViolationClass")
        var violationClass: String? = "",
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("ImpoundmentTypeID")
        var impoundmentTypeID: Int? = 0,
        @SerializedName("ImpoundmentType")
        var impoundmentType: String? = "",
        @SerializedName("ImpoundmentSubType")
        var impoundmentSubType: String? = "",
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycotaxID: String? = "",
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountID: Int? = 0,
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("VehicleOwnerMobile")
        var vehicleOwnerMobile: String? = "",
        @SerializedName("VehicleOwnerEmail")
        var vehicleOwnerEmail: String? = "",
        @SerializedName("DriverAccountID")
        var driverAccountID: Int? = 0,
        @SerializedName("Driver")
        var driver: String? = "",
        @SerializedName("drvrmob")
        var driverMobileNumber: String? = "",
        @SerializedName("DriverEmail")
        var driverEmail: String? = "",
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNo: String? = "",
        @SerializedName("ViolatorAccountID")
        var violatorAccountID: Int? = 0,
        @SerializedName("Violator")
        var violator: String? = "",
        @SerializedName("ViolatorMobile")
        var violatorMobile: String? = "",
        @SerializedName("ViolatorEmail")
        var violatorEmail: String? = "",
        @SerializedName("GoodsOwnerAccountID")
        var goodsOwnerAccountID: Int? = 0,
        @SerializedName("GoodsOwner")
        var goodsOwner: String? = "",
        @SerializedName("GoodsOwnerMobile")
        var goodsOwnerMobile: String? = "",
        @SerializedName("GoodsOwnerEmail")
        var goodsOwnerEmail: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ImpoundmentCharge")
        var impoundmentCharge: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("crtd")
        var created: String? = "",
        @SerializedName("ImpoundedBy")
        var impoundedBy: String? = "",
        @SerializedName("ReturnedBy")
        var returnedBy: String? = "",
        @SerializedName("ImpoundmentReturnDate")
        var impoundmentReturnDate: String? = "",
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("ApplicableOnVehicle")
        var applicableOnVehicle: String? = "",
        @SerializedName("ApplicableOnGoods")
        var applicableOnGoods: String? = "",
        @SerializedName("stscode")
        var statusCode: String? = "",
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false,
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("qty")
        var quantity: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("returnqty", alternate = ["ReturnedQuantity"])
        var returnQuantity: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PendingReturnQuantity")
        var pendingReturnQuantity: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PaidQuantity")
        var paidQuantity: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("chqno")
        var chequeNumber: String? = "",
        @SerializedName("ChequeStatus")
        var chequeStatus: String? = "",
        @SerializedName("ChequeStatusCode")
        var chequeStatusCode: String? = "",
        @SerializedName("AmountToSettleByCheque")
        var chequeAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("bnkname")
        var chequeBankName: String? = "",
        @SerializedName("chqdt")
        var chequeDate: String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(rank)
        parcel.writeString(TaxNoticeNo)
        parcel.writeString(transactiontypcode)
        parcel.writeValue(transactionNo)
        parcel.writeString(transactiondate)
        parcel.writeValue(accounttId)
        parcel.writeString(prodcode)
        parcel.writeValue(amount)
        parcel.writeValue(maxInstallmentNo)
        parcel.writeValue(minInstalledAmount)
        parcel.writeValue(currentDue)
        parcel.writeValue(minmumPayAmount)
        parcel.writeString(type)
        parcel.writeString(prodtypcode)
        parcel.writeString(noticeReferenceNo)
        parcel.writeString(invoiceTransactionTypeCode)
        parcel.writeValue(invoiceTransactionVoucherNo)
        parcel.writeString(invoiceTransactionVoucherDate)
        parcel.writeValue(usrorgbrid)
        parcel.writeString(policeStation)
        parcel.writeValue(violationTypeID)
        parcel.writeString(violationType)
        parcel.writeString(violationClass)
        parcel.writeString(violationDetails)
        parcel.writeValue(impoundmentTypeID)
        parcel.writeString(impoundmentType)
        parcel.writeString(impoundmentSubType)
        parcel.writeString(impoundmentReason)
        parcel.writeString(vehicleNo)
        parcel.writeString(vehicleSycotaxID)
        parcel.writeValue(vehicleOwnerAccountID)
        parcel.writeString(vehicleOwner)
        parcel.writeString(vehicleOwnerMobile)
        parcel.writeString(vehicleOwnerEmail)
        parcel.writeValue(driverAccountID)
        parcel.writeString(driver)
        parcel.writeString(driverMobileNumber)
        parcel.writeString(driverEmail)
        parcel.writeString(drivingLicenseNo)
        parcel.writeValue(violatorAccountID)
        parcel.writeString(violator)
        parcel.writeString(violatorMobile)
        parcel.writeString(violatorEmail)
        parcel.writeValue(goodsOwnerAccountID)
        parcel.writeString(goodsOwner)
        parcel.writeString(goodsOwnerMobile)
        parcel.writeString(goodsOwnerEmail)
        parcel.writeValue(fineAmount)
        parcel.writeValue(impoundmentCharge)
        parcel.writeString(created)
        parcel.writeString(impoundedBy)
        parcel.writeString(returnedBy)
        parcel.writeString(impoundmentReturnDate)
        parcel.writeValue(geoAddressID)
        parcel.writeString(applicableOnVehicle)
        parcel.writeString(applicableOnGoods)
        parcel.writeString(statusCode)
        parcel.writeByte(if (isLoading) 1 else 0)
        parcel.writeString(violatorTypeCode)
        parcel.writeValue(quantity)
        parcel.writeValue(returnQuantity)
        parcel.writeValue(pendingReturnQuantity)
        parcel.writeValue(paidQuantity)
        parcel.writeString(chequeNumber)
        parcel.writeString(chequeStatus)
        parcel.writeString(chequeStatusCode)
        parcel.writeValue(chequeAmount)
        parcel.writeString(chequeBankName)
        parcel.writeString(chequeDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImpondmentReturn> {
        override fun createFromParcel(parcel: Parcel): ImpondmentReturn {
            return ImpondmentReturn(parcel)
        }

        override fun newArray(size: Int): Array<ImpondmentReturn?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return getData()
    }


    fun getData(): String {
        var data: String? = ""
        if (violatorTypeCode != Constant.ViolationTypeCode.ANIMAL.code) {
            if (applicableOnVehicle == "Y") {
                data = "${getString(R.string.owner)} : ${vehicleOwner ?: ""}" +
                        if(vehicleNo!=null){
                            "\n${getString(R.string.vehicle_registration_no)} : ${vehicleNo}"
                        }else{
                            "\n${getString(R.string.vehicle_registration_no)} : "
                        } +
                        "\n${getString(R.string.violation_type)} : ${violationType}"

            } else {
                data = "${getString(R.string.goods_owner)} : ${goodsOwner ?: ""} \n${getString(R.string.phone_number)} : ${goodsOwnerMobile ?: ""}\n${getString(R.string.violation_type)} : ${violationType} \n"
            }
        } else {
            data = "${getString(R.string.impound_quantity)} : ${getQuantity(quantity.toString()) ?: ""} \n${getString(R.string.return_quantity)} : ${getQuantity(pendingReturnQuantity.toString()) ?: ""}\n${getString(R.string.impond_type)} : ${impoundmentType} \n"
        }

        return data
    }
}


