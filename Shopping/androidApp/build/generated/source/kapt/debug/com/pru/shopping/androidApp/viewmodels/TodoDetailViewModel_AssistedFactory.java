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
public final class TodoDetailViewModel_AssistedFactory implements ViewModelAssistedFactory<TodoDetailViewModel> {
  private final Provider<RepositorySDK> repositorySDK;

  @Inject
  TodoDetailViewModel_AssistedFactory(Provider<RepositorySDK> repositorySDK) {
    this.repositorySDK = repositorySDK;
  }

  @Override
  @NonNull
  public TodoDetailViewModel create(SavedStateHandle arg0) {
    return new TodoDetailViewModel(repositorySDK.get(), arg0);
  }
}
