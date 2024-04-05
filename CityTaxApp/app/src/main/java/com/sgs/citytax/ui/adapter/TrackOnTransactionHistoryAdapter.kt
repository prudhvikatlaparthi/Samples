package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.TrackOnTransaction
import com.sgs.citytax.databinding.ItemTransactionHistoryBinding
import com.sgs.citytax.util.*

class TrackOnTransactionHistoryAdapter(val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemClickListener: IClickListener? = listener

    class ViewHolder(var binding: ItemTransactionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TrackOnTransaction, itemClickListener: IClickListener, position: Int) {
            if (transaction.taxRuleBookCode == Constant.TaxRuleBook.VT.Code) {
                binding.tvDateHeader.text = getString(R.string.violation_date)
                binding.tvViolationType.text = transaction.violationType
                binding.llViolation.visibility = View.VISIBLE
            } else {
                binding.tvDateHeader.text = getString(R.string.impound_date)
                binding.tvImpoundType.text = transaction.impoundmentType
                binding.llImpoundType.visibility = View.VISIBLE
            }
            binding.tvDate.text = formatDisplayDateTimeInMillisecond(transaction.transactionDate)
            binding.tvNoticeReffNumber.text = transaction.noticeReferenceNo
            transaction.VehicleNo?.let {
                binding.tvVehicleNumber.text = it
                binding.llVoucherno.visibility = View.VISIBLE
            }

            transaction.vehicleOwner?.let {
                binding.tvVehicleOwner.text = it
                binding.llVehicleOwner.visibility = View.VISIBLE
            }
            transaction.violationDetails?.let {
                binding.tvViolationDetails.text = it
                binding.llViolationDetails.visibility = View.VISIBLE
            }

            transaction.violator?.let {
                binding.tvViolator.text = it
                binding.llViolator.visibility = View.VISIBLE
            }

            transaction.impoundmentReason?.let {
                binding.tvImpounmentReason.text = it
                binding.llImpoundReson.visibility = View.VISIBLE
            }



            binding.tvTransactionAmount.text = formatWithPrecision(transaction.transactionAmount)
            binding.tvAmount.text = formatWithPrecision(transaction.amount)
            binding.tvPaymentMode.text = transaction.paymentMode

            binding.root.setOnClickListener { itemClickListener.onClick(it, position, transaction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_transaction_history, parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = differ.currentList[position]
        (holder as ViewHolder).bind(transaction, itemClickListener!!, position)
    }
/*
    fun add(transaction: TrackOnTransaction) {
        mTransactions.add(transaction)
        notifyItemInserted(mTransactions.size - 1)
    }

    fun addAll(transactions: List<TrackOnTransaction>) {
        for (transaction in transactions) {
            add(transaction)
        }
    }

    fun remove(transaction: TrackOnTransaction?) {
        val position: Int = mTransactions.indexOf(transaction)
        if (position > -1) {
            mTransactions.removeAt(position)
            notifyItemRemoved(position)
        }
    }*/

    private val differCallback = object : DiffUtil.ItemCallback<TrackOnTransaction>() {
        override fun areItemsTheSame(oldItem: TrackOnTransaction, newItem: TrackOnTransaction): Boolean {
//            return oldItem.advancerecievedid == newItem.advancerecievedid
            return false
        }

        override fun areContentsTheSame(oldItem: TrackOnTransaction, newItem: TrackOnTransaction): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}