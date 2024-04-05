package com.pru.sqldelight.shared

class Apicall(private val databaseDriverFactory: DatabaseDriverFactory) {
    private val database = Database(databaseDriverFactory)

    fun insertData(tblTokens: TblTokens){
        database.insertToken(tblTokens)
    }

    fun getData() : List<TblTokens> = database.getTokens()

}