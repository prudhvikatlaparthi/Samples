package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ComplaintIncidentDetailLocation
import com.sgs.citytax.api.response.PropertyLocations
import com.sgs.citytax.databinding.ItemMapComplaintIncidentBinding
import com.sgs.citytax.databinding.ItemMapPropertyDetailsBinding
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond

class PropertyMapAdapter(private val items: List<PropertyDetailLocation>, private val itemClicked: (PropertyDetailLocation) -> Unit) :
        RecyclerView.Adapter<PropertyMapAdapter.ViewHolder>() {
    private var context: Context? = null
    private var rowIndex = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_map_property_details,
                parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], itemClicked, position)
    }

    inner class ViewHolder(val binding: ItemMapPropertyDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PropertyDetailLocation, itemClicked: (PropertyDetailLocation) -> Unit, position: Int) {
                binding.propertyDetailsVM = item
            if (rowIndex == position) {
                context?.let { binding.root.setBackgroundColor(ContextCompat.getColor(it, R.color.colorGray)) }
            } else {
                context?.let { binding.root.setBackgroundColor(ContextCompat.getColor(it, R.color.colorWhite)) }
            }

            binding.root.setOnClickListener {
                rowIndex = position
                notifyDataSetChanged()
                itemClicked(item)
            }
            binding.executePendingBindings()
        }
    }
}