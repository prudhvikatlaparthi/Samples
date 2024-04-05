package com.sgs.citytax.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.VUCRMServiceRequest
import com.sgs.citytax.databinding.ItemIncidentBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import java.util.*
import kotlin.collections.ArrayList

class IncidentAdapter(private val listener: (VUCRMServiceRequest) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var serviceRequests: ArrayList<VUCRMServiceRequest> = arrayListOf()
    private var filteredData: List<VUCRMServiceRequest> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    init {
        filteredData = serviceRequests
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_incident, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int = filteredData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val serviceRequest: VUCRMServiceRequest = filteredData[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as ViewHolder).bind(serviceRequest, listener)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(serviceRequest)
            }
        }
    }

    inner class ViewHolder(val binding: ItemIncidentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VUCRMServiceRequest, itemClicked: (VUCRMServiceRequest) -> Unit) {
            binding.tvDate.text = item.serviceRequestDate?.let { formatDisplayDateTimeInMillisecond(it) }
            binding.tvStatus.text = item.status
            item.accountName?.let {
                binding.tvCreatedBy.text = "${it}"
                binding.llCreatedBy.visibility = View.VISIBLE
            }
            binding.tvCreatedBy.text = "${item.accountName}"
            binding.tvIncidentType.text = item.incident
            binding.llPriority.visibility = View.GONE
            item.priority?.let {
                binding.tvPriority.text = it
                binding.llPriority.visibility = View.VISIBLE
            }
            item.incidentSubtype?.let {
                binding.llIncidentSubType.visibility = View.VISIBLE
                binding.tvIncidentSubType.text = it
            }
            binding.root.setOnClickListener { itemClicked(item) }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceRequest: VUCRMServiceRequest) {
            if (serviceRequest.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filteredData = if (TextUtils.isEmpty(constraint)) {
                    serviceRequests
                } else {
                    val newFilteredList: MutableList<VUCRMServiceRequest> = ArrayList()
                    for (o in serviceRequests) {
                        if (o.status.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
                                o.incident.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
                                o.incidentID.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                        ) {
                            newFilteredList.add(o)
                        }
                    }
                    newFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredData
                filterResults.count = filteredData.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredData = results.values as List<VUCRMServiceRequest>
                notifyDataSetChanged()
            }

        }
    }

    fun add(serviceRequest: VUCRMServiceRequest) {
        serviceRequests.add(serviceRequest)
        notifyDataSetChanged()
    }

    fun addAll(serviceRequests: List<VUCRMServiceRequest>) {
        for (serviceRequest in serviceRequests) {
            add(serviceRequest)
        }
    }

    fun clear() {
        serviceRequests.clear()
        notifyDataSetChanged()
    }

    fun remove(serviceRequest: VUCRMServiceRequest) {
        val position: Int = serviceRequests.indexOf(serviceRequest)
        if (position >-1) {
            serviceRequests.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (serviceRequests[position].isLoading)
            mLoading
        else mItem
    }

}