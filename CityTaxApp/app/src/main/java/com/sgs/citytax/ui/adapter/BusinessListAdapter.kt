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
import com.sgs.citytax.databinding.ItemBusinessBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.TaxPayerDetails
import java.util.*
import kotlin.collections.ArrayList

class BusinessListAdapter(val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var filteredList: ArrayList<TaxPayerDetails> = arrayListOf()
    private var items: ArrayList<TaxPayerDetails> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    init {
        filteredList = items
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = BusinessListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_business, parent, false))
            }
            mLoading -> {
                viewHolder = BusinessListViewHolder.LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val taxPayerDetails: TaxPayerDetails = filteredList[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as BusinessListViewHolder).bind(taxPayerDetails, listener, position)
            }
            mLoading -> {
                (holder as BusinessListViewHolder.LoadingViewHolder).bind(taxPayerDetails)
            }
        }
    }

    interface Listener {
        fun onItemClick(taxPayerDetails: TaxPayerDetails, position: Int)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filteredList = (if (TextUtils.isEmpty(constraint)) {
                    items
                } else {
                    val newFilteredList: MutableList<TaxPayerDetails> = ArrayList()
                    for (o in items) {
                        if (o.sycoTaxID.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                                || o.email.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                                || o.number.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                                || o.vuCrmAccounts?.accountName.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                            newFilteredList.add(o)
                        }
                    }
                    newFilteredList
                }) as ArrayList<TaxPayerDetails>
                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredList = results.values as ArrayList<TaxPayerDetails>
                notifyDataSetChanged()
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (filteredList[position].isLoading)
            mLoading
        else mItem
    }

    fun remove(position: Int) {
        if (position > -1) {
            filteredList.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }


    fun add(taxPayerDetails: TaxPayerDetails?) {
        filteredList.add(taxPayerDetails!!)
        notifyItemInserted(filteredList.size - 1)
    }

    fun addAll(taxPayerDetails: List<TaxPayerDetails?>) {
        for (mtaxPayerDetails in taxPayerDetails) {
            add(mtaxPayerDetails)
        }
    }

    fun clear() {
        filteredList.clear()
        notifyDataSetChanged()
    }

    fun update(position: Int, status: String?) {
        filteredList.get(position).vuCrmAccounts?.status = status
        notifyDataSetChanged()
    }
}

class BusinessListViewHolder(var binding: ItemBusinessBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(taxPayerDetails: TaxPayerDetails, listener: BusinessListAdapter.Listener, position: Int) {
        binding.tvSycoTaxID.text = taxPayerDetails.sycoTaxID
        binding.tvEmail.text = taxPayerDetails.email
        binding.tvPhoneNumber.text = taxPayerDetails.number
        binding.tvBusinessName.text = taxPayerDetails.vuCrmAccounts?.accountName
        binding.tvStatus.text = taxPayerDetails.vuCrmAccounts?.status
        binding.llContainer.setOnClickListener {
            listener.onItemClick(taxPayerDetails, position)
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxPayerDetails: TaxPayerDetails) {
            if (taxPayerDetails.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }


}
