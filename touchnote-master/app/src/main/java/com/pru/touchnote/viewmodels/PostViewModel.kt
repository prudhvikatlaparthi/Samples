package com.pru.touchnote.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.touchnote.data.model.Data
import com.pru.touchnote.data.model.PostUserResponse
import com.pru.touchnote.data.repositories.MainRepository
import com.pru.touchnote.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class PostViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    private var gender: String = "Male"

    fun setGender(g: String) {
        gender = g
    }

    fun getGender() = gender

    private var status: String = "Active"

    fun setStatus(s: String) {
        status = s
    }

    fun getStatus() = status

    val postResponse: MutableLiveData<Resource<PostUserResponse>> = MutableLiveData()

    fun postUser(name: String, email: String) = viewModelScope.launch {
        postResponse.postValue(Resource.Loading())
        try {
            val user = Data(
                name = name,
                email = email,
                gender = getGender(),
                status = getStatus(),
                id = -1
            )
            val response = mainRepository.postUser(user)
            postResponse.postValue(handleSearchNewsResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> postResponse.postValue(Resource.Error("Error! Network Failure."))
                else -> postResponse.postValue(Resource.Error("Error! Email has already been taken."))
            }
        }
    }

    fun postuserRXJ(name: String, email: String) {
        postResponse.postValue(Resource.Loading())
        val user = Data(
            name = name,
            email = email,
            gender = getGender(),
            status = getStatus(),
            id = -1
        )
        compositeDisposable.add(mainRepository.postUserRXJ(user).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    postResponse.postValue(Resource.Success(it))
                }, { t ->
                    when (t) {
                        is IOException -> postResponse.postValue(Resource.Error("Error! Network Failure."))
                        else -> postResponse.postValue(Resource.Error("Error! ${t.message}."))
                    }
                }
            ))
    }

    private fun handleSearchNewsResponse(response: Response<PostUserResponse>): Resource<PostUserResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}