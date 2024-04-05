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
import com.sgs.citytax.api.payload.NewServiceRequest
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.ItemServiceBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class ServiceAdapter(private val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val mBinderHelper = ViewBinderHelper()
    private var serviceRequests: ArrayList<NewServiceRequest> = arrayListOf()
    private var filteredData: List<NewServiceRequest> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    init {
        mBinderHelper.setOpenOnlyOne(true)
        filteredData = serviceRequests
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_service, parent, false))
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
        val serviceRequest: NewServiceRequest = filteredData[position]
        when (getItemViewType(position)) {
            mItem -> {
                mBinderHelper.bind((holder as ViewHolder).binding.swipeLayout, position.toString())
                mBinderHelper.closeAll()
                holder.bind(serviceRequest, listener)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(serviceRequest)
            }
        }
    }

    inner class ViewHolder(val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceRequest: NewServiceRequest, listener: IClickListener) {
            binding.tvServiceRequestNo.text = serviceRequest.serviceRequestNo
            binding.tvServiceType.text = serviceRequest.serviceType
            binding.tvServiceSubType.text = serviceRequest.serviceSubType
            binding.tvCustomerName.text = serviceRequest.customer
            binding.tvStatus.text = serviceRequest.status
           // if (serviceRequest.advanceAmount != null && serviceRequest.advanceAmount!! > BigDecimal("0"))
            binding.tvAdvAmount.text = formatWithPrecision(serviceRequest.advanceAmount.toString())
           /* else
                binding.tvAdvAmount.visibility = View.GONE*/
            binding.tvDate.text = serviceRequest.serviceRequestDate?.let { formatDisplayDateTimeInMinutes(it) }
            serviceRequest.is3rdParty?.let {
                binding.llAssignTo3rdParty.visibility = View.VISIBLE
                binding.tvAssignTo3rdParty.text = it
            }
            binding.llRootView.setOnClickListener { view: View? ->
                listener.onClick(view!!, adapterPosition, serviceRequest)
            }
            binding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                override fun onSingleClick(v: View?) {
                    listener.onClick(v!!, adapterPosition, serviceRequest)
                }
            })
           // binding.txtEdit.setOnClickListener { view: View? -> listener.onClick(view!!, adapterPosition, serviceRequest) }
        }
    }

    fun add(serviceRequest: NewServiceRequest) {
        serviceRequests.add(serviceRequest)
        notifyDataSetChanged()
    }

    fun addAll(serviceRequests: List<NewServiceRequest>) {
        for (serviceRequest in serviceRequests) {
            add(serviceRequest)
        }
    }

    fun clear() {
        serviceRequests.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (serviceRequests[position].isLoading)
            mLoading
        else mItem
    }

    fun remove(serviceRequest: NewServiceRequest?) {
        val position: Int = serviceRequests.indexOf(serviceRequest)
        serviceRequests.removeAt(position)
        notifyDataSetChanged()
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceRequest: NewServiceRequest) {
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
                    val newFilteredList: MutableList<NewServiceRequest> = ArrayList()
                    for (o in serviceRequests) {
                        if (o.status.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
                                o.customer.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
                                o.serviceRequestNo.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
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
                filteredData = results.values as List<NewServiceRequest>
                notifyDataSetChanged()
            }

        }
    }

}