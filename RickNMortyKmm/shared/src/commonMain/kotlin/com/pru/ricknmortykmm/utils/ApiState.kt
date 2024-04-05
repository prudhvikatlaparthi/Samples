package com.pru.ricknmortykmm.utils


sealed class ApiState<T>(
    val data: T? = null,
    val errorMessage: String = ""
) {
    class Success<T>(data: T) : ApiState<T>(data)
    class Failure<T>(errorMessage: String?) : ApiState<T>(
        errorMessage = errorMessage ?: "Something went wrong"
    )

    class Loading<T> : ApiState<T>()
//    class Initial<T> : ApiState<T>()
}