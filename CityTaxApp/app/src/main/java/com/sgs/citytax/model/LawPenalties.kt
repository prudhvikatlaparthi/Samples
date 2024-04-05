package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class LawPenalties(
        @SerializedName("PenaltyID")
        var penaltyID: Int? = 0,
        @SerializedName("PenaltyDate")
        var penaltyDate: String? = "",
        @SerializedName("PenaltyRuleID")
        var penaltyRuleID: Int? = 0,
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("InvoiceDue")
        var invoiceDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("pct")
        var pct: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PenaltyAmount")
        var penaltyAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("UnitCode")
        var unitCode: String? = "",
        @SerializedName("UnitValue")
        var unitValue: Int? = 0,
        @SerializedName("AllowOnlyOnNoPayment")
        var allowOnlyOnNoPayment: String? = "",
        @SerializedName("AllowOnBillingCycleEnd")
        var allowOnBillingCycleEnd: String? = "",
        @SerializedName("BillingCycleID")
        var billingCycleID: String? = "",
        @SerializedName("BeforPenaltyGracePeriod")
        var beforPenaltyGracePeriod: Int? = 0,
        @SerializedName("rndngmthdid")
        var rndngmthdid: Int? = 0,
        @SerializedName("rndng")
        var rndng: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("txntypcode")
        var txntypcode: String? = "",
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate: String? = "",
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("acctname")
        var accountName: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("NoticeReferenceNo")
        var noticeReferenceNo: String? = "",
        @SerializedName("TaxSubType")
        var taxSubType: String? = "",
        @SerializedName("vchrno")
        var voucherNo: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("PoliceStation")
        var policeStation: String? = "",
        @SerializedName("ViolationType")
        var violationType: String? = "",
        @SerializedName("ViolationClass")
        var violationClass: String? = "",
        @SerializedName("ImpoundmentType")
        var impoundmentType: String? = "",
        @SerializedName("ImpoundmentSubType")
        var impoundmentSubType: String? = "",
        @SerializedName("vehno")
        var vehicleNumber: String? = "",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycoTaxId: String? = "",
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("Driver")
        var driver: String? = "",
        @SerializedName("Violator")
        var violator: String? = "",
        @SerializedName("GoodsOwner")
        var goodsOwner: String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readValue(BigDecimal::class.java.classLoader) as? BigDecimal,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(penaltyID)
        parcel.writeString(penaltyDate)
        parcel.writeValue(penaltyRuleID)
        parcel.writeValue(taxInvoiceID)
        parcel.writeValue(invoiceDue)
        parcel.writeValue(pct)
        parcel.writeValue(penaltyAmount)
        parcel.writeString(unitCode)
        parcel.writeValue(unitValue)
        parcel.writeString(allowOnlyOnNoPayment)
        parcel.writeString(allowOnBillingCycleEnd)
        parcel.writeString(billingCycleID)
        parcel.writeValue(beforPenaltyGracePeriod)
        parcel.writeValue(rndngmthdid)
        parcel.writeValue(rndng)
        parcel.writeString(txntypcode)
        parcel.writeValue(currentDue)
        parcel.writeString(taxInvoiceDate)
        parcel.writeValue(accountID)
        parcel.writeString(accountName)
        parcel.writeString(productCode)
        parcel.writeString(product)
        parcel.writeString(noticeReferenceNo)
        parcel.writeString(taxSubType)
        parcel.writeString(voucherNo)
        parcel.writeString(taxRuleBookCode)
        parcel.writeString(policeStation)
        parcel.writeString(violationType)
        parcel.writeString(violationClass)
        parcel.writeString(impoundmentType)
        parcel.writeString(impoundmentSubType)
        parcel.writeString(vehicleNumber)
        parcel.writeString(vehicleSycoTaxId)
        parcel.writeString(vehicleOwner)
        parcel.writeString(driver)
        parcel.writeString(violator)
        parcel.writeString(goodsOwner)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LawPenalties> {
        override fun createFromParcel(parcel: Parcel): LawPenalties {
            return LawPenalties(parcel)
        }

        override fun newArray(size: Int): Array<LawPenalties?> {
            return arrayOfNulls(size)
        }
    }

//    override fun toString(): String {
//        return getData()
//    }
//
//
//    fun getData(): String {
//        var data: String? = ""
//
//        if (!vehicleOwner.isNullOrEmpty() && !goodsOwner.isNullOrEmpty())
//            data = "${getString(R.string.vehicle_owner)} : ${vehicleOwner}" +
//                    "\n${getString(R.string.driver)} : ${driver}" +
//                    "\n${getString(R.string.violator)} : ${violator}" +
//                    "\n${getString(R.string.goods_owner)} : ${goodsOwner}\n\n"
//        else if (!vehicleOwner.isNullOrEmpty())
//            data = "${getString(R.string.vehicle_owner)} : ${vehicleOwner}" +
//                    "\n${getString(R.string.driver)} : ${driver}" +
//                    "\n${getString(R.string.violator)} : ${violator}\n\n"
//        else if (!goodsOwner.isNullOrEmpty())
//            data = "${getString(R.string.violator)} : ${violator}" +
//                    "\n${getString(R.string.goods_owner)} : ${goodsOwner}\n\n"
//        else
//            data = ""
//        return data
//    }

}