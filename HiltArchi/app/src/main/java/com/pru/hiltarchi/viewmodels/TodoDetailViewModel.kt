package com.pru.hiltarchi.viewmodels

import androidx.lifecycle.*
import com.pru.hiltarchi.models.TodoItem
import com.pru.hiltarchi.repositories.TodoRepository
import com.pru.hiltarchi.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id = savedStateHandle.get<Int>("todoID")
    private val _todoDetail = MutableLiveData<Resource<TodoItem>>()
    val todoDetail: LiveData<Resource<TodoItem>>
        get() = _todoDetail

    init {
        getTodoDetails(id ?: -1)
    }

    private fun getTodoDetails(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _todoDetail.postValue(Resource.Loading())
        try {
            val data = todoRepository.getTodoDetails(id)
            _todoDetail.postValue(Resource.Success(data))
        } catch (e: Exception) {
            _todoDetail.postValue(Resource.Error(message = "Error! ${e.message ?: "Something went wrong"}"))
        }
    }
}