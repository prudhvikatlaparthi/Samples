package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ComplaintIncidentDetailLocation
import com.sgs.citytax.databinding.ItemMapComplaintIncidentBinding
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond

class ComplaintMapAdapter(private val items: List<ComplaintIncidentDetailLocation>, private val itemClicked: (ComplaintIncidentDetailLocation) -> Unit) :
        RecyclerView.Adapter<ComplaintMapAdapter.ViewHolder>() {
    private var context: Context? = null
    private var rowIndex = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_map_complaint_incident,
                parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], itemClicked, position)
    }

    inner class ViewHolder(val binding: ItemMapComplaintIncidentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ComplaintIncidentDetailLocation, itemClicked: (ComplaintIncidentDetailLocation) -> Unit, position: Int) {
            binding.txtLabelIncidentType.text = context?.resources?.getString(R.string.complaint_category)
            binding.txtLabelIncidentDate.text = context?.resources?.getString(R.string.complaint_date)

            item.complaint?.let {
                if (it.isNotEmpty()) {
                    binding.txtIncidentType.text = it
                }
            }

            item.complaintDate?.let {
                if (it.isNotEmpty()) {
                    binding.txtDate.text = formatDisplayDateTimeInMillisecond(it)
                }
            }

            item.status?.let {
                binding.txtStatus.text = it
            }

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
        }
    }
}