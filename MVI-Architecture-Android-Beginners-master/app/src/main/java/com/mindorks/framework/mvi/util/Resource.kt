package com.mindorks.framework.mvi.util

import java.lang.Exception

sealed class Resource {
    class Success(val data: Any) : Resource()
    class Error(val e: Exception) : Resource()
    object Loading : Resource()
}