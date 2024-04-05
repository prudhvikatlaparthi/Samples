package cloud.mariapps.chatapp.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/api/login")
    suspend fun login(
        @Query("page") page: Int,
        @Query("per_page") size: Int
    ): Boolean
}