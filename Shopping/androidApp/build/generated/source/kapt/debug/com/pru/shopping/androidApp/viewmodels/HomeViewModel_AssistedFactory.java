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
public final class HomeViewModel_AssistedFactory implements ViewModelAssistedFactory<HomeViewModel> {
  private final Provider<RepositorySDK> apiRepositorySDK;

  @Inject
  HomeViewModel_AssistedFactory(Provider<RepositorySDK> apiRepositorySDK) {
    this.apiRepositorySDK = apiRepositorySDK;
  }

  @Override
  @NonNull
  public HomeViewModel create(SavedStateHandle arg0) {
    return new HomeViewModel(apiRepositorySDK.get());
  }
}
