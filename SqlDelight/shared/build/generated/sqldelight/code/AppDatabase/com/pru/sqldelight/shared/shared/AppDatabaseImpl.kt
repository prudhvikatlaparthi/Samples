package com.pru.sqldelight.shared.shared

import com.pru.sqldelight.shared.AppDatabase
import com.pru.sqldelight.shared.AppDatabaseQueries
import com.pru.sqldelight.shared.TOKEN
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.internal.copyOnWriteList
import kotlin.Any
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.MutableList
import kotlin.reflect.KClass

internal val KClass<AppDatabase>.schema: SqlDriver.Schema
  get() = AppDatabaseImpl.Schema

internal fun KClass<AppDatabase>.newInstance(driver: SqlDriver): AppDatabase =
    AppDatabaseImpl(driver)

private class AppDatabaseImpl(
  driver: SqlDriver
) : TransacterImpl(driver), AppDatabase {
  override val appDatabaseQueries: AppDatabaseQueriesImpl = AppDatabaseQueriesImpl(this, driver)

  object Schema : SqlDriver.Schema {
    override val version: Int
      get() = 1

    override fun create(driver: SqlDriver) {
      driver.execute(null, """
          |CREATE TABLE TOKEN (
          |    staticToken TEXT NOT NULL,
          |    dynamicToken TEXT,
          |    secretKey TEXT,
          |    domainAccountID INTEGER,
          |    userOrgBRID INTEGER
          |)
          """.trimMargin(), 0)
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ) {
    }
  }
}

private class AppDatabaseQueriesImpl(
  private val database: AppDatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), AppDatabaseQueries {
  internal val selectTOKENS: MutableList<Query<*>> = copyOnWriteList()

  override fun <T : Any> selectTOKENS(mapper: (
    staticToken: String,
    dynamicToken: String?,
    secretKey: String?,
    domainAccountID: Long?,
    userOrgBRID: Long?
  ) -> T): Query<T> = Query(-1815639862, selectTOKENS, driver, "AppDatabase.sq", "selectTOKENS",
      "SELECT * FROM TOKEN") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1),
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getLong(4)
    )
  }

  override fun selectTOKENS(): Query<TOKEN> = selectTOKENS { staticToken, dynamicToken, secretKey,
      domainAccountID, userOrgBRID ->
    com.pru.sqldelight.shared.TOKEN(
      staticToken,
      dynamicToken,
      secretKey,
      domainAccountID,
      userOrgBRID
    )
  }

  override fun insertTOKEN(
    dynamicToken: String?,
    secretKey: String?,
    domainAccountID: Long?,
    userOrgBRID: Long?
  ) {
    driver.execute(-931361524, """
    |INSERT INTO TOKEN(staticToken, dynamicToken, secretKey, domainAccountID, userOrgBRID)
    |VALUES('ABC', ?, ?, ?, ?)
    """.trimMargin(), 4) {
      bindString(1, dynamicToken)
      bindString(2, secretKey)
      bindLong(3, domainAccountID)
      bindLong(4, userOrgBRID)
    }
    notifyQueries(-931361524, {database.appDatabaseQueries.selectTOKENS})
  }

  override fun deleteAllTOKENS() {
    driver.execute(1053687132, """DELETE FROM TOKEN""", 0)
    notifyQueries(1053687132, {database.appDatabaseQueries.selectTOKENS})
  }
}
