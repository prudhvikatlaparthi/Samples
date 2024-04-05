package com.pru.misc.repository

import com.pru.misc.model.PostsResponse
import com.pru.misc.utils.Resource
import kotlinx.coroutines.flow.Flow

interface APIRepository {
    suspend fun getPosts(): Flow<Resource<List<PostsResponse>>>
}