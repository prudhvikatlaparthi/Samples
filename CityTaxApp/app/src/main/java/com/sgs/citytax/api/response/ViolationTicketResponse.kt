package com.sgs.citytax.api.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ViolationTicketResponse(
       @SerializedName("TicketID")
       var ticketId:Int?=0,
       @SerializedName("InvoiceID")
       var invoiceId:Int?=0,
       var taxRuleBookCode:String?=""
) : Parcelable {
       constructor(parcel: Parcel) : this(
               parcel.readValue(Int::class.java.classLoader) as? Int,
               parcel.readValue(Int::class.java.classLoader) as? Int,
               parcel.readString()) {
       }

       override fun writeToParcel(parcel: Parcel, flags: Int) {
              parcel.writeValue(ticketId)
              parcel.writeValue(invoiceId)
              parcel.writeString(taxRuleBookCode)
       }

       override fun describeContents(): Int {
              return 0
       }

       companion object CREATOR : Parcelable.Creator<ViolationTicketResponse> {
              override fun createFromParcel(parcel: Parcel): ViolationTicketResponse {
                     return ViolationTicketResponse(parcel)
              }

              override fun newArray(size: Int): Array<ViolationTicketResponse?> {
                     return arrayOfNulls(size)
              }
       }
}