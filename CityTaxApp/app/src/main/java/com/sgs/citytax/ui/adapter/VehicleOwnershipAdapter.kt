package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemVehicleOwnershipBinding
import com.sgs.citytax.model.VUADMVehicleOwnership
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class VehicleOwnershipAdapter(iClickListener: IClickListener, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<VehicleOwnershipAdapter.MasterListViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var mArrayList: ArrayList<VUADMVehicleOwnership> = arrayListOf()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterListViewHolder {
        return MasterListViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_vehicle_ownership, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: MasterListViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW)
            holder.binding.txtDelete.visibility = View.GONE
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<VUADMVehicleOwnership>) {
        for (item: VUADMVehicleOwnership in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class MasterListViewHolder(var binding: ItemVehicleOwnershipBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vehicleOwnership: VUADMVehicleOwnership, iClickListener: IClickListener?) {
            binding.llStartDate.visibility = GONE
            binding.llEndDate.visibility = GONE
            binding.tvName.text = vehicleOwnership.vehicleNo
            vehicleOwnership.fromDate?.let {
                binding.llStartDate.visibility = VISIBLE
                binding.tvStartDate.text = displayFormatDate(it)
            }
            vehicleOwnership.toDate?.let {
                binding.llEndDate.visibility = VISIBLE
                binding.tvEndDate.text = displayFormatDate(it)
            }
            vehicleOwnership.estimatedTax?.let {
                binding.llEstimatedAmount.visibility = VISIBLE
                binding.tvEstimatedAmount.text = formatWithPrecision(it)
            }
            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, vehicleOwnership)
                    }
                })
                binding.txtDelete.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, vehicleOwnership)
                    }
                })
            }
        }

    }
}