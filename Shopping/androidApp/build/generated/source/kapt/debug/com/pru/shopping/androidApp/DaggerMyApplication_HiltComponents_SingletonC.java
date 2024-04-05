package com.pru.shopping.androidApp;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.lifecycle.ViewModelAssistedFactory;
import androidx.hilt.lifecycle.ViewModelFactoryModules_ActivityModule_ProvideFactoryFactory;
import androidx.hilt.lifecycle.ViewModelFactoryModules_FragmentModule_ProvideFactoryFactory;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.pru.shopping.androidApp.di.AppModule;
import com.pru.shopping.androidApp.di.AppModule_ProvideRepositorySDKFactory;
import com.pru.shopping.androidApp.ui.activities.MainActivity;
import com.pru.shopping.androidApp.ui.fragments.HomeFragment;
import com.pru.shopping.androidApp.ui.fragments.ShopByCategoryFragment;
import com.pru.shopping.androidApp.ui.fragments.TodoDetailFragment;
import com.pru.shopping.androidApp.viewmodels.HomeViewModel_AssistedFactory;
import com.pru.shopping.androidApp.viewmodels.HomeViewModel_AssistedFactory_Factory;
import com.pru.shopping.androidApp.viewmodels.ShopByCategoryViewModel_AssistedFactory;
import com.pru.shopping.androidApp.viewmodels.ShopByCategoryViewModel_AssistedFactory_Factory;
import com.pru.shopping.androidApp.viewmodels.TodoDetailViewModel_AssistedFactory;
import com.pru.shopping.androidApp.viewmodels.TodoDetailViewModel_AssistedFactory_Factory;
import com.pru.shopping.shared.commonRepositories.RepositorySDK;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideApplicationFactory;
import dagger.internal.DoubleCheck;
import dagger.internal.MapBuilder;
import dagger.internal.MemoizedSentinel;
import dagger.internal.Preconditions;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
public final class DaggerMyApplication_HiltComponents_SingletonC extends MyApplication_HiltComponents.SingletonC {
  private final ApplicationContextModule applicationContextModule;

  private volatile Object repositorySDK = new MemoizedSentinel();

  private volatile Provider<RepositorySDK> provideRepositorySDKProvider;

  private DaggerMyApplication_HiltComponents_SingletonC(
      ApplicationContextModule applicationContextModuleParam) {
    this.applicationContextModule = applicationContextModuleParam;
  }

  public static Builder builder() {
    return new Builder();
  }

  private RepositorySDK getRepositorySDK() {
    Object local = repositorySDK;
    if (local instanceof MemoizedSentinel) {
      synchronized (local) {
        local = repositorySDK;
        if (local instanceof MemoizedSentinel) {
          local = AppModule_ProvideRepositorySDKFactory.provideRepositorySDK();
          repositorySDK = DoubleCheck.reentrantCheck(repositorySDK, local);
        }
      }
    }
    return (RepositorySDK) local;
  }

  private Provider<RepositorySDK> getRepositorySDKProvider() {
    Object local = provideRepositorySDKProvider;
    if (local == null) {
      local = new SwitchingProvider<>(0);
      provideRepositorySDKProvider = (Provider<RepositorySDK>) local;
    }
    return (Provider<RepositorySDK>) local;
  }

  @Override
  public void injectMyApplication(MyApplication myApplication) {
  }

  @Override
  public ActivityRetainedComponentBuilder retainedComponentBuilder() {
    return new ActivityRetainedCBuilder();
  }

  @Override
  public ServiceComponentBuilder serviceComponentBuilder() {
    return new ServiceCBuilder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder appModule(AppModule appModule) {
      Preconditions.checkNotNull(appModule);
      return this;
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MyApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new DaggerMyApplication_HiltComponents_SingletonC(applicationContextModule);
    }
  }

  private final class ActivityRetainedCBuilder implements MyApplication_HiltComponents.ActivityRetainedC.Builder {
    @Override
    public MyApplication_HiltComponents.ActivityRetainedC build() {
      return new ActivityRetainedCImpl();
    }
  }

  private final class ActivityRetainedCImpl extends MyApplication_HiltComponents.ActivityRetainedC {
    private ActivityRetainedCImpl() {

    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder();
    }

    private final class ActivityCBuilder implements MyApplication_HiltComponents.ActivityC.Builder {
      private Activity activity;

      @Override
      public ActivityCBuilder activity(Activity activity) {
        this.activity = Preconditions.checkNotNull(activity);
        return this;
      }

