package com.mindorks.framework.mvi.ui.main.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindorks.framework.mvi.data.repository.MainRepository
import com.mindorks.framework.mvi.ui.main.intent.MainStateIntent
import com.mindorks.framework.mvi.ui.main.viewstate.MainState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val userIntent = Channel<MainStateIntent>(Channel.UNLIMITED)
    private val _data = MutableLiveData<MainState>()
    val data: LiveData<MainState>
        get() = _data

    init {
        handleIntent()
    }
    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when(it){
                    MainStateIntent.FetchUser ->{
                        repository.getData().onEach {
                            _data.value = it
                        }.launchIn(viewModelScope)
                    }
                }
            }
        }
    }
}