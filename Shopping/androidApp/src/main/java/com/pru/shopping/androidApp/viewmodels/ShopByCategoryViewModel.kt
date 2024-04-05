package com.pru.shopping.androidApp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.shopping.androidApp.utils.Resource
import com.pru.shopping.androidApp.utils.kErrorMessage
import com.pru.shopping.shared.commonRepositories.RepositorySDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopByCategoryViewModel @ViewModelInject constructor(
    private val repositorySDK: RepositorySDK
) : ViewModel() {

    private val _categoryItems = MutableLiveData<Resource<List<String>>>()
    val categoryItems: LiveData<Resource<List<String>>>
        get() = _categoryItems

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryItems.postValue(Resource.Loading())
            kotlin.runCatching {
                repositorySDK.getShopByCategories()
            }.onSuccess {
                _categoryItems.postValue(Resource.Success(data = it))
            }.onFailure {
                _categoryItems.postValue(Resource.Error(message = it.message ?: kErrorMessage))
            }
        }
    }
}