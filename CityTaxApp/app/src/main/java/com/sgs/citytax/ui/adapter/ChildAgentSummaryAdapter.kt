package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ChildAgentSummary
import com.sgs.citytax.databinding.ItemChildAgentSummaryBinding
import com.sgs.citytax.util.formatWithPrecision

class ChildAgentSummaryAdapter : RecyclerView.Adapter<ChildAgentSummaryAdapter.ChildAgentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildAgentViewHolder {
        return ChildAgentViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_child_agent_summary, parent, false
        )
        )
    }

    override fun onBindViewHolder(holder: ChildAgentViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateAdapter(list: List<ChildAgentSummary>) {
        differ.submitList(list)
    }

    inner class ChildAgentViewHolder(var binding: ItemChildAgentSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(childAgentSummary: ChildAgentSummary) {
            binding.agentWrapper.isVisible = childAgentSummary.agentCode?.let {
                if (it.trim() != "") {
                    binding.txtAgentCode.text = it
                    true
                } else {
                    false
                }
            } ?: false
            binding.txtAgentName.text = childAgentSummary.agentName
            binding.txtAgentType.text = childAgentSummary.agentType

            binding.txtCashCollected.text = formatWithPrecision(childAgentSummary.cashCollected)
            binding.txtAmountDeposited.text = formatWithPrecision(childAgentSummary.depositAmount)
            binding.txtCashLimit.text = formatWithPrecision(childAgentSummary.cashLimit)
            binding.txtCashInHand.text = formatWithPrecision(childAgentSummary.cashInHand)
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<ChildAgentSummary>() {
        override fun areItemsTheSame(oldItem: ChildAgentSummary, newItem: ChildAgentSummary): Boolean {
            return oldItem.agentID == newItem.agentID
        }

        override fun areContentsTheSame(oldItem: ChildAgentSummary, newItem: ChildAgentSummary): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)
}