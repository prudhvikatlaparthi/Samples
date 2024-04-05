package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemDashboardBinding
import com.sgs.citytax.model.QuickMenuItem
import com.sgs.citytax.util.IClickListener

class DashboardAdapter(private var quickMenuItems: List<QuickMenuItem>, private val listener: IClickListener? = null) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_dashboard, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(quickMenuItems[position], listener)
    }

    override fun getItemCount(): Int {
        return quickMenuItems.size
    }

    class ViewHolder(var binding: ItemDashboardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quickMenu: QuickMenuItem, listener: IClickListener?) {
            binding.ivIcon.setImageResource(quickMenu.resourceID)
            binding.tvActionName.text = quickMenu.name
            if (quickMenu.count != 0) {
                binding.tvCount.visibility = View.VISIBLE
                binding.tvCount.text = quickMenu.count.toString()
            }
            binding.container.setOnClickListener { listener?.onClick(it, adapterPosition, quickMenu) }
        }
    }
}