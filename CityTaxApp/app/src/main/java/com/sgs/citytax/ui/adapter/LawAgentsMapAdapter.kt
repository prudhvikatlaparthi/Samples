package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemMapLawAgentsBinding
import com.sgs.citytax.model.BusinessLocations
import com.sgs.citytax.model.LawPendingTransactionLocations

class LawAgentsMapAdapter(val items: MutableList<LawPendingTransactionLocations>, private val itemClicked: (LawPendingTransactionLocations) -> Unit) :
        RecyclerView.Adapter<LawAgentsMapAdapter.ViewHolder>() {
    private var context: Context? = null
    private var focusedItem: Int? = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_map_law_agents,
                parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], itemClicked)
        holder.itemView.isSelected = focusedItem == position
    }

    inner class ViewHolder(val binding: ItemMapLawAgentsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LawPendingTransactionLocations, lawItemClicked: (LawPendingTransactionLocations) -> Unit)
        {
            binding.lawLocationVM = item
            binding.root.setOnClickListener {
                focusedItem?.let { it1 -> notifyItemChanged(it1) }
                focusedItem = layoutPosition
                focusedItem?.let { it2 ->
                    notifyItemChanged(it2)
                }
                lawItemClicked(item)
            }
        }
    }

    fun updateList(items: List<LawPendingTransactionLocations>){
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}