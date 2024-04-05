package com.sgs.citytax.api.payload

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMNotes
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.util.Constant

data class StoreCustomerB2B(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("org")
        var organization: Organization? = Organization(),
        @SerializedName("add")
        var geoAddress: GeoAddress? = null,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference>? = arrayListOf(),
        @SerializedName("notes")
        var note: List<COMNotes>? = arrayListOf(),
        @SerializedName("acctypecode")
        var accountTypeCode: Constant.AccountTypeCode = Constant.AccountTypeCode.CRO
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(SecurityContext::class.java.classLoader),
            parcel.readParcelable(Organization::class.java.classLoader),
            parcel.readParcelable(GeoAddress::class.java.classLoader),
            parcel.createTypedArrayList(COMDocumentReference),
            parcel.createTypedArrayList(COMNotes),
            parcel.readSerializable() as Constant.AccountTypeCode)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(context, flags)
        parcel.writeParcelable(organization, flags)
        parcel.writeParcelable(geoAddress, flags)
        parcel.writeTypedList(attachment)
        parcel.writeTypedList(note)
        parcel.writeSerializable(accountTypeCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoreCustomerB2B> {
        override fun createFromParcel(parcel: Parcel): StoreCustomerB2B {
            return StoreCustomerB2B(parcel)
        }

        override fun newArray(size: Int): Array<StoreCustomerB2B?> {
            return arrayOfNulls(size)
        }
    }
}