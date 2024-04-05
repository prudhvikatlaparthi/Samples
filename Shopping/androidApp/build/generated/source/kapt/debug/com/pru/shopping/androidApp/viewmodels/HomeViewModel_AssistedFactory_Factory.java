package com.pru.shopping.androidApp.viewmodels;

import com.pru.shopping.shared.commonRepositories.RepositorySDK;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class HomeViewModel_AssistedFactory_Factory implements Factory<HomeViewModel_AssistedFactory> {
  private final Provider<RepositorySDK> apiRepositorySDKProvider;

  public HomeViewModel_AssistedFactory_Factory(Provider<RepositorySDK> apiRepositorySDKProvider) {
    this.apiRepositorySDKProvider = apiRepositorySDKProvider;
  }

  @Override
  public HomeViewModel_AssistedFactory get() {
    return newInstance(apiRepositorySDKProvider);
  }

  public static HomeViewModel_AssistedFactory_Factory create(
      Provider<RepositorySDK> apiRepositorySDKProvider) {
    return new HomeViewModel_AssistedFactory_Factory(apiRepositorySDKProvider);
  }

  public static HomeViewModel_AssistedFactory newInstance(
      Provider<RepositorySDK> apiRepositorySDK) {
    return new HomeViewModel_AssistedFactory(apiRepositorySDK);
  }
}
