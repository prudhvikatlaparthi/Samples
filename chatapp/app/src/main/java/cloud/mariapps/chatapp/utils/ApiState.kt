package cloud.mariapps.chatapp.utils

import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.utils.Global.getString

sealed class ApiState<T>(
    val data: T? = null,
    val errorMessage: String = ""
) {
    class Success<T>(data: T) : ApiState<T>(data)
    class Failure<T>(errorMessage: String?) : ApiState<T>(
        errorMessage = errorMessage ?: getString(R.string.msg_no_data)
    )

    class Loading<T> : ApiState<T>()
//    class Initial<T> : ApiState<T>()
}