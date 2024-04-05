// Generated by view binder compiler. Do not edit!
package com.pru.shopping.androidApp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.pru.shopping.androidApp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentImageViewPagerBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final ProgressBar imageProgress;

  @NonNull
  public final TabLayout tabLayout;

  @NonNull
  public final ViewPager2 viewPager2;

  private FragmentImageViewPagerBinding(@NonNull FrameLayout rootView,
      @NonNull ProgressBar imageProgress, @NonNull TabLayout tabLayout,
      @NonNull ViewPager2 viewPager2) {
    this.rootView = rootView;
    this.imageProgress = imageProgress;
    this.tabLayout = tabLayout;
    this.viewPager2 = viewPager2;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentImageViewPagerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentImageViewPagerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_image_view_pager, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentImageViewPagerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.image_progress;
      ProgressBar imageProgress = rootView.findViewById(id);
      if (imageProgress == null) {
        break missingId;
      }

      id = R.id.tabLayout;
      TabLayout tabLayout = rootView.findViewById(id);
      if (tabLayout == null) {
        break missingId;
      }

      id = R.id.viewPager2;
      ViewPager2 viewPager2 = rootView.findViewById(id);
      if (viewPager2 == null) {
        break missingId;
      }

      return new FragmentImageViewPagerBinding((FrameLayout) rootView, imageProgress, tabLayout,
          viewPager2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
