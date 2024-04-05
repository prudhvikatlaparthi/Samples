package com.mindorks.framework.mvi.data.repository

import com.mindorks.framework.mvi.data.api.ApiService
import com.mindorks.framework.mvi.data.model.User
import com.mindorks.framework.mvi.util.FailedException
import com.mindorks.framework.mvi.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getInfo(): Flow<Resource> = flow {
        emit(Resource.Loading)
        try {
            val data = apiService.getUsers()
            emit(Resource.Success(data = (data as List<User>)))
        } catch (e: Exception) {
            emit(Resource.Error(FailedException(e.message ?: e.localizedMessage ?: "Error")))
        }
    }.catch {
        emit(Resource.Error(FailedException("Flow error happened")))
    }
}