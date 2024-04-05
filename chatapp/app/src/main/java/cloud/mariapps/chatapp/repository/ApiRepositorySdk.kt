package cloud.mariapps.chatapp.repository

import cloud.mariapps.chatapp.remote.ApiService

class ApiRepositorySdk(private val apiService: ApiService) : ApiRepository {
    override suspend fun login() {

    }
}