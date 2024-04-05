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
public final class TodoDetailViewModel_AssistedFactory_Factory implements Factory<TodoDetailViewModel_AssistedFactory> {
  private final Provider<RepositorySDK> repositorySDKProvider;

  public TodoDetailViewModel_AssistedFactory_Factory(
      Provider<RepositorySDK> repositorySDKProvider) {
    this.repositorySDKProvider = repositorySDKProvider;
  }

  @Override
  public TodoDetailViewModel_AssistedFactory get() {
    return newInstance(repositorySDKProvider);
  }

  public static TodoDetailViewModel_AssistedFactory_Factory create(
      Provider<RepositorySDK> repositorySDKProvider) {
    return new TodoDetailViewModel_AssistedFactory_Factory(repositorySDKProvider);
  }

  public static TodoDetailViewModel_AssistedFactory newInstance(
      Provider<RepositorySDK> repositorySDK) {
    return new TodoDetailViewModel_AssistedFactory(repositorySDK);
  }
}
