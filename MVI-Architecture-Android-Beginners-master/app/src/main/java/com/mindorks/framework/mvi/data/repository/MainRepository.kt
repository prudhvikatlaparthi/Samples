package com.mindorks.framework.mvi.data.repository

import com.mindorks.framework.mvi.data.api.ApiService
import com.mindorks.framework.mvi.data.model.User
import com.mindorks.framework.mvi.ui.main.viewstate.MainState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MainRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getData(): Flow<MainState> = flow {
        emit(MainState.Loading)
        try {
            emit(MainState.Success(apiService.getUsers() as List<User>))
        } catch (e: Exception) {
            emit(MainState.Error(e))
        }
    }

}