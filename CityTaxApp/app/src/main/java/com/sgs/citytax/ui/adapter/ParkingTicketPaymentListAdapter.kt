package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ParkingPaymentTrans
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.ParkingTicketPaymentListBinding
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class ParkingTicketPaymentListAdapter(val clickListener: IClickListener, val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mReturnList: ArrayList<ParkingPaymentTrans> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.parking_ticket_payment_list, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mList = mReturnList[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as ViewHolder).bind(mList, clickListener, listener, position)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(mList)
            }
        }
    }

    override fun getItemCount(): Int {
        return mReturnList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mReturnList[position].isLoading)
            mLoading
        else mItem
    }


    class ViewHolder(val mBinding: ParkingTicketPaymentListBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(mImpondmentReturn: ParkingPaymentTrans, clickListener: IClickListener, listener: Listener, position: Int) {
                mBinding.llReturn.visibility = View.VISIBLE
                //mBinding.llViolation.visibility = View.GONE

                mBinding.tvParentParkingType.text = mImpondmentReturn.parentParkingType
                mBinding.tvViolationDate.text = formatDateTimeInMillisecond(mImpondmentReturn.transactiondate)
                mBinding.tvParkingType.text = mImpondmentReturn.parkingType
                mBinding.tvParkingPlaceId.text = mImpondmentReturn.parkingPlaceID.toString()

                mBinding.tvParkingPlace.text = mImpondmentReturn.parkingPlace
                mBinding.tvVehicleNumber.text = mImpondmentReturn.vehicleNo
                mBinding.tvVehicleOwner.text = mImpondmentReturn.vehicleOwner


                mBinding.tvCurrentDue.text = formatWithPrecision(mImpondmentReturn.currentDue)
                mBinding.tvMinPayAmount.text = formatWithPrecision(mImpondmentReturn.minmumPayAmount)
                mBinding.tvAmount.text = formatWithPrecision(mImpondmentReturn.amount)


            mBinding.btnPay.setOnClickListener {
                clickListener.onClick(it, adapterPosition, mImpondmentReturn)
            }

            mBinding.llRootView.setOnClickListener {
                listener.onItemClick(mImpondmentReturn, position)
            }

        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetList: ParkingPaymentTrans) {
            if (assetList.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(list: ParkingPaymentTrans?) {
        mReturnList.add(list!!)
        notifyItemInserted(mReturnList.size - 1)
    }

    fun addAll(mImpondmentReturnList: List<ParkingPaymentTrans?>) {
        for (mList in mImpondmentReturnList) {
            add(mList)
        }
    }

    fun remove(mList: ParkingPaymentTrans?) {
        val position: Int = mReturnList.indexOf(mList)
        if (position > -1) {
            mReturnList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<ParkingPaymentTrans> {
        return mReturnList
    }

    fun clear() {
        mReturnList = arrayListOf()
        notifyDataSetChanged()
    }

    interface Listener {
        fun onItemClick(list: ParkingPaymentTrans, position: Int)
    }
}