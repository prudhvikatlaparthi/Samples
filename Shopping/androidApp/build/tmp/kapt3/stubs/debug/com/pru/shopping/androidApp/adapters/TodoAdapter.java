package com.pru.shopping.androidApp.adapters;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u001bB\u0013\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\u001c\u0010\u0012\u001a\u00020\u000f2\n\u0010\u0013\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0014\u001a\u00020\u0011H\u0016J\u001c\u0010\u0015\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0011H\u0016J)\u0010\u0019\u001a\u00020\u000f2!\u0010\n\u001a\u001d\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f0\u000bJ\u0014\u0010\u001a\u001a\u00020\u000f2\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004R \u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\u0006R+\u0010\n\u001a\u001f\u0012\u0013\u0012\u00110\u0005\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/pru/shopping/androidApp/adapters/TodoAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/pru/shopping/androidApp/adapters/TodoAdapter$TodoViewHolder;", "list", "", "Lcom/pru/shopping/shared/commonModels/TodoItem;", "(Ljava/util/List;)V", "getList", "()Ljava/util/List;", "setList", "listener", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "name", "todoItem", "", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setItemClickListener", "updateData", "TodoViewHolder", "androidApp_debug"})
public final class TodoAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.pru.shopping.androidApp.adapters.TodoAdapter.TodoViewHolder> {
    private kotlin.jvm.functions.Function1<? super com.pru.shopping.shared.commonModels.TodoItem, kotlin.Unit> listener;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.pru.shopping.shared.commonModels.TodoItem> list;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.pru.shopping.androidApp.adapters.TodoAdapter.TodoViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.pru.shopping.androidApp.adapters.TodoAdapter.TodoViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void updateData(@org.jetbrains.annotations.NotNull()
    java.util.List<com.pru.shopping.shared.commonModels.TodoItem> list) {
    }
    
    public final void setItemClickListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.pru.shopping.shared.commonModels.TodoItem, kotlin.Unit> listener) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.pru.shopping.shared.commonModels.TodoItem> getList() {
        return null;
    }
    
    public final void setList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.pru.shopping.shared.commonModels.TodoItem> p0) {
    }
    
    public TodoAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.pru.shopping.shared.commonModels.TodoItem> list) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 1}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/pru/shopping/androidApp/adapters/TodoAdapter$TodoViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemBinding", "Lcom/pru/shopping/androidApp/databinding/ItemTodoLayoutBinding;", "(Lcom/pru/shopping/androidApp/adapters/TodoAdapter;Lcom/pru/shopping/androidApp/databinding/ItemTodoLayoutBinding;)V", "bindData", "", "todoItem", "Lcom/pru/shopping/shared/commonModels/TodoItem;", "androidApp_debug"})
    public final class TodoViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final com.pru.shopping.androidApp.databinding.ItemTodoLayoutBinding itemBinding = null;
        
        public final void bindData(@org.jetbrains.annotations.NotNull()
        com.pru.shopping.shared.commonModels.TodoItem todoItem) {
        }
        
        public TodoViewHolder(@org.jetbrains.annotations.NotNull()
        com.pru.shopping.androidApp.databinding.ItemTodoLayoutBinding itemBinding) {
            super(null);
        }
    }
}