package com.pru.responsiveapp.ui.alpha

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.pru.responsiveapp.data.models.DataItem

class DashBoardListAdapter(private val listener: ((position: Int) -> Unit)? = null) :
    RecyclerView.Adapter<DashBoardListAdapter.DashBoardViewModel>() {

    inner class DashBoardViewModel
    constructor(
        private val binding: com.pru.responsiveapp.databinding.LayoutListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem) = with(binding) {
            itemView.setOnClickListener {
                if (adapterPosition != -1) {
                    listener?.let {
                        it(adapterPosition)
                    }
                }
            }
            tvName.text = item.title
            tvDescrip.text = item.description
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewModel {
        val binding = com.pru.responsiveapp.databinding.LayoutListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return DashBoardViewModel(binding)
    }

    override fun onBindViewHolder(holder: DashBoardViewModel, position: Int) {
        holder.bind(differ.currentList.get(position))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: MutableList<DataItem>) {
        differ.submitList(list.toList())
    }

    private val differCallback = object : DiffUtil.ItemCallback<DataItem>() {

        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, differCallback)

}