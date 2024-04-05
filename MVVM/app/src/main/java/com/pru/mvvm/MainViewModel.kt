package com.pru.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    private val _userData: MutableLiveData<List<String>> = MutableLiveData()
    val userData: LiveData<List<String>>
        get() = _userData


    suspend fun getUserNames(): List<String> {
        return List(20) {
            "user $it"
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            delay(2000)
            val users = getUserNames()
            _userData.value = users
        }
    }
}