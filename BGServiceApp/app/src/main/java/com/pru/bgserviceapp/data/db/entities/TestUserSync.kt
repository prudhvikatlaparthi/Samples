package com.pru.bgserviceapp.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_test_user_sync")
data class TestUserSync(
    @PrimaryKey var uid: Int? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "is-synced", defaultValue = "N") var isSynced: String,
)
