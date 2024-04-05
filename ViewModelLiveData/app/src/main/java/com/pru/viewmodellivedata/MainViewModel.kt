package com.pru.viewmodellivedata

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var counter = 0
    private var _data: MutableLiveData<String> = MutableLiveData("")
    var data: LiveData<String> = _data

    private var _data1: MutableLiveData<String> = MutableLiveData("")
    var data1: LiveData<String> = _data1

    var commonSource: MediatorLiveData<String> = MediatorLiveData()

    init {
        commonSource.addSource(_data) {
            commonSource.value = it
        }

        commonSource.addSource(data1) {
            commonSource.value = it
        }
    }

    fun callAPI() {
        viewModelScope.launch {
            delay(500)
            _data.value = "Hello there!!"
            delay(500)
            _data1.value = "Hello there 1 !!"
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("Prudhvi Log", "onCleared: ")
    }
}

object Constants {
//    companion object {
        val kSo = "so"
//    }
}