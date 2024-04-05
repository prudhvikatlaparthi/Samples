package com.pru.navigationcomponentapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pru.navigationcomponentapp.databinding.ItemTaskBinding

class TaskListAdapter(private val dataList: MutableList<Task>) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                cardItem.setOnClickListener {
                    if (adapterPosition != -1) {
                        onCardItemListener?.let {
                            it(dataList[adapterPosition])
                        }
                    }
                }
            }
        }

        fun bindData(task: Task) = with(binding) {
            tvTaskName.text = task.taskName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemTaskBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(itemTaskBinding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = dataList[position]
        holder.bindData(task)
    }

    override fun getItemCount(): Int = dataList.size

    var onCardItemListener: ((Task) -> Unit)? = null

    fun setOnCardListener(onCardItemListener: (Task) -> Unit) {
        this.onCardItemListener = onCardItemListener
    }
}