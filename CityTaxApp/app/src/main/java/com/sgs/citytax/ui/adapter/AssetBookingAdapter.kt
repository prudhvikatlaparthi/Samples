package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.AssetBookingRequestLine
import com.sgs.citytax.databinding.ItemAssetBookingBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class AssetBookingAdapter(iClickListener: IClickListener, code: Constant.QuickMenu) : RecyclerView.Adapter<AssetBookingAdapter.ViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var mArrayList: ArrayList<AssetBookingRequestLine> = arrayListOf()
    private val mCode: Constant.QuickMenu = code

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_asset_booking, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mCode == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT)
            mBinderHelper.lockSwipe(position.toString())
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mCode, mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    private fun add(line: AssetBookingRequestLine) {
        mArrayList.add(line)
    }

    fun addAll(list: List<AssetBookingRequestLine>) {
        for (item: AssetBookingRequestLine in list)
            add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList.clear()
        notifyDataSetChanged()
    }

    fun update(line: AssetBookingRequestLine) {
        var index = -1
        mArrayList.forEach {
            if (it.bookingRequestLineID == line.bookingRequestLineID)
                index = mArrayList.indexOf(it)
        }
        if (index != -1)
            mArrayList.set(index, line)
        notifyDataSetChanged()
    }

    fun get(): ArrayList<AssetBookingRequestLine> {
        return mArrayList
    }

    fun remove(obj: AssetBookingRequestLine) {
        mArrayList.remove(obj)
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemAssetBookingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(line: AssetBookingRequestLine, code: Constant.QuickMenu, iClickListener: IClickListener?) {
            binding.tvAssetCategory.text = line.assetCategory

            val startDate = line.bookingStartDate
            val endDate = line.bookingEndDate

            startDate?.let {
                binding.tvStartDate.text = if (it.length == 23) formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyhhmmssaa) else formatDate(it, Constant.DateFormat.DFyyyyMMddHHmmss, Constant.DateFormat.DFddMMyyyyhhmmssaa)
            }
            endDate?.let {
                binding.tvEndDate.text = if (it.length == 23) formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyhhmmssaa) else formatDate(it, Constant.DateFormat.DFyyyyMMddHHmmss, Constant.DateFormat.DFddMMyyyyhhmmssaa)
            }
            line.bookingQuantity?.let {
                binding.tvBookingQuantity.text = it.toString()
            }
            line.assignQuantity?.let {
                binding.tvAssignQauntity.text = it.toString()
            }

            val isDateInRanges: Boolean = startDate?.let { endDate?.let { it1 -> checkDatesInTodayRange(it, it1) } }!!

            if (code == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT) {
                binding.llBookingQuantity.visibility = VISIBLE
                binding.llAssignQuantity.visibility = VISIBLE
                if (isDateInRanges) {
                    binding.btnAssignAsset.visibility = VISIBLE
                } else {
                    binding.btnAssignAsset.visibility = GONE
                }
            } else {
                binding.llBookingQuantity.visibility = GONE
                binding.llAssignQuantity.visibility = GONE
                binding.btnAssignAsset.visibility = GONE
            }

            if (line.bookingQuantity == line.assignQuantity) {
                binding.btnAssignAsset.isEnabled = false
            }


            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, line)
                    }
                })
                binding.txtDelete.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, line)
                    }
                })
                binding.btnAssignAsset.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, line)
                    }
                })
            }
        }
    }

}