package com.pru.shopping.androidApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pru.shopping.androidApp.databinding.ItemTodoLayoutBinding
import com.pru.shopping.shared.commonModels.TodoItem
import java.lang.StringBuilder

class TodoAdapter( var list: List<TodoItem>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    inner class TodoViewHolder(private val itemBinding: ItemTodoLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        init {
            with(itemBinding) {
                cvItem.setOnClickListener {
                    if(adapterPosition != -1){
                        val data = list[adapterPosition]
                        listener?.let {
                            it(data)
                        }
                    }
                }
            }
        }

        fun bindData(todoItem: TodoItem) = with(itemBinding) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(todoItem.id.toString() +". "+todoItem.title)
            tvTodoName.text = stringBuilder.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemTodoLayoutBinding = ItemTodoLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return TodoViewHolder(itemTodoLayoutBinding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun updateData(list: List<TodoItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    private var listener: ((todoItem : TodoItem) -> Unit)? = null
    fun setItemClickListener(listener: (todoItem : TodoItem) -> Unit) {
        this.listener = listener
    }
}