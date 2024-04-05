package com.pru.localapp.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomMigration(startVersion: Int, endVersion: Int) : Migration(startVersion, endVersion) {
    override fun migrate(database: SupportSQLiteDatabase) {
        when (Pair(startVersion,endVersion)) {
            Pair(1,2) -> {
                database.execSQL("ALTER TABLE [CrmCustomer] ADD [loyaltyPoints] REAL NOT NULL DEFAULT 0;")
            }
            Pair(2,3) -> {
                database.execSQL("Alter Table CrmCustomer add Column address Varchar(255)")
            }
            Pair(3,4) -> {
                database.execSQL("CREATE VIEW ViewCustomer AS Select c.* ,vm.*,isActive from CrmCustomer c inner join CrmCustomerVehicle vm On c.customerId==vm.accountId")
            }
        }
    }
}