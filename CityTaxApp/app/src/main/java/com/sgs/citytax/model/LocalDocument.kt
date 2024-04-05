package com.sgs.citytax.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocalDocument(
    val localSrc: String? = null
) : Parcelable
