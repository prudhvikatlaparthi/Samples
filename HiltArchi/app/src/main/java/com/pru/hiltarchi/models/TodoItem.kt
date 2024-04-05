package com.pru.hiltarchi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TodoItem(
    val completed: Boolean? = false,
    val id: Int? = null,
    val title: String? = null,
    val userId: Int? = null
) : Parcelable