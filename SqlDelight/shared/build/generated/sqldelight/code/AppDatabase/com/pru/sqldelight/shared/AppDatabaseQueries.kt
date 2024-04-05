package com.pru.sqldelight.shared

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String

interface AppDatabaseQueries : Transacter {
  fun <T : Any> selectTOKENS(mapper: (
    staticToken: String,
    dynamicToken: String?,
    secretKey: String?,
    domainAccountID: Long?,
    userOrgBRID: Long?
  ) -> T): Query<T>

  fun selectTOKENS(): Query<TOKEN>

  fun insertTOKEN(
    dynamicToken: String?,
    secretKey: String?,
    domainAccountID: Long?,
    userOrgBRID: Long?
  )

  fun deleteAllTOKENS()
}
