package com.pru.shopping.androidApp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.shopping.androidApp.utils.Resource
import com.pru.shopping.androidApp.utils.kErrorMessage
import com.pru.shopping.shared.commonModels.RocketLaunch
import com.pru.shopping.shared.commonModels.TodoItem
import com.pru.shopping.shared.commonRepositories.RepositorySDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val apiRepositorySDK : RepositorySDK
) : ViewModel() {

    private val _rocketsState = MutableLiveData<Resource<List<RocketLaunch>>>()
    val rocketState: LiveData<Resource<List<RocketLaunch>>>
        get() = _rocketsState

    private val _todosState = MutableLiveData<Resource<List<TodoItem>>>()
    val todosState: LiveData<Resource<List<TodoItem>>>
        get() = _todosState


    init {
//        fetchData()
        fetchTodos()
    }

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            _rocketsState.postValue(Resource.Loading())
            kotlin.runCatching {
                apiRepositorySDK.getLaunches()
            }.onSuccess {
                _rocketsState.postValue(Resource.Success(it))
            }.onFailure {
                _rocketsState.postValue(Resource.Error(message = it.message ?: kErrorMessage))
            }
        }
    }

    private fun fetchTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            _todosState.postValue(Resource.Loading())
            kotlin.runCatching {
                apiRepositorySDK.getTodos()
            }.onSuccess {
                _todosState.postValue(Resource.Success(it))
            }.onFailure {
                _todosState.postValue(Resource.Error(message = it.message))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}