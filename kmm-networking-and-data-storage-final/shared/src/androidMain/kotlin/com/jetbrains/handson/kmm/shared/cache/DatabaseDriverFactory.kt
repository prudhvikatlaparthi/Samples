package com.jetbrains.handson.kmm.shared.cache

import android.content.Context
import com.jetbrains.handson.kmm.shared.cache.AppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
       val sqlDriver = AndroidSqliteDriver(schema = AppDatabase.Schema, context, "test.db")

        return sqlDriver
    }
}