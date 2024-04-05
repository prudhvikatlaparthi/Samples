package com.pru.shopping.androidApp.viewmodels

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.pru.shopping.androidApp.utils.Resource
import com.pru.shopping.androidApp.utils.kErrorMessage
import com.pru.shopping.shared.commonModels.UserResponse
import com.pru.shopping.shared.commonRepositories.RepositorySDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoDetailViewModel @ViewModelInject constructor(
    private val repositorySDK: RepositorySDK,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val TAG = "TodoDetailViewModel"
    private val _todoDetail = MutableLiveData<Resource<UserResponse>>()
    val todoDetail: LiveData<Resource<UserResponse>>
        get() = _todoDetail


    fun postUser(etEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        /* _todoDetail.postValue(Resource.Loading())
         try {
             val data = todoRepository.getTodoDetails(id)
             _todoDetail.postValue(Resource.Success(data))
         } catch (e: Exception) {
             _todoDetail.postValue(Resource.Error(message = "Error! ${e.message ?: "Something went wrong"}"))
         }*/

        viewModelScope.launch(Dispatchers.IO) {
            _todoDetail.postValue(Resource.Loading())
            kotlin.runCatching {
                repositorySDK.postUser(etEmail)
            }.onSuccess {
                _todoDetail.postValue(Resource.Success(it))
                Log.i(TAG, "getTodoDetails: ${it}")
            }.onFailure {
                _todoDetail.postValue(Resource.Error(it.message ?: kErrorMessage))
                Log.i(TAG, "getTodoDetails: ${it}")
            }
        }
    }
}