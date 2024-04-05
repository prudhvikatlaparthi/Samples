package com.pru.touchnote.data.repositories

import com.pru.newsapp.mvvm.db.UsersDao
import com.pru.touchnote.data.model.Data
import com.pru.touchnote.remote.GoRestAPI
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val goRestAPI: GoRestAPI,
    private val usersDao: UsersDao
) {
   suspend fun fetchUsers(page :Int) = goRestAPI.fetchUsers(page)

    suspend fun postUser(data: Data) = goRestAPI.postUser(data)

    fun postUserRXJ(data: Data) = goRestAPI.postUserRXJ(data)

    suspend fun upsertData(response :List<Data>) = usersDao.upInsert(response)

    fun upsertDataRXJ(response :List<Data>) = usersDao.upInsertRXJ(response)

    fun fetchUsersRXJ(page: Int) = goRestAPI.fetchUsersRXJ(page)
}