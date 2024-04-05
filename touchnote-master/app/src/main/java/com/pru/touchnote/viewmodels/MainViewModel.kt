package com.pru.touchnote.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.touchnote.data.model.Data
import com.pru.touchnote.data.model.UserResponse
import com.pru.touchnote.data.repositories.MainRepository
import com.pru.touchnote.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    val usersData: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    var page = 1
    private var initialData: UserResponse? = null
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    init {
//        fetchUsers()
        fetchUsersRXJ()
    }

    fun fetchUsers() = viewModelScope.launch {
        mainRepository.upsertData(emptyList())
        usersData.postValue(Resource.Loading())
        try {
            val response = mainRepository.fetchUsers(page)
            if (response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    page++
                    if (initialData == null) {
                        initialData = resultResponse
                    } else {
                        val oldArticles = initialData?.data
                        val newArticles = resultResponse.data
                        oldArticles?.addAll(newArticles)
                    }
                    usersData.postValue((Resource.Success(initialData ?: resultResponse)))
                }
                mainRepository.upsertData(response.body()?.data?.toList() ?: emptyList())
            } else {
                usersData.postValue(Resource.Error(response.message()))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> usersData.postValue(Resource.Error("Error! Network Failure."))
                else -> usersData.postValue(Resource.Error("Error! ${t.message}."))
            }
        }
    }

    fun saveToLocal(data: List<Data>) {
        compositeDisposable.add(
            mainRepository.upsertDataRXJ(data).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    print("completed");
                }

        )
    }

    fun fetchUsersRXJ() {
        usersData.postValue(Resource.Loading())
        compositeDisposable.add(
            mainRepository.fetchUsersRXJ(page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ resultResponse ->
                    page++
                    if (initialData == null) {
                        initialData = resultResponse
                    } else {
                        val oldArticles = initialData?.data
                        val newArticles = resultResponse.data
                        oldArticles?.addAll(newArticles)
                    }
                    saveToLocal(resultResponse?.data?.toList() ?: emptyList())
                    usersData.postValue((Resource.Success(initialData ?: resultResponse)))
                }, { t ->
                    when (t) {
                        is IOException -> usersData.postValue(Resource.Error("Error! Network Failure."))
                        else -> usersData.postValue(Resource.Error("Error! ${t.message}."))
                    }
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()

    }
}