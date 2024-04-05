package com.pru.judostoreapp.remote

import io.ktor.client.*
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
    config(this)
    install(HttpTimeout) {
        connectTimeoutMillis = 300000
        requestTimeoutMillis = 300000
        socketTimeoutMillis  = 300000
    }
    engine {
        configureRequest {

        }
    }
}