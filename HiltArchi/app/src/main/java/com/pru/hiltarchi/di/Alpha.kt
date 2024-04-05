package com.pru.hiltarchi.di

object Alpha {
    private var instance: Alpha? = null

    @Synchronized
    private fun createInstance() {
        if (instance == null) {
            instance = Alpha
        }
    }

    fun getInstance(): Alpha? {
        if (instance == null) createInstance()
        return instance
    }
}