package com.pru.judostoreapp.repo

import com.pru.judostoreapp.models.User
import com.pru.judostoreapp.remote.RemoteService

class RepositorySDK(
    private val remoteService: RemoteService
) {

    suspend fun authenticate(userName: String, password: String): Result<User> {

        return remoteService.makePostCall(endPoint = "/user/authenticate", rawBody = User(userName = userName, password = password))
    }
}