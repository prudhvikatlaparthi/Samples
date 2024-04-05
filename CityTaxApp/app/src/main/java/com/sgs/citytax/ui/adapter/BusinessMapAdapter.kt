package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemMapBusinessBinding
import com.sgs.citytax.model.BusinessLocations
import java.util.*

class BusinessMapAdapter(var items: MutableList<BusinessLocations>, private val itemClicked: (BusinessLocations) -> Unit) :
        RecyclerView.Adapter<BusinessMapAdapter.ViewHolder>() {
    private var context: Context? = null
    private var focusedItem: Int? = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_map_business,
                parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], itemClicked)
        holder.itemView.isSelected = focusedItem == position
    }

    inner class ViewHolder(val binding: ItemMapBusinessBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BusinessLocations, itemClicked: (BusinessLocations) -> Unit) {
            item.sycotaxID?.let {
                if (it.isNotEmpty()) {
                    binding.llSycoTaxID.visibility = View.VISIBLE
                    binding.txtSycoTaxID.text = item.sycotaxID
                }
            }
            item.business?.let {
                if (it.isNotEmpty()) {
                    binding.llbusinessName.visibility = View.VISIBLE
                    binding.txtBusinessName.text = item.business
                }
            }

            binding.root.setOnClickListener {
                focusedItem?.let { it1 -> notifyItemChanged(it1) }
                focusedItem = layoutPosition
                focusedItem?.let { it2 ->
                    notifyItemChanged(it2)
                }
                itemClicked(item)
            }
        }
    }

    fun updateList(items: List<BusinessLocations>){
        this.items.addAll(items)
        this.items.reverse()
        notifyDataSetChanged()
    }

    fun clearList(){
        this.items.clear()
    }

}