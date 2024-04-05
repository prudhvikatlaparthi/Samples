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
public final class ShopByCategoryViewModel_AssistedFactory_Factory implements Factory<ShopByCategoryViewModel_AssistedFactory> {
  private final Provider<RepositorySDK> repositorySDKProvider;

  public ShopByCategoryViewModel_AssistedFactory_Factory(
      Provider<RepositorySDK> repositorySDKProvider) {
    this.repositorySDKProvider = repositorySDKProvider;
  }

  @Override
  public ShopByCategoryViewModel_AssistedFactory get() {
    return newInstance(repositorySDKProvider);
  }

  public static ShopByCategoryViewModel_AssistedFactory_Factory create(
      Provider<RepositorySDK> repositorySDKProvider) {
    return new ShopByCategoryViewModel_AssistedFactory_Factory(repositorySDKProvider);
  }

  public static ShopByCategoryViewModel_AssistedFactory newInstance(
      Provider<RepositorySDK> repositorySDK) {
    return new ShopByCategoryViewModel_AssistedFactory(repositorySDK);
  }
}
