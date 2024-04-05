package com.pru.sqldelight.shared


internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun insertToken(item: TblTokens) {
        dbQuery.insertTOKEN(
            dynamicToken = item.dynamicToken,
            secretKey = item.secretKey,
            domainAccountID = item.domainAccountID?.toLong(),
            userOrgBRID = item.userOrgBRID?.toLong()
        )
    }

    internal fun getTokens(): List<TblTokens> = dbQuery.selectTOKENS(::retrieveData).executeAsList()

    private fun retrieveData(
        staticToken: String?,
        dynamicToken: String?,
        secretKey: String?,
        domainAccountID: Long?,
        userOrgBRID: Long?
    ): TblTokens {
        return TblTokens(
            staticToken = staticToken,
            dynamicToken = dynamicToken,
            secretKey = secretKey,
            domainAccountID = domainAccountID?.toInt(),
            userOrgBRID = userOrgBRID?.toInt()
        )
    }
}