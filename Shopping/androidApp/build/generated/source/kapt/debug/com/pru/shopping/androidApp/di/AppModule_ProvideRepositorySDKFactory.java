package com.pru.shopping.androidApp.di;

import com.pru.shopping.shared.commonRepositories.RepositorySDK;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class AppModule_ProvideRepositorySDKFactory implements Factory<RepositorySDK> {
  @Override
  public RepositorySDK get() {
    return provideRepositorySDK();
  }

  public static AppModule_ProvideRepositorySDKFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RepositorySDK provideRepositorySDK() {
    return Preconditions.checkNotNull(AppModule.INSTANCE.provideRepositorySDK(), "Cannot return null from a non-@Nullable @Provides method");
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideRepositorySDKFactory INSTANCE = new AppModule_ProvideRepositorySDKFactory();
  }
}
