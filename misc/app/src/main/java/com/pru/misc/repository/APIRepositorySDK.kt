package com.pru.misc.repository

import com.pru.misc.model.PostsResponse
import com.pru.misc.remote.APIService
import com.pru.misc.utils.APIConstants
import com.pru.misc.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class APIRepositorySDK @Inject constructor(private val apiService: APIService) : APIRepository {
    override suspend fun getPosts() = flow<Resource<List<PostsResponse>>> {
        emit(Resource.Loading())
        kotlin.runCatching {
            apiService.getPosts(APIConstants.POSTS)
        }.onSuccess {
            emit(Resource.Success(it))
        }.onFailure {
            emit(Resource.Error(it.message))
        }
    }

}