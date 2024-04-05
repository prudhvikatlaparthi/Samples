package com.pru.judostoreapp.remote

import io.ktor.client.*
import io.ktor.client.engine.android.*

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Android) {
    config(this)

    engine {
        connectTimeout = 10_000
        socketTimeout = 10_000
    }
}