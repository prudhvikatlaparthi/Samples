package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CRMAgentTransactionDetail
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemCollectionHistroyBinding
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class AgentCollectionHistoryAdapter() : RecyclerView.Adapter<AgentCollectionHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_collection_histroy,
                parent, false))
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    inner class ViewHolder(val binding: ItemCollectionHistroyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CRMAgentTransactionDetail) {
            binding.llAgentType.visibility = GONE
            binding.llCollectedBy.visibility = GONE

            if (!item.chequeStatus.isNullOrEmpty()) {
                binding.llStatus.visibility = VISIBLE
                binding.tvStatus.text = item.chequeStatus
            } else {
                binding.llStatus.visibility = GONE
                binding.tvStatus.text = ""
            }

            if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.PPS.name) {
                binding.llAgentType.visibility = VISIBLE
                binding.tvPPSUserType.text = if (item.isAdminUser?.equals("Y") == true)
                    binding.tvPPSUserType.context.getString(R.string.admin)
                else binding.tvPPSUserType.context.getString(R.string.cashier)

                if (!item.receivedBy.isNullOrEmpty()) {
                    binding.llCollectedBy.visibility = VISIBLE
                    binding.tvCollectedBy.text = item.receivedBy
                }

            } else {
                binding.tvCollectedBy.text = ""
                binding.tvPPSUserType.text = ""
                binding.llAgentType.visibility = GONE
            }
            binding.tvDate.text = formatDisplayDateTimeInMillisecond(item.date)
            binding.tvPaymentMode.text = item.paymentMode
            if (item.accountTypeCode == Constant.AccountTypeCode.CRO.name)
                binding.tvBusinessNameLabel.text = binding.tvBusinessNameLabel.context.resources.getString(R.string.business_name)
            else binding.tvBusinessNameLabel.text = binding.tvBusinessNameLabel.context.resources.getString(R.string.customer_name)
            binding.tvBusiness.text = item.customerName
            binding.tvAmount.text = formatWithPrecision(item.amount)
            binding.llTaxName.visibility =
                item.taxPayer?.let {
                    binding.tvTaxName.text = item.product
                    VISIBLE
                } ?: GONE
            if (item.productTypeCode == "L" || item.productTypeCode == "P") {
                binding.llBusinessName.visibility = GONE

                binding.llVehicleNumber.visibility =
                    item.vehicleNo?.let {
                        binding.tvVehicleNumber.text = it
                        VISIBLE
                    } ?: GONE
                binding.llTaxPayer.visibility =
                    item.taxPayer?.let {
                        binding.tvTaxPayer.text = it
                        VISIBLE
                    } ?: GONE
            } else {
                binding.llBusinessName.visibility = VISIBLE
                binding.tvBusiness.text = item.customerName
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<CRMAgentTransactionDetail>() {
        override fun areItemsTheSame(oldItem: CRMAgentTransactionDetail, newItem: CRMAgentTransactionDetail): Boolean {
            return oldItem.voucherNo == newItem.voucherNo
        }

        override fun areContentsTheSame(oldItem: CRMAgentTransactionDetail, newItem: CRMAgentTransactionDetail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    /* override fun getFilter(): Filter {
         return object : Filter() {
             override fun performFiltering(constraint: CharSequence): FilterResults {
                 filteredList = if (TextUtils.isEmpty(constraint)) {
                     items
                 } else {
                     val newFilteredList: MutableList<CRMAgentTransactionDetail> = ArrayList()
                     for (o in items) {
                         o.taxType?.toLowerCase(Locale.getDefault())?.let {
                             if (it.contains(constraint.toString().toLowerCase(Locale.getDefault())))
                                 newFilteredList.add(o)
                         }
                     }
                     newFilteredList
                 }
                 val filterResults = FilterResults()
                 filterResults.values = filteredList
                 filterResults.count = filteredList.size
                 return filterResults
             }

             override fun publishResults(constraint: CharSequence, results: FilterResults) {
                 filteredList = results.values as List<CRMAgentTransactionDetail>
                 notifyDataSetChanged()
             }

         }
     }*/
}