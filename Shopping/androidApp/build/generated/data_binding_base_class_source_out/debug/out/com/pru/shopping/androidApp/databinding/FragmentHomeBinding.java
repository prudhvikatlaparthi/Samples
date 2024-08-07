// Generated by view binder compiler. Do not edit!
package com.pru.shopping.androidApp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.pru.shopping.androidApp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentHomeBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final AppBarLayout appBar;

  @NonNull
  public final TextView errorView;

  @NonNull
  public final RelativeLayout header;

  @NonNull
  public final ImageView imageView;

  @NonNull
  public final ProgressBar pbView;

  @NonNull
  public final RecyclerView rcView;

  private FragmentHomeBinding(@NonNull CoordinatorLayout rootView, @NonNull AppBarLayout appBar,
      @NonNull TextView errorView, @NonNull RelativeLayout header, @NonNull ImageView imageView,
      @NonNull ProgressBar pbView, @NonNull RecyclerView rcView) {
    this.rootView = rootView;
    this.appBar = appBar;
    this.errorView = errorView;
    this.header = header;
    this.imageView = imageView;
    this.pbView = pbView;
    this.rcView = rcView;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.app_bar;
      AppBarLayout appBar = rootView.findViewById(id);
      if (appBar == null) {
        break missingId;
      }

      id = R.id.error_view;
      TextView errorView = rootView.findViewById(id);
      if (errorView == null) {
        break missingId;
      }

      id = R.id.header;
      RelativeLayout header = rootView.findViewById(id);
      if (header == null) {
        break missingId;
      }

      id = R.id.imageView;
      ImageView imageView = rootView.findViewById(id);
      if (imageView == null) {
        break missingId;
      }

      id = R.id.pb_view;
      ProgressBar pbView = rootView.findViewById(id);
      if (pbView == null) {
        break missingId;
      }

      id = R.id.rc_view;
      RecyclerView rcView = rootView.findViewById(id);
      if (rcView == null) {
        break missingId;
      }

      return new FragmentHomeBinding((CoordinatorLayout) rootView, appBar, errorView, header,
          imageView, pbView, rcView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
