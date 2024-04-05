package com.pru.shopping.shared.commonRepositories

import com.pru.shopping.shared.commonModels.Data
import com.pru.shopping.shared.commonModels.RocketLaunch
import com.pru.shopping.shared.commonModels.TodoItem
import com.pru.shopping.shared.commonModels.UserResponse
import com.pru.shopping.shared.commonRemote.ApiService

class RepositorySDK {

    @Throws(Exception::class)
    suspend fun getLaunches(): List<RocketLaunch> {
        return ApiService.getAllLaunches()
    }

    @Throws(Exception::class)
    suspend fun getTodos(): List<TodoItem> {
        return ApiService.getTodos()
    }

    @Throws(Exception::class)
    suspend fun getShopByCategories(): List<String> {
        return ApiService.getShopByCategories()
    }

    @Throws(Exception::class)
    suspend fun postUser(email :String): UserResponse {
        val user = Data(
            name = "name",
            email = email,
            gender = "Male",
            status = "Active",
            id = -1
        )
        return ApiService.postUser(user)
    }
}
