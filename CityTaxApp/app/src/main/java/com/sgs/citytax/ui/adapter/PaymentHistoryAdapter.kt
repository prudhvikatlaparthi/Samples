package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.RowPaymentHistoryBinding
import com.sgs.citytax.model.TaxPaymentHistory
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class PaymentHistoryAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var paymentHistories: ArrayList<TaxPaymentHistory> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = PaymentHistoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.row_payment_history, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val paymentHistory: TaxPaymentHistory = paymentHistories[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as PaymentHistoryViewHolder).bind(paymentHistory)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(paymentHistory)
            }
        }
    }

    override fun getItemCount(): Int {
        return paymentHistories.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (paymentHistories[position].isLoading)
            mLoading
        else mItem
    }


    class PaymentHistoryViewHolder(val mBinding: RowPaymentHistoryBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(paymentHistory: TaxPaymentHistory) {
            mBinding.tvAmount.text = formatWithPrecision(paymentHistory.amount)
            mBinding.tvType.text = paymentHistory.paymentMode
            mBinding.tvDate.text = formatDisplayDateTimeInMillisecond(paymentHistory.transactionDate)
            mBinding.tvPaymentMethod.text = paymentHistory.paymentMode

            if (!paymentHistory.chequeStatus.isNullOrEmpty()) {
                mBinding.llStatus.visibility = View.VISIBLE
                mBinding.tvStatus.text = paymentHistory.chequeStatus
            } else {
                mBinding.llStatus.visibility = View.GONE
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(details: TaxPaymentHistory) {
            if (details.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(paymentHistory: TaxPaymentHistory?) {
        paymentHistories.add(paymentHistory!!)
        notifyItemInserted(paymentHistories.size - 1)
    }

    fun addAll(paymentHistories: List<TaxPaymentHistory?>) {
        for (mPaymentHistory in paymentHistories) {
            add(mPaymentHistory)
        }
    }

    fun remove(paymentHistory: TaxPaymentHistory?) {
        val position: Int = paymentHistories.indexOf(paymentHistory)
        if (position > -1) {
            paymentHistories.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<TaxPaymentHistory> {
        return paymentHistories
    }

    fun clear() {
        paymentHistories = arrayListOf()
        notifyDataSetChanged()
    }

}