package com.pru.ktordemo.android

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.ktordemo.Post
import com.pru.ktordemo.RepositorySDK
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _dataList = mutableStateListOf<Post>()
    val dataList: List<Post>
        get() = _dataList

    init {
        viewModelScope.launch {
            val data = RepositorySDK.getUsers()
            _dataList.addAll(data)
        }
    }
}