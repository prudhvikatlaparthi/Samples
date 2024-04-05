package com.mindorks.framework.mvi.ui.main.intent

import com.mindorks.framework.mvi.util.Resource

sealed class HomeEventIntent {
    object FetchInfo : HomeEventIntent()
    class Success<T>(data: T) : HomeEventIntent()
    class SendEvent<T>(data: T) : HomeEventIntent()
}
