package com.mindorks.framework.mvi.ui.main.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindorks.framework.mvi.data.repository.HomeRepository
import com.mindorks.framework.mvi.ui.main.intent.HomeEventIntent
import com.mindorks.framework.mvi.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeViewModel @ViewModelInject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    val homeEventIntent = Channel<HomeEventIntent>(Channel.UNLIMITED)

    private val _myLoad = MutableLiveData<Resource>()
    val myLoad: LiveData<Resource>
        get() = _myLoad

    init {
        startSubscription()
    }

    private fun startSubscription() {
        viewModelScope.launch {
            homeEventIntent.consumeAsFlow().collect {
                when (it) {
                    is HomeEventIntent.FetchInfo -> {
                        homeRepository.getInfo().onEach {
                            _myLoad.value = it
                        }.launchIn(viewModelScope)
                    }
                    is HomeEventIntent.SendEvent<*> -> {
                    }
                }
            }
        }
    }
}