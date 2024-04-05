package com.pru.hiltarchi.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.hiltarchi.listeners.ClickEvent
import com.pru.hiltarchi.models.TodoItem
import com.pru.hiltarchi.models.TodoList
import com.pru.hiltarchi.remote.APIRepository
import com.pru.hiltarchi.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val apiRespository: APIRepository
) : ViewModel() {
    private val _todos = MutableLiveData<Resource<TodoList>>()
    val todos: LiveData<Resource<TodoList>>
        get() = _todos
    private var index: Int = 1

    var motionProgress = 0f

    private val _dos = MutableLiveData<ClickEvent<String>>()
    val dos: LiveData<ClickEvent<String>>
        get() = _dos


    fun displayToast() = viewModelScope.launch {
        delay(1000)
        val event = ClickEvent("Hello")
        if (!event.hasBeenHandled){
            _dos.postValue(ClickEvent("Hello"))
        }
    }

    fun getTodos() = viewModelScope.launch(Dispatchers.IO) {

        val todoList: MutableList<TodoItem> = mutableListOf()
        if (index >= 30) {
            index = 1
        }
        for (i in 1..10) {
            index++
            val todoItem = TodoItem(id = index, title = "$index item", userId = index)
            todoList.add(todoItem)
        }
        _todos.postValue(Resource.Success(TodoList(data = todoList)))
    }
}