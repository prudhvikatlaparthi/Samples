package com.pru.shopping.androidApp.viewmodels;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0012\u001a\u00020\u0013J\b\u0010\u0014\u001a\u00020\u0013H\u0002J\b\u0010\u0015\u001a\u00020\u0013H\u0014R \u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00070\r8F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR#\u0010\u0010\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\b0\u00070\r8F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u000f\u00a8\u0006\u0016"}, d2 = {"Lcom/pru/shopping/androidApp/viewmodels/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "apiRepositorySDK", "Lcom/pru/shopping/shared/commonRepositories/RepositorySDK;", "(Lcom/pru/shopping/shared/commonRepositories/RepositorySDK;)V", "_rocketsState", "Landroidx/lifecycle/MutableLiveData;", "Lcom/pru/shopping/androidApp/utils/Resource;", "", "Lcom/pru/shopping/shared/commonModels/RocketLaunch;", "_todosState", "Lcom/pru/shopping/shared/commonModels/TodoItem;", "rocketState", "Landroidx/lifecycle/LiveData;", "getRocketState", "()Landroidx/lifecycle/LiveData;", "todosState", "getTodosState", "fetchData", "", "fetchTodos", "onCleared", "androidApp_debug"})
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    private final androidx.lifecycle.MutableLiveData<com.pru.shopping.androidApp.utils.Resource<java.util.List<com.pru.shopping.shared.commonModels.RocketLaunch>>> _rocketsState = null;
    private final androidx.lifecycle.MutableLiveData<com.pru.shopping.androidApp.utils.Resource<java.util.List<com.pru.shopping.shared.commonModels.TodoItem>>> _todosState = null;
    private final com.pru.shopping.shared.commonRepositories.RepositorySDK apiRepositorySDK = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.pru.shopping.androidApp.utils.Resource<java.util.List<com.pru.shopping.shared.commonModels.RocketLaunch>>> getRocketState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.pru.shopping.androidApp.utils.Resource<java.util.List<com.pru.shopping.shared.commonModels.TodoItem>>> getTodosState() {
        return null;
    }
    
    public final void fetchData() {
    }
    
    private final void fetchTodos() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
    
    @androidx.hilt.lifecycle.ViewModelInject()
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.pru.shopping.shared.commonRepositories.RepositorySDK apiRepositorySDK) {
        super();
    }
}