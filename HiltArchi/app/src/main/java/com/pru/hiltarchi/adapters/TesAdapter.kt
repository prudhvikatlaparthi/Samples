package com.pru.hiltarchi.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.pru.hiltarchi.databinding.ItemTodoLayoutBinding
import com.pru.hiltarchi.models.TodoItem

class TesAdapter(private val listener: ((position: Int) -> Unit)? = null) :
    RecyclerView.Adapter<TesAdapter.TesViewHolder>() {

    inner class TesViewHolder
    constructor(
        binding: ItemTodoLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TodoItem) = with(itemView) {
            itemView.setOnClickListener {
                if (adapterPosition != -1) {
                    listener?.let {
                        it(adapterPosition)
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TesViewHolder {
        val binding = ItemTodoLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return TesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TesViewHolder, position: Int) {
        holder.bind(differ.currentList.get(position))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<TodoItem>) {
        differ.submitList(list)
    }

    private val differCallback = object : DiffUtil.ItemCallback<TodoItem>() {

        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            TODO("not implemented")
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            TODO("not implemented")
        }

    }
    private val differ = AsyncListDiffer(this, differCallback)

}