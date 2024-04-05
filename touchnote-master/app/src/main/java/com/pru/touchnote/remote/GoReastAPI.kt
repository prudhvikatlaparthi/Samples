 package com.pru.touchnote.remote

import com.pru.touchnote.BuildConfig
import com.pru.touchnote.data.model.Data
import com.pru.touchnote.data.model.PostUserResponse
import com.pru.touchnote.data.model.UserResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface GoRestAPI {
    companion object {
        const val BASE_URL = "https://gorest.co.in/public-api/"
        const val CLIENT_ID = BuildConfig.GOREST_ACCESS_KEY
    }

    @GET("users")
    suspend fun fetchUsers(
        @Query("page") page: Int,
    ): Response<UserResponse>

    @Headers("Authorization: Bearer $CLIENT_ID")
    @POST("users")
    suspend fun postUser(
        @Body user: Data
    ): Response<PostUserResponse>

    @Headers("Authorization: Bearer $CLIENT_ID")
    @POST("users")
    fun postUserRXJ(
        @Body user: Data
    ): Single<PostUserResponse>

    @GET("users")
    fun fetchUsersRXJ(
        @Query("page") page: Int,
    ): Observable<UserResponse>
}