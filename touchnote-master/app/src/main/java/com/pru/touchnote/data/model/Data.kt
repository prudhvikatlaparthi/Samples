package com.pru.touchnote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Data(
    val created_at: String? = null,
    val email: String? = null,
    val gender: String? = null,
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String? = null,
    val status: String? = null,
    val updated_at: String? = null,
    val check_test : String? = null
) : Serializable