package com.pru.bgserviceapp.data.db.repository

import com.pru.bgserviceapp.data.db.database.AppDatabase
import com.pru.bgserviceapp.data.db.entities.TestUserSync
import com.pru.bgserviceapp.domain.repository.AppRepository

class AppRepositorySdk(appDatabase: AppDatabase) : AppRepository {
    private val testUserSyncDao = appDatabase.testUserSyncDao()

    override suspend fun getAllCachedUsers(): List<TestUserSync> {
        return testUserSyncDao.getAll()
    }

    override suspend fun getUnSyncedUsers(): List<TestUserSync> {
        return testUserSyncDao.getUnSyncedUsers()
    }

    override suspend fun cacheUsers(users: List<TestUserSync>) {
        testUserSyncDao.upsert(users)
    }

}