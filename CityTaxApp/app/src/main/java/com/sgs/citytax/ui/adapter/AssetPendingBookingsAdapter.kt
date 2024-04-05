package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemAssetPendingBookingsBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.PendingBookingsList
import com.sgs.citytax.util.displayFormatDate

class AssetPendingBookingsAdapter(val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var pendingBookings: ArrayList<PendingBookingsList> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = PendingBookingsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_asset_pending_bookings, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pendingBooking: PendingBookingsList = pendingBookings[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as PendingBookingsViewHolder).bind(pendingBooking, listener,position)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(pendingBooking)
            }
        }
    }

    override fun getItemCount(): Int {
        return pendingBookings.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (pendingBookings[position].isLoading)
            mLoading
        else mItem
    }


    class PendingBookingsViewHolder(val mBinding: ItemAssetPendingBookingsBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(pendingBooking: PendingBookingsList, listener: Listener, position: Int) {
            pendingBooking.bookingRequestId?.let {
                mBinding.txtBookingNumber.text = it.toString()
            }
            pendingBooking.bookingRequestDate?.let {
                mBinding.txtBookingRequestDate.text = displayFormatDate(it)
            }
            pendingBooking.customer?.let {
                mBinding.txtCustomerName.text = it
            }

            mBinding.llRootView.setOnClickListener {
                listener.onItemClick(pendingBooking, position)
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(details: PendingBookingsList) {
            if (details.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(booking: PendingBookingsList?) {
        pendingBookings.add(booking!!)
        notifyItemInserted(pendingBookings.size - 1)
    }

    fun addAll(bookings: List<PendingBookingsList?>) {
        for (mBookings in bookings) {
            add(mBookings)
        }
    }

    fun remove(pendingBooking: PendingBookingsList?) {
        val position: Int = pendingBookings.indexOf(pendingBooking)
        if (position > -1) {
            pendingBookings.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<PendingBookingsList> {
        return pendingBookings
    }

    fun clear() {
        pendingBookings = arrayListOf()
        notifyDataSetChanged()
    }

    interface Listener {
        fun onItemClick(pendingBookings: PendingBookingsList, position: Int)
    }
}