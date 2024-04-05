package com.mindorks.framework.mvi.util

import okhttp3.logging.HttpLoggingInterceptor

fun getInterceptors(): HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return httpLoggingInterceptor
}