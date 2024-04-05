package com.pru.localapp.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CrmCustomer(
    @PrimaryKey
    val customerId: Int,
    val name: String,
    val mobile: String,
    val email: String,
    val address: String? = null,
    val loyaltyPoints: Double = 0.0
)
