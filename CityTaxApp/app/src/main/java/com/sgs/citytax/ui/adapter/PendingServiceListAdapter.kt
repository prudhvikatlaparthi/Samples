package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemPendingServiceBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.PendingServiceDetails
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class PendingServiceListAdapter(val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: ArrayList<PendingServiceDetails> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = PendingServiceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_pending_service, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            mItem -> {
                (holder as PendingServiceViewHolder).bind(mList[position], listener)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(mList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].isLoading)
            mLoading
        else mItem
    }

    class PendingServiceViewHolder(val mBinding: ItemPendingServiceBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(pendingList: PendingServiceDetails, iClickListener: IClickListener) {
            pendingList.serviceRequestNumber?.let {
                mBinding.txtServiceRequestNumber.text = it.toString()
            }
            pendingList.serviceRequestDate?.let {
                mBinding.txtServiceRequestDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            pendingList.accountName?.let {
                mBinding.txtCustomerName.text = it
            }
            pendingList.estimatedAmount?.let {
                mBinding.txtEstimatedAmount.text = formatWithPrecision(it)
            }
            pendingList.advanceAmount?.let {
                mBinding.txtAdvanceAmount.text = formatWithPrecision(it)
            }
            pendingList.totalAmount?.let {
                mBinding.txtNetReceivable.text = formatWithPrecision(it)
            }
            mBinding.llAssignTo3rdParty.visibility = View.GONE
            pendingList.assignTo3rdParty?.let {
                mBinding.llAssignTo3rdParty.visibility = View.VISIBLE
                mBinding.tvAssignTo3rdParty.text = it
            }

            mBinding.llRootView.setOnClickListener(object : OnSingleClickListener(){
                override fun onSingleClick(v: View?) {
                    iClickListener.onClick(v!!, adapterPosition, pendingList)
                }
            })
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(details: PendingServiceDetails) {
            if (details.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(pendingList: PendingServiceDetails?) {
        mList.add(pendingList!!)
        notifyItemInserted(mList.size - 1)
    }

    fun addAll(pendingList: List<PendingServiceDetails?>) {
        for (mServiceList in pendingList) {
            add(mServiceList)
        }
    }

    fun remove(pendingList: PendingServiceDetails?) {
        val position: Int = mList.indexOf(pendingList)
        if (position > -1) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<PendingServiceDetails> {
        return mList
    }

    fun clear() {
        mList = arrayListOf()
        notifyDataSetChanged()
    }

}