package com.pru.hiltarchi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pru.hiltarchi.databinding.ItemTodoLayoutBinding
import com.pru.hiltarchi.models.TodoItem

class TodoAdapter(private val listener: ((position: Int) -> Unit)? = null) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    inner class TodoViewHolder(private val itemBinding: ItemTodoLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        init {
            with(itemBinding) {
                cvItem.setOnClickListener {
                    if (adapterPosition != -1) {
                        listener?.let {
                            it(adapterPosition)
                        }
                    }
                }
            }
        }

        fun bindData(todoItem: TodoItem) = with(itemBinding) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(todoItem.id.toString() + ". " + todoItem.title)
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
        holder.bindData(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}