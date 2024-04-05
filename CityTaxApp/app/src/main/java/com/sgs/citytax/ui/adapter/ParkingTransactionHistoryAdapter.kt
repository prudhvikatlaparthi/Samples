package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ParkingPaymentTrans
import com.sgs.citytax.databinding.ItemParkingTransactionHistoryBinding
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class ParkingTransactionHistoryAdapter(val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mTransactions: ArrayList<ParkingPaymentTrans> = arrayListOf()
    private val mItem = 0
    private var itemClickListener: IClickListener? = listener

    class ViewHolder(var binding: ItemParkingTransactionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: ParkingPaymentTrans, itemClickListener: IClickListener, position: Int) {
            binding.tvDate.text = formatDisplayDateTimeInMillisecond(transaction.transactiondate)
            binding.tvVehicleNumber.text = transaction.vehicleNo
            binding.tvVehicleOwner.text = transaction.vehicleOwner
            binding.tvAmount.text = formatWithPrecision(transaction.amount)
            binding.tvCurrentDue.text = formatWithPrecision(transaction.currentDue)
            binding.tvParkingType.text = transaction.parkingType
            binding.tvParkingPlace.text = transaction.parkingPlace
            binding.root.setOnClickListener { itemClickListener.onClick(it, position, transaction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_parking_transaction_history, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return mTransactions.size
    }

    override fun getItemViewType(position: Int): Int {
        return mItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = mTransactions[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as ViewHolder).bind(transaction, itemClickListener!!, position)
            }
        }
    }

    fun add(transaction: ParkingPaymentTrans) {
        mTransactions.add(transaction)
        notifyItemInserted(mTransactions.size - 1)
    }

    fun addAll(transactions: List<ParkingPaymentTrans>) {
        for (transaction in transactions) {
            add(transaction)
        }
    }

    fun remove(transaction: ParkingPaymentTrans?) {
        val position: Int = mTransactions.indexOf(transaction)
        if (position > -1) {
            mTransactions.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}