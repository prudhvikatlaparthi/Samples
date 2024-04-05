package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.LicenseRenewalResp
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.LicenseRenewalHistoryBinding
import com.sgs.citytax.databinding.RowBusinessTransactionHistoryBinding
import com.sgs.citytax.model.LicenseRenewalModel
import com.sgs.citytax.model.TransactionHistoryGenModel
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDate
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class LicenseRenewalHistoryAdapter(val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mTransactions: ArrayList<LicenseRenewalModel> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1
    private var itemClickListener: IClickListener? = listener

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: LicenseRenewalModel, itemClickListener: IClickListener, position: Int) {
            if (transaction.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    class ViewHolder(var binding: LicenseRenewalHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: LicenseRenewalModel, itemClickListener: IClickListener, position: Int) {
            binding.tvLicenseID.text = transaction.licenseId.toString()
            binding.tvTaxInvoiceDate.text = formatDisplayDateTimeInMillisecond(transaction.taxInvoiceDate)
            binding.tvNoticeReferenceNo.text = transaction.noticeReferenceNo
            binding.tvValidFrom.text = formatDate(transaction.validFromDate)
            binding.tvValidTill.text = formatDate(transaction.validToDate)

            binding.tvAmount.text = formatWithPrecision(transaction.amt?.toDouble())
            binding.root.setOnClickListener { itemClickListener.onClick(it, position, transaction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.license_renewal_history, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return mTransactions.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mTransactions[position].isLoading)
            mLoading
        else mItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = mTransactions[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as ViewHolder).bind(transaction, itemClickListener!!, position)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(transaction, itemClickListener!!, position)
            }
        }
    }

    fun add(transaction: LicenseRenewalModel) {
        mTransactions.add(transaction)
        notifyItemInserted(mTransactions.size - 1)
    }

    fun addAll(transactions: List<LicenseRenewalModel>) {
        for (transaction in transactions) {
            add(transaction)
        }
    }

    fun remove(transaction: LicenseRenewalModel?) {
        val position: Int = mTransactions.indexOf(transaction)
        if (position > -1) {
            mTransactions.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}