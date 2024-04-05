package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.RowBusinessTransactionHistoryBinding
import com.sgs.citytax.model.TransactionHistoryGenModel
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class PropertyTransactionHistoryAdapter(val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mTransactions: ArrayList<TransactionHistoryGenModel> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1
    private var itemClickListener: IClickListener? = listener

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionHistoryGenModel, itemClickListener: IClickListener, position: Int) {
            if (transaction.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    class ViewHolder(var binding: RowBusinessTransactionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionHistoryGenModel, itemClickListener: IClickListener, position: Int) {
            binding.tvReceiptNumber.text = transaction.voucherNo
            binding.tvDate.text = formatDisplayDateTimeInMillisecond(transaction.date)
            binding.tvTaxName.text = transaction.product
            binding.tvAmount.text = formatWithPrecision(transaction.amount)
            binding.tvPaymentMode.text = transaction.paymentMode
            binding.tvSycoTaxID.text = transaction.sycoTaxID
            binding.root.setOnClickListener { itemClickListener.onClick(it, position, transaction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.row_business_transaction_history, parent, false))
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

    fun add(transaction: TransactionHistoryGenModel) {
        mTransactions.add(transaction)
        notifyItemInserted(mTransactions.size - 1)
    }

    fun addAll(transactions: List<TransactionHistoryGenModel>) {
        for (transaction in transactions) {
            add(transaction)
        }
    }

    fun remove(transaction: TransactionHistoryGenModel?) {
        val position: Int = mTransactions.indexOf(transaction)
        if (position > -1) {
            mTransactions.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}