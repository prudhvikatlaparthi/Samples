package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemHotelBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.HotelDetails
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class HotelListAdapter(val listener: IClickListener, val screenMode: Constant.ScreenMode?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mHotels: ArrayList<HotelDetails> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1
    private val binderHelper = ViewBinderHelper()


    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = HotelViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_hotel, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hotelDetails: HotelDetails = mHotels[position]
        when (getItemViewType(position)) {
            mItem -> {
                binderHelper.bind((holder as HotelViewHolder).mBinding.swipeLayout, position.toString())
                binderHelper.closeAll()
                (holder as HotelViewHolder).bind(hotelDetails, listener, screenMode)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(hotelDetails)
            }
        }
    }

    override fun getItemCount(): Int {
        return mHotels.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mHotels[position].isLoading)
            mLoading
        else mItem
    }

    class HotelViewHolder(val mBinding: ItemHotelBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(details: HotelDetails, iClickListener: IClickListener?, screenMode: Constant.ScreenMode?) {
            details.hotelName?.let {
                mBinding.txtHotelName.text = it
            }
            details.star?.let {
                mBinding.txtStar.text = it
            }
            details.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }
            details.active?.let {
                mBinding.txtActive.text = if (it =="Y") mBinding.txtActive.context.getString(R.string.yes) else mBinding.txtActive.context.getString(R.string.no)
            }

            if (screenMode == Constant.ScreenMode.VIEW) {
                mBinding.txtDelete.visibility = View.GONE
            }
            if(details.allowDelete=="N"){
                mBinding.txtDelete.visibility = View.GONE
            }

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, details)
                    }
                })
                mBinding.txtDelete.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, details)
                    }
                })
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(details: HotelDetails) {
            if (details.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(hotelDetails: HotelDetails?) {
        mHotels.add(hotelDetails!!)
        notifyItemInserted(mHotels.size - 1)
    }

    fun addAll(hotelDetails: List<HotelDetails?>) {
        for (mHotelList in hotelDetails) {
            add(mHotelList)
        }
    }

    fun remove(hotelDetails: HotelDetails?) {
        val position: Int = mHotels.indexOf(hotelDetails)
        if (position > -1) {
            mHotels.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<HotelDetails> {
        return mHotels
    }

    fun clear() {
        mHotels = arrayListOf()
        notifyDataSetChanged()
    }

}