package com.pru.offlineapp.sync_server

import com.pru.offlineapp.database.AppDatabase
import com.pru.offlineapp.database.Tables
import kotlinx.coroutines.delay

class AppSync {
    companion object {
        @Volatile
        private var INSTANCE: AppSync? = null

        fun getInstance(): AppSync = INSTANCE ?: synchronized(this) {
            INSTANCE ?: AppSync()
        }
    }

    private var isSyncing = false

    suspend fun uploadData() {
        if (isSyncing) {
            return
        }
        isSyncing = true
        for (table in Tables.entries) {
            when (table) {
                Tables.TblProperty -> {
                    AppDatabase.getDatabase().propertyDao().getPropertiesToUpload().forEach {
                        delay(1000)
                        it.webPropertyID =
                            if (it.webPropertyID == 0) it.propertyID!! else it.webPropertyID
                        it.modifiedFrom = "W"
                        AppDatabase.getDatabase().propertyDao().upsert(it)
                    }
                }
            }
        }

        isSyncing = false
    }
}