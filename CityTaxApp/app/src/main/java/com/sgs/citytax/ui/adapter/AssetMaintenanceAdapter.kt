package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AssetMaintenanceData
import com.sgs.citytax.databinding.RowItemBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class AssetMaintenanceAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu) : RecyclerView.Adapter<AssetMaintenanceAdapter.ViewHolder>() {

    private var mArrayList: ArrayList<AssetMaintenanceData> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.row_item, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT)
            mBinderHelper.lockSwipe(position.toString())
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<AssetMaintenanceData>) {
        for (item: AssetMaintenanceData in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: RowItemBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetMaintenanceData: AssetMaintenanceData, iClickListener: IClickListener?) {
            binding.tvDate.text = displayFormatDate(assetMaintenanceData.maintenanceDate)
            binding.tvType.text = assetMaintenanceData.maintenanceType
            binding.tvInsurer.text = assetMaintenanceData.vendor
            binding.tvPrice.visibility = View.VISIBLE
            binding.tvPrice.text =  formatWithPrecision(assetMaintenanceData.totalCost)

            if (assetMaintenanceData.awsPath != null && assetMaintenanceData.awsPath!!.isNotEmpty())
                Glide.with(context).load(assetMaintenanceData.awsPath).placeholder(R.drawable.ic_place_holder).override(72, 72).into(binding.imgDocument)

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, assetMaintenanceData)
                }
                binding.imgDocument.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, assetMaintenanceData)
                }

                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, assetMaintenanceData)
                }
            }

        }
    }

}
