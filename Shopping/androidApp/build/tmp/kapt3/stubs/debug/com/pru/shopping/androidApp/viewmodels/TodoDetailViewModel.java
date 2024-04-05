package com.pru.shopping.androidApp.viewmodels;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bR\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000e8F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0014"}, d2 = {"Lcom/pru/shopping/androidApp/viewmodels/TodoDetailViewModel;", "Landroidx/lifecycle/ViewModel;", "repositorySDK", "Lcom/pru/shopping/shared/commonRepositories/RepositorySDK;", "savedStateHandle", "Landroidx/lifecycle/SavedStateHandle;", "(Lcom/pru/shopping/shared/commonRepositories/RepositorySDK;Landroidx/lifecycle/SavedStateHandle;)V", "TAG", "", "_todoDetail", "Landroidx/lifecycle/MutableLiveData;", "Lcom/pru/shopping/androidApp/utils/Resource;", "Lcom/pru/shopping/shared/commonModels/UserResponse;", "todoDetail", "Landroidx/lifecycle/LiveData;", "getTodoDetail", "()Landroidx/lifecycle/LiveData;", "postUser", "Lkotlinx/coroutines/Job;", "etEmail", "androidApp_debug"})
public final class TodoDetailViewModel extends androidx.lifecycle.ViewModel {
    private final java.lang.String TAG = "TodoDetailViewModel";
    private final androidx.lifecycle.MutableLiveData<com.pru.shopping.androidApp.utils.Resource<com.pru.shopping.shared.commonModels.UserResponse>> _todoDetail = null;
    private final com.pru.shopping.shared.commonRepositories.RepositorySDK repositorySDK = null;
    private final androidx.lifecycle.SavedStateHandle savedStateHandle = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.pru.shopping.androidApp.utils.Resource<com.pru.shopping.shared.commonModels.UserResponse>> getTodoDetail() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job postUser(@org.jetbrains.annotations.NotNull()
    java.lang.String etEmail) {
        return null;
    }
    
    @androidx.hilt.lifecycle.ViewModelInject()
    public TodoDetailViewModel(@org.jetbrains.annotations.NotNull()
    com.pru.shopping.shared.commonRepositories.RepositorySDK repositorySDK, @org.jetbrains.annotations.NotNull()
    @androidx.hilt.Assisted()
    androidx.lifecycle.SavedStateHandle savedStateHandle) {
        super();
    }
}