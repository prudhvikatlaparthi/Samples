package com.sgs.citytax.api

interface ConnectionCallBack<T> {
    fun onSuccess(response: T)
    fun onFailure(message: String)
}