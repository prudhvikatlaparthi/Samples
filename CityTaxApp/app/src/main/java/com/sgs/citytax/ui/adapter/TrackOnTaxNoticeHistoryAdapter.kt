package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.databinding.ItemTrackOnTaxNoticeHistoryBinding
import com.sgs.citytax.model.TaxNoticeDetail
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDate
import com.sgs.citytax.util.formatWithPrecision

class TrackOnTaxNoticeHistoryAdapter(private val listener: IClickListener, private var mTaxNoticeDetails: List<ImpondmentReturn>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(var binding: ItemTrackOnTaxNoticeHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxNoticeDetail: ImpondmentReturn, listener: IClickListener) {
            taxNoticeDetail.transactiondate?.let {
                binding.tvDate.text = formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyy)
            }
            binding.tvInvoiceNo.text = taxNoticeDetail.transactionNo.toString()
            binding.tvNoticeReferenceNo.text = taxNoticeDetail.noticeReferenceNo
           /* taxNoticeDetail.statusCode?.split(".")?.get(1)?.let {
                binding.tvStatus.text = it
            }*/
            if(taxNoticeDetail.statusCode == Constant.TaxInvoices.CANCELLED.Status){
                binding.tvStatus.text=binding.tvStatus.context.getString(R.string.cancelled)
            }
            if(taxNoticeDetail.statusCode == Constant.TaxInvoices.NEW.Status){
                binding.tvStatus.text=binding.tvStatus.context.getString(R.string.status_new)
            }
            binding.tvTotal.text = formatWithPrecision(taxNoticeDetail.amount)
            binding.btnCancel.visibility = GONE
            if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.LEI.name) {
                if (taxNoticeDetail.currentDue == taxNoticeDetail.amount && taxNoticeDetail.statusCode != Constant.TaxInvoices.CANCELLED.Status)
                    binding.btnCancel.visibility = VISIBLE
            }

            binding.btnCancel.setOnClickListener {
                listener.onClick(it, adapterPosition, taxNoticeDetail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_track_on_tax_notice_history, parent, false))
    }

    override fun getItemCount(): Int {
        return mTaxNoticeDetails.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = mTaxNoticeDetails[position]
        (holder as ViewHolder).bind(transaction, listener)
    }

}