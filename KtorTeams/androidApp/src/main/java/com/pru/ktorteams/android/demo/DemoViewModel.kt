package com.pru.ktorteams.android.demo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.ktorteams.Post
import com.pru.ktorteams.RepositorySDK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(val repository: Repository): ViewModel() {

    private val _counterState = MutableLiveData(1)
    val counterState: LiveData<Int>
        get() = _counterState

    private val _postsState = MutableLiveData<List<Post>>(emptyList())
    val postsState: LiveData<List<Post>>
        get() = _postsState

    init {
        repository.getPosts().forEach {
            Log.i("Prudhvi Log", "$it:")
        }
        getPosts()
    }

    fun increment() {
        _counterState.value = _counterState.value?.plus(1)
    }

    fun getPosts() {
        viewModelScope.launch {
            val data = RepositorySDK.getPosts()
            _postsState.value = data
        }
    }
}