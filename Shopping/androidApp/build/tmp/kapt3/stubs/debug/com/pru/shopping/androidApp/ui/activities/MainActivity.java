package com.pru.shopping.androidApp.ui.activities;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u000f\u001a\u00020\u0010J\b\u0010\u0011\u001a\u00020\u0010H\u0016J\u0012\u0010\u0012\u001a\u00020\u00102\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0014J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0016H\u0016J\b\u0010\u001a\u001a\u00020\u0010H\u0002J\b\u0010\u001b\u001a\u00020\u0010H\u0002J\u0006\u0010\u001c\u001a\u00020\u0010R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/pru/shopping/androidApp/ui/activities/MainActivity;", "Lcom/pru/shopping/androidApp/ui/BaseActivity;", "()V", "TAG", "", "activityBinding", "Lcom/pru/shopping/androidApp/databinding/ActivityMainBinding;", "getActivityBinding", "()Lcom/pru/shopping/androidApp/databinding/ActivityMainBinding;", "setActivityBinding", "(Lcom/pru/shopping/androidApp/databinding/ActivityMainBinding;)V", "appBarConfiguration", "Landroidx/navigation/ui/AppBarConfiguration;", "navController", "Landroidx/navigation/NavController;", "lockNavigationDrawer", "", "onBackPressed", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onOptionsItemSelected", "", "item", "Landroid/view/MenuItem;", "onSupportNavigateUp", "setupBottomAppBarViews", "setupListeners", "unLockNavigationDrawer", "androidApp_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class MainActivity extends com.pru.shopping.androidApp.ui.BaseActivity {
    public com.pru.shopping.androidApp.databinding.ActivityMainBinding activityBinding;
    private androidx.navigation.NavController navController;
    private androidx.navigation.ui.AppBarConfiguration appBarConfiguration;
    private final java.lang.String TAG = "FireBase";
    private java.util.HashMap _$_findViewCache;
    
    @org.jetbrains.annotations.NotNull()
    public final com.pru.shopping.androidApp.databinding.ActivityMainBinding getActivityBinding() {
        return null;
    }
    
    public final void setActivityBinding(@org.jetbrains.annotations.NotNull()
    com.pru.shopping.androidApp.databinding.ActivityMainBinding p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupListeners() {
    }
    
    private final void setupBottomAppBarViews() {
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    @java.lang.Override()
    public boolean onSupportNavigateUp() {
        return false;
    }
    
    @java.lang.Override()
    public void onBackPressed() {
    }
    
    public final void lockNavigationDrawer() {
    }
    
    public final void unLockNavigationDrawer() {
    }
    
    public MainActivity() {
        super();
    }
}