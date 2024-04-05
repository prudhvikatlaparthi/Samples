package com.pru.bgserviceapp.domain.repository

import com.pru.bgserviceapp.data.db.entities.TestUserSync

interface AppRepository {

    suspend fun getAllCachedUsers(): List<TestUserSync>

    suspend fun getUnSyncedUsers(): List<TestUserSync>

    suspend fun cacheUsers(users: List<TestUserSync>)
}