package com.pru.shopping.androidApp.viewmodels;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelAssistedFactory;
import androidx.lifecycle.SavedStateHandle;
import com.pru.shopping.shared.commonRepositories.RepositorySDK;
import java.lang.Override;
import javax.annotation.Generated;
import javax.inject.Inject;
import javax.inject.Provider;

@Generated("androidx.hilt.AndroidXHiltProcessor")
public final class ShopByCategoryViewModel_AssistedFactory implements ViewModelAssistedFactory<ShopByCategoryViewModel> {
  private final Provider<RepositorySDK> repositorySDK;

  @Inject
  ShopByCategoryViewModel_AssistedFactory(Provider<RepositorySDK> repositorySDK) {
    this.repositorySDK = repositorySDK;
  }

  @Override
  @NonNull
  public ShopByCategoryViewModel create(SavedStateHandle arg0) {
    return new ShopByCategoryViewModel(repositorySDK.get());
  }
}
