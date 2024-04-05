package com.pru.roomdbsample.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "basic_details")
data class BasicDetailsEntity(
    @PrimaryKey(autoGenerate = false) val reportId: Long = 0,
    val name: String,
)

@Entity(
    tableName = "layout", foreignKeys = [ForeignKey(
        entity = BasicDetailsEntity::class,
        parentColumns = ["reportId"],
        childColumns = ["reportId"],
        onDelete = CASCADE
    )]
)
data class LayoutEntity(
    @PrimaryKey(autoGenerate = false) val basicDetailsId: Long = 0,
    val mergeWithAboveLayout: Int,
    val sortOrder: Int,
    val reportId: Long
)

@Entity(
    tableName = "control", foreignKeys = [ForeignKey(
        entity = LayoutEntity::class,
        parentColumns = ["mergeWithAboveLayout"],
        childColumns = ["mergeWithAboveLayout"],
        onDelete = CASCADE
    )]
)
data class ControlEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val numberOfColumns: Int,
    val value: String,
    val hint: String,
    val inputType: String,
    val inputData: String?,
    val dataType: String,
    val colour: String,
    val isInfoIcon: Int,
    val infoText: String,
    val layoutId: Long,
    val mergeWithAboveLayout: Int,
)