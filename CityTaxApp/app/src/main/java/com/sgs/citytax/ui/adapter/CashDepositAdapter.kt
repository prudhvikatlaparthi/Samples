package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemCashDepositHistoryBinding
import com.sgs.citytax.model.VUAgentCashCollectionSummary
import com.sgs.citytax.util.formatDateTimeInMillisecond
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class CashDepositAdapter(var vuAgentCashCollectionSummaries: List<VUAgentCashCollectionSummary>) : RecyclerView.Adapter<CashDepositAdapter.CashRequestViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashRequestViewHolder {
        return CashRequestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cash_deposit_history, parent, false))
    }

    override fun getItemCount(): Int {
        return vuAgentCashCollectionSummaries.size
    }

    override fun onBindViewHolder(holder: CashRequestViewHolder, position: Int) {
        holder.bind(vuAgentCashCollectionSummaries.get(position))
    }

    class CashRequestViewHolder(var binding: ItemCashDepositHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(vuAgentCashCollectionSummary: VUAgentCashCollectionSummary) {
            binding.txtAgentName.text = vuAgentCashCollectionSummary.agentName
            if (vuAgentCashCollectionSummary.status.isNullOrEmpty())
                binding.llStatus.visibility = View.GONE
            else {
                binding.llStatus.visibility = View.VISIBLE
                binding.txtStatus.text = vuAgentCashCollectionSummary.status
            }
            binding.txtDepositedAmount.text = formatWithPrecision(vuAgentCashCollectionSummary.depositAmount.toString())

            if (vuAgentCashCollectionSummary.processedDate.isNullOrEmpty())
                binding.llDepositDate.visibility = View.GONE
            else {
                binding.llDepositDate.visibility = View.VISIBLE
                binding.txtDepositDate.text = formatDisplayDateTimeInMillisecond(vuAgentCashCollectionSummary.processedDate)
            }

            if (vuAgentCashCollectionSummary.requestDate.isNullOrEmpty())
                binding.llRequestedDate.visibility = View.GONE
            else {
                binding.llRequestedDate.visibility = View.VISIBLE
                binding.txtRequestedDate.text = formatDisplayDateTimeInMillisecond(vuAgentCashCollectionSummary.requestDate)
            }

        }
    }
    private val differCallback = object : DiffUtil.ItemCallback<VUAgentCashCollectionSummary>() {
        override fun areItemsTheSame(oldItem: VUAgentCashCollectionSummary, newItem: VUAgentCashCollectionSummary): Boolean {
            return oldItem.accountID == newItem.accountID
        }

        override fun areContentsTheSame(oldItem: VUAgentCashCollectionSummary, newItem: VUAgentCashCollectionSummary): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}

