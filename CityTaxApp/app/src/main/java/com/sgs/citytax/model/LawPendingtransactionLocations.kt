package com.sgs.citytax.model

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import com.sgs.citytax.R
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.formatWithPrecision
import com.sgs.citytax.util.getString

data class LawPendingTransactionLocations(
        @SerializedName("InvoiceTransactionTypeCode")
        var InvoiceTransactionTypeCode: String? = "",
        @SerializedName("InvoiceTransactionVoucherNo")
        var InvoiceTransactionVoucherNo: Int? = 0,
        @SerializedName("InvoiceTransactionVoucherDate")
        var InvoiceTransactionVoucherDate: String? = "",
        @SerializedName("UserOrgBranchID")
        var UserOrgBranchID: Int? = 0,
        @SerializedName("PoliceStation")
        var PoliceStation: String? = "",
        @SerializedName("ViolationTypeID")
        var ViolationTypeID: Int? = 0,
        @SerializedName("ViolationType")
        var ViolationType: String? = null,
        @SerializedName("ViolationClass")
        var ViolationClass: String? = null,
        @SerializedName("ViolationDetails")
        var ViolationDetails: String? =null,
        @SerializedName("ImpoundmentTypeID")
        var ImpoundmentTypeID: Int? = 0,
        @SerializedName("ImpoundmentType")
        var ImpoundmentType: String? = null,
        @SerializedName("ImpoundmentSubType")
        var ImpoundmentSubType: String? = null,
        @SerializedName("ImpoundmentReason")
        var ImpoundmentReason: String? =null,
        @SerializedName("VehicleNo")
        var VehicleNo: String? = null,
        @SerializedName("VehicleSycotaxID")
        var VehicleSycotaxID: String? = null,
        @SerializedName("VehicleOwnerAccountID")
        var VehicleOwnerAccountID: String? = null,
        @SerializedName("VehicleOwner")
        var VehicleOwner: String? = null,
        @SerializedName("VehicleOwnerMobile")
        var VehicleOwnerMobile: String? = null,
        @SerializedName("VehicleOwnerMobileWithCode")
        var VehicleOwnerMobileWithCode: String? = null,
        @SerializedName("VehicleOwnerEmail")
        var VehicleOwnerEmail: String? =null,
        @SerializedName("DriverAccountID")
        var DriverAccountID: String? = null,
        @SerializedName("Driver")
        var Driver: String? = null,
        @SerializedName("DriverMobile")
        var DriverMobile: String? = null,
        @SerializedName("DriverMobileWithCode")
        var DriverMobileWithCode: String? = null,
        @SerializedName("DriverEmail")
        var DriverEmail: String? = null,
        @SerializedName("DrivingLicenseNo")
        var DrivingLicenseNo: String? = null,
        @SerializedName("ViolatorAccountID")
        var ViolatorAccountID: String? = null,
        @SerializedName("Violator")
        var Violator: String? = null,
        @SerializedName("ViolatorMobile")
        var ViolatorMobile: String? = null,
        @SerializedName("ViolatorMobileWithCode")
        var ViolatorMobileWithCode: String? = null,
        @SerializedName("ViolatorEmail")
        var ViolatorEmail: String? = null,
        @SerializedName("GoodsOwnerAccountID")
        var GoodsOwnerAccountID: String? = null,
        @SerializedName("GoodsOwner")
        var GoodsOwner: String? = null,
        @SerializedName("GoodsOwnerMobile")
        var GoodsOwnerMobile: String? = null,
        @SerializedName("GoodsOwnerMobileWithCode")
        var GoodsOwnerMobileWithCode: String? = null,
        @SerializedName("GoodsOwnerEmail")
        var GoodsOwnerEmail: String? = null,
        @SerializedName("FineAmount")
        var FineAmount: Double? = 0.0,
        @SerializedName("ImpoundmentCharge")
        var ImpoundmentCharge: Double? = 0.0,
        @SerializedName("CreatedBy")
        var CreatedBy: String? =null,
        @SerializedName("ImpoundedBy")
        var ImpoundedBy: String? = null,
        @SerializedName("ReturnedBy")
        var ReturnedBy: String? = null,
        @SerializedName("ImpoundmentReturnDate")
        var ImpoundmentReturnDate: String? = null,
        @SerializedName("GeoAddressID")
        var GeoAddressID: Int? = 0,
        @SerializedName("NetReceivable")
        var NetReceivable: Double? = 0.0,
        @SerializedName("TransactionDue")
        var TransactionDue: Double? = 0.0,
        @SerializedName("TaxNoticeReferenceNo")
        var TaxNoticeReferenceNo: String? = null,
        @SerializedName("Latitude")
        var latitude: Double? = 0.0,
        @SerializedName("Longitude")
        var longitude: Double? = 0.0,
        @SerializedName("Color")
        var color: String? = ""
) : Parcelable, ClusterItem {
    constructor(parcel: Parcel) : this(
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
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(InvoiceTransactionTypeCode)
        parcel.writeValue(InvoiceTransactionVoucherNo)
        parcel.writeString(InvoiceTransactionVoucherDate)
        parcel.writeValue(UserOrgBranchID)
        parcel.writeString(PoliceStation)
        parcel.writeValue(ViolationTypeID)
        parcel.writeString(ViolationType)
        parcel.writeString(ViolationClass)
        parcel.writeString(ViolationDetails)
        parcel.writeValue(ImpoundmentTypeID)
        parcel.writeString(ImpoundmentType)
        parcel.writeString(ImpoundmentSubType)
        parcel.writeString(ImpoundmentReason)
        parcel.writeString(VehicleNo)
        parcel.writeString(VehicleSycotaxID)
        parcel.writeString(VehicleOwnerAccountID)
        parcel.writeString(VehicleOwner)
        parcel.writeString(VehicleOwnerMobile)
        parcel.writeString(VehicleOwnerMobileWithCode)
        parcel.writeString(VehicleOwnerEmail)
        parcel.writeString(DriverAccountID)
        parcel.writeString(Driver)
        parcel.writeString(DriverMobile)
        parcel.writeString(DriverMobileWithCode)
        parcel.writeString(DriverEmail)
        parcel.writeString(DrivingLicenseNo)
        parcel.writeString(ViolatorAccountID)
        parcel.writeString(Violator)
        parcel.writeString(ViolatorMobile)
        parcel.writeString(ViolatorMobileWithCode)
        parcel.writeString(ViolatorEmail)
        parcel.writeString(GoodsOwnerAccountID)
        parcel.writeString(GoodsOwner)
        parcel.writeString(GoodsOwnerMobile)
        parcel.writeString(GoodsOwnerMobileWithCode)
        parcel.writeString(GoodsOwnerEmail)
        parcel.writeValue(FineAmount)
        parcel.writeValue(ImpoundmentCharge)
        parcel.writeString(CreatedBy)
        parcel.writeString(ImpoundedBy)
        parcel.writeString(ReturnedBy)
        parcel.writeString(ImpoundmentReturnDate)
        parcel.writeValue(GeoAddressID)
        parcel.writeValue(NetReceivable)
        parcel.writeValue(TransactionDue)
        parcel.writeString(TaxNoticeReferenceNo)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeValue(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LawPendingTransactionLocations> {
        override fun createFromParcel(parcel: Parcel): LawPendingTransactionLocations {
            return LawPendingTransactionLocations(parcel)
        }

        override fun newArray(size: Int): Array<LawPendingTransactionLocations?> {
            return arrayOfNulls(size)
        }
    }

    fun markerColor(): String {
        //Impond: #8E44AD ==> IMPOUNDMENT
        //vioaltion: #F1C40F ==> VIOLATION_TICKETS
        if (isViolation()) {
            return "#F1C40F"
        } else {
            return "#8E44AD"
        }
    }

    private fun isViolation(): Boolean {
        return InvoiceTransactionTypeCode == "VIOLATION_TICKETS"
    }

    fun refNo():String{
        if (isViolation()) {
            return getString(R.string.violation_reference_no)
        } else {
            return getString(R.string.impond_reference_no)
        }
    }

    fun hDetails():String{
       /* return if (isViolation()) {
            getString(R.string.violation_details)
        } else {
            getString(R.string.impond_details)    //changed for SR
        }*/
        return getString(R.string.violation_details)
    }

    fun hNo():String{
        return if (isViolation()) {
            getString(R.string.violation_no)
        } else {
            getString(R.string.impond_no)
        }
    }

    fun hType():String{
        return if (isViolation()) {
            getString(R.string.violation_type)
        } else {
            getString(R.string.impond_type)
        }
    }
    fun type():String{
        return if (isViolation()) {
            ViolationType.toString()
        } else {
            ImpoundmentType.toString()
        }
    }

    fun hSubType():String{
        return if (isViolation()) {
            getString(R.string.violation_sub_type)
        } else {
            getString(R.string.impond_sub_type)
        }
    }
    fun subType():String{
        return if (isViolation()) {
            ViolationClass.toString()
        } else {
            ImpoundmentSubType.toString()
        }
    }

    fun date():String{
        return displayFormatDate(InvoiceTransactionVoucherDate)
    }

    fun netReceivableAmount():String{
        return formatWithPrecision(NetReceivable)
    }

    fun transactionDueAmount():String{
        return formatWithPrecision(TransactionDue)
    }

    fun details():String{
        return if (isViolation()) {
            return ViolationDetails?.toString() ?: ""
        } else {
            return if (!ImpoundmentReason.isNullOrEmpty()) {
                ImpoundmentReason.toString()
            } else ""
        }
    }

    fun isOwnerName():Boolean{
        return !VehicleOwner.isNullOrEmpty()
    }
    fun isDriverName():Boolean{
        return !Driver.isNullOrEmpty()
    }
    fun isViolatorName():Boolean{
        return !Violator.isNullOrEmpty()
    }
    fun isGoodsOwnerName():Boolean{
        return !GoodsOwner.isNullOrEmpty()
    }

    override fun getPosition(): LatLng {
        val lat: Double? = this.latitude?.toDouble()
        val lng: Double? = this.longitude?.toDouble()
        return lat?.let { lng?.let { it1 -> LatLng(it, it1) } }!!
    }

    override fun getTitle(): String {
        return ""
    }

    override fun getSnippet(): String {
        return ""
    }

}