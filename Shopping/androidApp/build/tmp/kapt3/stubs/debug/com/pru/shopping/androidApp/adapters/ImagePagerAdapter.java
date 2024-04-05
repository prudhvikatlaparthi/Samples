package com.pru.shopping.androidApp.adapters;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u001bB\u0015\u0012\u000e\u0010\u0003\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\u001c\u0010\u0012\u001a\u00020\u000f2\n\u0010\u0013\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0014\u001a\u00020\u0011H\u0016J\u001c\u0010\u0015\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0011H\u0016J)\u0010\u0019\u001a\u00020\u000f2!\u0010\n\u001a\u001d\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f0\u000bJ\u0016\u0010\u001a\u001a\u00020\u000f2\u000e\u0010\u0003\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004R\"\u0010\u0003\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\u0006R+\u0010\n\u001a\u001f\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/pru/shopping/androidApp/adapters/ImagePagerAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/pru/shopping/androidApp/adapters/ImagePagerAdapter$MyViewHolder;", "list", "", "", "(Ljava/util/List;)V", "getList", "()Ljava/util/List;", "setList", "listener", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "name", "item", "", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setItemClickListener", "updateData", "MyViewHolder", "androidApp_debug"})
public final class ImagePagerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.pru.shopping.androidApp.adapters.ImagePagerAdapter.MyViewHolder> {
    private kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> listener;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<java.lang.String> list;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.pru.shopping.androidApp.adapters.ImagePagerAdapter.MyViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.pru.shopping.androidApp.adapters.ImagePagerAdapter.MyViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void updateData(@org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> list) {
    }
    
    public final void setItemClickListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> listener) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> getList() {
        return null;
    }
    
    public final void setList(@org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> p0) {
    }
    
    public ImagePagerAdapter(@org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> list) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J*\u0010\u0005\u001a\u001e\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u0007\u0012\f\u0012\n \b*\u0004\u0018\u00010\t0\t0\u00062\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/pru/shopping/androidApp/adapters/ImagePagerAdapter$MyViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemBinding", "Lcom/pru/shopping/androidApp/databinding/LayoutImagePagerViewBinding;", "(Lcom/pru/shopping/androidApp/adapters/ImagePagerAdapter;Lcom/pru/shopping/androidApp/databinding/LayoutImagePagerViewBinding;)V", "bindData", "Lcom/bumptech/glide/request/target/ViewTarget;", "Landroid/widget/ImageView;", "kotlin.jvm.PlatformType", "Landroid/graphics/drawable/Drawable;", "todoItem", "", "androidApp_debug"})
    public final class MyViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final com.pru.shopping.androidApp.databinding.LayoutImagePagerViewBinding itemBinding = null;
        
        @org.jetbrains.annotations.NotNull()
        public final com.bumptech.glide.request.target.ViewTarget<android.widget.ImageView, android.graphics.drawable.Drawable> bindData(@org.jetbrains.annotations.NotNull()
        java.lang.String todoItem) {
            return null;
        }
        
        public MyViewHolder(@org.jetbrains.annotations.NotNull()
        com.pru.shopping.androidApp.databinding.LayoutImagePagerViewBinding itemBinding) {
            super(null);
        }
    }
}