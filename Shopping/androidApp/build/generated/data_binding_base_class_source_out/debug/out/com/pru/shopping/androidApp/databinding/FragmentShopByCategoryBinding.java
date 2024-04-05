// Generated by view binder compiler. Do not edit!
package com.pru.shopping.androidApp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.pru.shopping.androidApp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentShopByCategoryBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final ProgressBar pbView;

  @NonNull
  public final RecyclerView rcView;

  private FragmentShopByCategoryBinding(@NonNull FrameLayout rootView, @NonNull ProgressBar pbView,
      @NonNull RecyclerView rcView) {
    this.rootView = rootView;
    this.pbView = pbView;
    this.rcView = rcView;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentShopByCategoryBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentShopByCategoryBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_shop_by_category, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentShopByCategoryBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
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

      return new FragmentShopByCategoryBinding((FrameLayout) rootView, pbView, rcView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
