package com.pru.shopping.androidApp.viewmodels;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000e\u001a\u00020\u000fH\u0002R \u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00070\u000b8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/pru/shopping/androidApp/viewmodels/ShopByCategoryViewModel;", "Landroidx/lifecycle/ViewModel;", "repositorySDK", "Lcom/pru/shopping/shared/commonRepositories/RepositorySDK;", "(Lcom/pru/shopping/shared/commonRepositories/RepositorySDK;)V", "_categoryItems", "Landroidx/lifecycle/MutableLiveData;", "Lcom/pru/shopping/androidApp/utils/Resource;", "", "", "categoryItems", "Landroidx/lifecycle/LiveData;", "getCategoryItems", "()Landroidx/lifecycle/LiveData;", "fetchCategories", "", "androidApp_debug"})
public final class ShopByCategoryViewModel extends androidx.lifecycle.ViewModel {
    private final androidx.lifecycle.MutableLiveData<com.pru.shopping.androidApp.utils.Resource<java.util.List<java.lang.String>>> _categoryItems = null;
    private final com.pru.shopping.shared.commonRepositories.RepositorySDK repositorySDK = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.pru.shopping.androidApp.utils.Resource<java.util.List<java.lang.String>>> getCategoryItems() {
        return null;
    }
    
    private final void fetchCategories() {
    }
    
    @androidx.hilt.lifecycle.ViewModelInject()
    public ShopByCategoryViewModel(@org.jetbrains.annotations.NotNull()
    com.pru.shopping.shared.commonRepositories.RepositorySDK repositorySDK) {
        super();
    }
}