      @Override
      public MyApplication_HiltComponents.ActivityC build() {
        Preconditions.checkBuilderRequirement(activity, Activity.class);
        return new ActivityCImpl(activity);
      }
    }

    private final class ActivityCImpl extends MyApplication_HiltComponents.ActivityC {
      private final Activity activity;

      private volatile Provider<HomeViewModel_AssistedFactory> homeViewModel_AssistedFactoryProvider;

      private volatile Provider<ShopByCategoryViewModel_AssistedFactory> shopByCategoryViewModel_AssistedFactoryProvider;

      private volatile Provider<TodoDetailViewModel_AssistedFactory> todoDetailViewModel_AssistedFactoryProvider;

      private ActivityCImpl(Activity activityParam) {
        this.activity = activityParam;
      }

      private HomeViewModel_AssistedFactory getHomeViewModel_AssistedFactory() {
        return HomeViewModel_AssistedFactory_Factory.newInstance(DaggerMyApplication_HiltComponents_SingletonC.this.getRepositorySDKProvider());
      }

      private Provider<HomeViewModel_AssistedFactory> getHomeViewModel_AssistedFactoryProvider() {
        Object local = homeViewModel_AssistedFactoryProvider;
        if (local == null) {
          local = new SwitchingProvider<>(0);
          homeViewModel_AssistedFactoryProvider = (Provider<HomeViewModel_AssistedFactory>) local;
        }
        return (Provider<HomeViewModel_AssistedFactory>) local;
      }

      private ShopByCategoryViewModel_AssistedFactory getShopByCategoryViewModel_AssistedFactory() {
        return ShopByCategoryViewModel_AssistedFactory_Factory.newInstance(DaggerMyApplication_HiltComponents_SingletonC.this.getRepositorySDKProvider());
      }

      private Provider<ShopByCategoryViewModel_AssistedFactory> getShopByCategoryViewModel_AssistedFactoryProvider(
          ) {
        Object local = shopByCategoryViewModel_AssistedFactoryProvider;
        if (local == null) {
          local = new SwitchingProvider<>(1);
          shopByCategoryViewModel_AssistedFactoryProvider = (Provider<ShopByCategoryViewModel_AssistedFactory>) local;
        }
        return (Provider<ShopByCategoryViewModel_AssistedFactory>) local;
      }

      private TodoDetailViewModel_AssistedFactory getTodoDetailViewModel_AssistedFactory() {
        return TodoDetailViewModel_AssistedFactory_Factory.newInstance(DaggerMyApplication_HiltComponents_SingletonC.this.getRepositorySDKProvider());
      }

      private Provider<TodoDetailViewModel_AssistedFactory> getTodoDetailViewModel_AssistedFactoryProvider(
          ) {
        Object local = todoDetailViewModel_AssistedFactoryProvider;
        if (local == null) {
          local = new SwitchingProvider<>(2);
          todoDetailViewModel_AssistedFactoryProvider = (Provider<TodoDetailViewModel_AssistedFactory>) local;
        }
        return (Provider<TodoDetailViewModel_AssistedFactory>) local;
      }

      private Map<String, Provider<ViewModelAssistedFactory<? extends ViewModel>>> getMapOfStringAndProviderOfViewModelAssistedFactoryOf(
          ) {
        return MapBuilder.<String, Provider<ViewModelAssistedFactory<? extends ViewModel>>>newMapBuilder(3).put("com.pru.shopping.androidApp.viewmodels.HomeViewModel", (Provider) getHomeViewModel_AssistedFactoryProvider()).put("com.pru.shopping.androidApp.viewmodels.ShopByCategoryViewModel", (Provider) getShopByCategoryViewModel_AssistedFactoryProvider()).put("com.pru.shopping.androidApp.viewmodels.TodoDetailViewModel", (Provider) getTodoDetailViewModel_AssistedFactoryProvider()).build();
      }

      private ViewModelProvider.Factory getProvideFactory() {
        return ViewModelFactoryModules_ActivityModule_ProvideFactoryFactory.provideFactory(activity, ApplicationContextModule_ProvideApplicationFactory.provideApplication(DaggerMyApplication_HiltComponents_SingletonC.this.applicationContextModule), getMapOfStringAndProviderOfViewModelAssistedFactoryOf());
      }

      @Override
      public void injectMainActivity(MainActivity arg0) {
      }

      @Override
      public Set<ViewModelProvider.Factory> getActivityViewModelFactory() {
        return Collections.<ViewModelProvider.Factory>singleton(getProvideFactory());
      }

      @Override
      public FragmentComponentBuilder fragmentComponentBuilder() {
        return new FragmentCBuilder();
      }

