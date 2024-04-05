package com.sgs.citytax.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sgs.citytax.api.response.SellableProduct

class SellableTaxViewModel : ViewModel() {

    private val _productsList = MutableLiveData<List<SellableProduct>>()

    val productList: LiveData<List<SellableProduct>>
        get() = _productsList


    fun getSellableProducts() {

    }

}