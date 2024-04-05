package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AssetFitnessesData
import com.sgs.citytax.databinding.RowItemBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class AssetFitnessAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu) : RecyclerView.Adapter<AssetFitnessAdapter.ViewHolder>() {

    private var mArrayList: ArrayList<AssetFitnessesData> = arrayListOf()
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

    fun update(list: List<AssetFitnessesData>) {
        for (item: AssetFitnessesData in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: RowItemBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetFitnesssData: AssetFitnessesData, iClickListener: IClickListener?) {
            binding.tvDate.text = displayFormatDate(assetFitnesssData.fitnessDate)
            binding.tvType.text = assetFitnesssData.fitnessType
            binding.tvInsurer.text = assetFitnesssData.vendor
            binding.tvPrice.visibility = View.VISIBLE
            binding.tvPrice.text =  formatWithPrecision(assetFitnesssData.cost)

            if (assetFitnesssData.awsPath != null && assetFitnesssData.awsPath!!.isNotEmpty())
                Glide.with(context).load(assetFitnesssData.awsPath).placeholder(R.drawable.ic_place_holder).override(72, 72).into(binding.imgDocument)

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, assetFitnesssData)
                }
                binding.imgDocument.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, assetFitnesssData)
                }

                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, assetFitnesssData)
                }
            }

        }
    }

}