      @Override
      public ViewComponentBuilder viewComponentBuilder() {
        return new ViewCBuilder();
      }

      private final class FragmentCBuilder implements MyApplication_HiltComponents.FragmentC.Builder {
        private Fragment fragment;

        @Override
        public FragmentCBuilder fragment(Fragment fragment) {
          this.fragment = Preconditions.checkNotNull(fragment);
          return this;
        }

        @Override
        public MyApplication_HiltComponents.FragmentC build() {
          Preconditions.checkBuilderRequirement(fragment, Fragment.class);
          return new FragmentCImpl(fragment);
        }
      }

      private final class FragmentCImpl extends MyApplication_HiltComponents.FragmentC {
        private final Fragment fragment;

        private FragmentCImpl(Fragment fragmentParam) {
          this.fragment = fragmentParam;
        }

        private ViewModelProvider.Factory getProvideFactory() {
          return ViewModelFactoryModules_FragmentModule_ProvideFactoryFactory.provideFactory(fragment, ApplicationContextModule_ProvideApplicationFactory.provideApplication(DaggerMyApplication_HiltComponents_SingletonC.this.applicationContextModule), ActivityCImpl.this.getMapOfStringAndProviderOfViewModelAssistedFactoryOf());
        }

        @Override
        public void injectHomeFragment(HomeFragment arg0) {
        }

        @Override
        public void injectShopByCategoryFragment(ShopByCategoryFragment arg0) {
        }

        @Override
        public void injectTodoDetailFragment(TodoDetailFragment arg0) {
        }

        @Override
        public Set<ViewModelProvider.Factory> getFragmentViewModelFactory() {
          return Collections.<ViewModelProvider.Factory>singleton(getProvideFactory());
        }

        @Override
        public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
          return new ViewWithFragmentCBuilder();
        }

        private final class ViewWithFragmentCBuilder implements MyApplication_HiltComponents.ViewWithFragmentC.Builder {
          private View view;

          @Override
          public ViewWithFragmentCBuilder view(View view) {
            this.view = Preconditions.checkNotNull(view);
            return this;
          }

          @Override
          public MyApplication_HiltComponents.ViewWithFragmentC build() {
            Preconditions.checkBuilderRequirement(view, View.class);
            return new ViewWithFragmentCImpl(view);
          }
        }

        private final class ViewWithFragmentCImpl extends MyApplication_HiltComponents.ViewWithFragmentC {
          private ViewWithFragmentCImpl(View view) {

          }
        }
      }

      private final class ViewCBuilder implements MyApplication_HiltComponents.ViewC.Builder {
        private View view;

        @Override
        public ViewCBuilder view(View view) {
          this.view = Preconditions.checkNotNull(view);
          return this;
        }

        @Override
        public MyApplication_HiltComponents.ViewC build() {
          Preconditions.checkBuilderRequirement(view, View.class);
          return new ViewCImpl(view);
        }
      }

      private final class ViewCImpl extends MyApplication_HiltComponents.ViewC {
        private ViewCImpl(View view) {

        }
      }

      private final class SwitchingProvider<T> implements Provider<T> {
        private final int id;

        SwitchingProvider(int id) {
          this.id = id;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T get() {
          switch (id) {
            case 0: // com.pru.shopping.androidApp.viewmodels.HomeViewModel_AssistedFactory 
            return (T) ActivityCImpl.this.getHomeViewModel_AssistedFactory();

            case 1: // com.pru.shopping.androidApp.viewmodels.ShopByCategoryViewModel_AssistedFactory 
            return (T) ActivityCImpl.this.getShopByCategoryViewModel_AssistedFactory();

            case 2: // com.pru.shopping.androidApp.viewmodels.TodoDetailViewModel_AssistedFactory 
            return (T) ActivityCImpl.this.getTodoDetailViewModel_AssistedFactory();

            default: throw new AssertionError(id);
          }
        }
      }
    }
  }

  private final class ServiceCBuilder implements MyApplication_HiltComponents.ServiceC.Builder {
    private Service service;

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(service);
    }
  }

  private final class ServiceCImpl extends MyApplication_HiltComponents.ServiceC {
    private ServiceCImpl(Service service) {

    }
  }

  private final class SwitchingProvider<T> implements Provider<T> {
    private final int id;

    SwitchingProvider(int id) {
      this.id = id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
      switch (id) {
        case 0: // com.pru.shopping.shared.commonRepositories.RepositorySDK 
        return (T) DaggerMyApplication_HiltComponents_SingletonC.this.getRepositorySDK();

        default: throw new AssertionError(id);
      }
    }
  }
}
