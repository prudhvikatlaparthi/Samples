package com.sgs.citytax.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrgData(
    @SerializedName("QRCodeNote")
    val qRCodeNote: String? = null,
    @SerializedName("QRCodeNote2")
    val qRCodeNote2: String? = null,
    @SerializedName("TreasuryLogoID")
    val treasuryLogoID: Int? = null,
    @SerializedName("TreasuryLogoPath")
    val treasuryLogoPath: String? = null
) : Parcelable