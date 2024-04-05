package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemHandoverDueNoticeBinding
import com.sgs.citytax.model.HandoverDueNoticesList
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond

class HandoverDueNoticesAdapter(val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaxNoticeHistoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_handover_due_notice, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TaxNoticeHistoryViewHolder).bind(differ.currentList[position], listener, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class TaxNoticeHistoryViewHolder(var binding: ItemHandoverDueNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dueNotice: HandoverDueNoticesList, listener: Listener, position: Int) {
            binding.txtDueDate.text = formatDisplayDateTimeInMillisecond(dueNotice.dueNoticeDate)
            dueNotice.handoverDate?.let {
                binding.txtHandoverDate.text = formatDisplayDateTimeInMillisecond(dueNotice.handoverDate)
                binding.llHandoverDate.visibility = View.VISIBLE
            }

            dueNotice.status.let {
                binding.txtStatus.text=it
            }
            dueNotice.year.let {
                binding.txtYear.text= it.toString()
            }
            dueNotice.noticeReferenceNo.let {
                binding.txtNoticeNumber.text= it
            }
            dueNotice.dueNoticeType.let {
                binding.txtDueInvoiceType.text= it
            }


//            binding.btnCancel.visibility = GONE
//            if (MyApplication.getPrefHelper().superiorTo.isNotEmpty() && dueNotice.currentDue == dueNotice.subTotal && dueNotice.statusCode != Constant.TaxInvoices.CANCELLED.Status)
//                binding.btnCancel.visibility = VISIBLE


            binding.btnCancel.setOnClickListener (object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    listener.onItemClick(dueNotice, position)
                }
            })

        }

    }

    fun updateAdapter(list: MutableList<HandoverDueNoticesList>) {
        differ.submitList(list.toList())
    }

    fun clearAdapter() {
        differ.submitList(null)
    }

    private val differCallback = object : DiffUtil.ItemCallback<HandoverDueNoticesList>() {
        override fun areItemsTheSame(oldItem: HandoverDueNoticesList, newItem: HandoverDueNoticesList): Boolean {
            return oldItem.dueNoticeDate + oldItem.dueNoticeID == newItem.dueNoticeDate + newItem.dueNoticeID
        }

        override fun areContentsTheSame(oldItem: HandoverDueNoticesList, newItem: HandoverDueNoticesList): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    interface Listener {
        fun onItemClick(dueNotice: HandoverDueNoticesList, position: Int)
    }
}

