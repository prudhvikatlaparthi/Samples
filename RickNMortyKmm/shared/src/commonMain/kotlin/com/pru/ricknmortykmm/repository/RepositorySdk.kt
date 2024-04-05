package com.pru.ricknmortykmm.repository

import com.pru.ricknmortykmm.models.response.ProfileDto
import com.pru.ricknmortykmm.models.response.ValueDto
import com.pru.ricknmortykmm.remote.ApiService
import com.pru.ricknmortykmm.utils.ApiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositorySdk(private val apiService: ApiService) {

    fun getProfile(): Flow<ApiState<ProfileDto>> {
        return apiService.callAPI(url = "https://rickandmortyapi.com/api/character/1")
    }

    fun getValues(): Flow<ApiState<List<ValueDto>>> {
        return apiService.callAPI(url = "https://63caa804f36cbbdfc75d55c6.mockapi.io/api/v1/values")
    }

    fun getValue(): Flow<ApiState<ValueDto>> {
        return apiService.callAPI(url = "https://mocki.io/v1/c0df786c-be23-49c8-be72-ae19603ce191")
    }

}