package com.pru.sqldelight.shared

import kotlin.Long
import kotlin.String

data class TOKEN(
  val staticToken: String,
  val dynamicToken: String?,
  val secretKey: String?,
  val domainAccountID: Long?,
  val userOrgBRID: Long?
) {
  override fun toString(): String = """
  |TOKEN [
  |  staticToken: $staticToken
  |  dynamicToken: $dynamicToken
  |  secretKey: $secretKey
  |  domainAccountID: $domainAccountID
  |  userOrgBRID: $userOrgBRID
  |]
  """.trimMargin()
}
