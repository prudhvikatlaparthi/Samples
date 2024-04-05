package com.pru.misc.remote

import com.pru.misc.model.PostsResponse
import com.pru.misc.utils.APIConstants.BASEURL
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class APIService @Inject constructor(private val httpClient: HttpClient) {
    @Throws(Exception::class)
    suspend fun getPosts(endPoint: String): List<PostsResponse> {
        return httpClient.get(url = URLBuilder(BASEURL + endPoint).build())
    }
}