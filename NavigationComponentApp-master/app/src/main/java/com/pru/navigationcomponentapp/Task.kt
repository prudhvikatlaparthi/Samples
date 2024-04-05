package com.pru.navigationcomponentapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(var taskName: String, val id: Int) : Parcelable
