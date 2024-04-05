package com.pru.misc.ui.posts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.misc.model.PostsResponse
import com.pru.misc.repository.APIRepositorySDK
import com.pru.misc.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val apiRepositorySDK: APIRepositorySDK
) : ViewModel() {

    private val _posts = mutableStateOf<List<PostsResponse>>(emptyList())
    val posts: State<List<PostsResponse>>
        get() = _posts

    init {
        getPosts()
    }

    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            apiRepositorySDK.getPosts().collect { result ->
                when (result) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _posts.value = it
                        }
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }
}