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
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemTaxNoticeHistoryBinding
import com.sgs.citytax.model.TaxNoticeHistoryList
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class TaxNoticeHistoryAdapter(val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaxNoticeHistoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_tax_notice_history, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TaxNoticeHistoryViewHolder).bind(differ.currentList[position], listener, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class TaxNoticeHistoryViewHolder(var binding: ItemTaxNoticeHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxNoticeHistoryList: TaxNoticeHistoryList, listener: Listener, position: Int) {
            binding.txtProduct.text = taxNoticeHistoryList.product
            binding.txtDate.text = formatDisplayDateTimeInMillisecond(taxNoticeHistoryList.taxInvoiceDate)
            binding.txtTaxInvoiceId.text = taxNoticeHistoryList.taxInvoiceID
            binding.txtSubTotal.text = formatWithPrecision(taxNoticeHistoryList.subTotal)
            binding.txtSycoTaxID.text = taxNoticeHistoryList.sycoTaxId
            /*taxNoticeHistoryList.statusCode?.split(".")?.get(1)?.let {
                binding.txtStatus.text = it
            }*/
            taxNoticeHistoryList.status.let {
                binding.txtStatus.text=it
            }



            if (taxNoticeHistoryList.taxSubType.isNullOrEmpty()) {
                binding.llOccupancyName.visibility = GONE
            } else {
                binding.llOccupancyName.visibility = VISIBLE
                binding.txtOccupancyName.text = taxNoticeHistoryList.taxSubType
            }
            if (taxNoticeHistoryList.taxRuleBookCode == Constant.KEY_COM_PROP || taxNoticeHistoryList.taxRuleBookCode == Constant.KEY_RES_PROP) {
                binding.lbloccupancy.text = binding.lbloccupancy.context.resources.getString(R.string.property_type)
            }
            if (taxNoticeHistoryList.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code) {
                binding.lbloccupancy.text = binding.lbloccupancy.context.resources.getString(R.string.land_type)
            }
            if (taxNoticeHistoryList.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code) {
                binding.lbloccupancy.text = binding.lbloccupancy.context.resources.getString(R.string.operator_type_history)
            }
            if (taxNoticeHistoryList.taxRuleBookCode == Constant.TaxRuleBook.HOTEL.Code) {
                binding.lbloccupancy.text = binding.lbloccupancy.context.resources.getString(R.string.star)
            }
            binding.btnCancel.visibility = GONE
            if (MyApplication.getPrefHelper().superiorTo.isNotEmpty() && taxNoticeHistoryList.currentDue == taxNoticeHistoryList.subTotal && taxNoticeHistoryList.statusCode != Constant.TaxInvoices.CANCELLED.Status)
                binding.btnCancel.visibility = VISIBLE

            if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ISP.name)
            {
                binding.btnCancel.visibility = VISIBLE
            }
            if(taxNoticeHistoryList.statusCode == Constant.TaxInvoices.CANCELLED.Status){
                binding.btnCancel.visibility = GONE
            }
            binding.llRootView.setOnClickListener {
                listener.onItemClick(taxNoticeHistoryList, position)
            }

            binding.btnCancel.setOnClickListener {
                listener.onCancelTaxNotice(taxNoticeHistoryList)
            }

        }

    }

    fun updateAdapter(list: MutableList<TaxNoticeHistoryList>) {
        differ.submitList(list.toList())
    }

    fun clearAdapter() {
        differ.submitList(null)
    }

    private val differCallback = object : DiffUtil.ItemCallback<TaxNoticeHistoryList>() {
        override fun areItemsTheSame(oldItem: TaxNoticeHistoryList, newItem: TaxNoticeHistoryList): Boolean {
            return oldItem.taxInvoiceDate + oldItem.taxInvoiceID == newItem.taxInvoiceDate + newItem.taxInvoiceID
        }

        override fun areContentsTheSame(oldItem: TaxNoticeHistoryList, newItem: TaxNoticeHistoryList): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    interface Listener {
        fun onItemClick(taxNoticeHistoryList: TaxNoticeHistoryList, position: Int)
        fun onCancelTaxNotice(taxNotice: TaxNoticeHistoryList)
    }
}

