package com.sgs.citytax.model

import android.R.attr.name
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sgs.citytax.util.splitData
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TaxPayerList(
        @SerializedName("Customer")
        var customer: String? = "",
        @SerializedName("custid", alternate = ["CustomerID"])
        var CustomerID: Int,
        @SerializedName("SycotaxID")
        var sycoTaxID: String? = "",
        @SerializedName("BusinessName")
        var businessName: String? = "",
        @SerializedName("OwnersWithNumber")
        var ownersWithNumber: String? = "",
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false,
        var dataString: String = "Business Owner:1213123123;Business Owner2:4545454545;"
) : Parcelable {


    override fun toString(): String {
        return customer + "\n" + sycoTaxID+"\n"+ if (ownersWithNumber == null) "" else splitData(ownersWithNumber!!)
      //  return "$customer\n$sycoTaxID\n${ownersWithNumber?.let { splitData(it) }}"
    }

    /*fun splitData(sss:String):String{
        val inputString0 = sss
        if(inputString0!=null)
       return inputString0.replace(';', '\n')
        else
            return ""
    }*/
}