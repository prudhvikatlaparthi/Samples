package com.pru.responsiveapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataItem(
    var title: String,
    var description: String
) : Parcelable
