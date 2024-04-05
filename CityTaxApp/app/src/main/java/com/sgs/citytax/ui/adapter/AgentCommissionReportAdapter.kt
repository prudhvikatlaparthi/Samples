package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CRMAgentTransactionDetail
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemCommissionReportBinding
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision
import com.sgs.citytax.util.getString

class AgentCommissionReportAdapter() : RecyclerView.Adapter<AgentCommissionReportAdapter.AgentCommissionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentCommissionViewHolder {
        return AgentCommissionViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_commission_report,
                parent, false))
    }

    override fun onBindViewHolder(holder: AgentCommissionViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    class AgentCommissionViewHolder(var binding: ItemCommissionReportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crmAgentTransaction: CRMAgentTransactionDetail) {

            when {
                MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASO.name -> {
                    binding.txtBusinessName.text = getString(R.string.collected_by)
                    binding.txtCustomerName.text = crmAgentTransaction.receivedBy
                }
                crmAgentTransaction.accountTypeCode == Constant.AccountTypeCode.CRO.name -> {
                    binding.txtBusinessName.text = binding.txtBusinessName.context.resources.getString(R.string.business_name)
                    binding.txtCustomerName.text = crmAgentTransaction.customerName
                }
                else -> {
                    binding.txtBusinessName.text = binding.txtBusinessName.context.resources.getString(R.string.customer_name)
                    binding.txtCustomerName.text = crmAgentTransaction.customerName
                }
            }
            binding.txtCommissionAmount.text = formatWithPrecision(crmAgentTransaction.commission)
            binding.txtInvoiceAmount.text = formatWithPrecision(crmAgentTransaction.amount)
            binding.txtVoucherNo.text = crmAgentTransaction.voucherNo
            binding.txtDate.text = formatDisplayDateTimeInMillisecond(crmAgentTransaction.date)
            binding.txtCommmissionBalance.text = formatWithPrecision(crmAgentTransaction.commissionBalance)
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

}