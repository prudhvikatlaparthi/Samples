package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemViolationTypesBinding
import com.sgs.citytax.model.MultipleViolationTypes
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision

class ViolationTypeAdapter(iClickListener: IClickListener) : RecyclerView.Adapter<ViolationTypeAdapter.ViolationViewHolder>() {

    private var mViolationTypes: ArrayList<MultipleViolationTypes> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViolationViewHolder {
        return ViolationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_violation_types, parent, false))
    }

    override fun onBindViewHolder(holder: ViolationViewHolder, position: Int) {
        mBinderHelper.bind(holder.mBinding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mViolationTypes[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mViolationTypes.size
    }

    fun update(list: List<MultipleViolationTypes>) {
        for (item: MultipleViolationTypes in list)
            mViolationTypes.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mViolationTypes = arrayListOf()
        notifyDataSetChanged()
    }


    class ViolationViewHolder(val mBinding: ItemViolationTypesBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(violationType: MultipleViolationTypes, iClickListener: IClickListener?) {
            mBinding.txtAmount.text = formatWithPrecision(violationType.fineAmount)
            mBinding.txtViolationType.text = violationType.violationType

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, violationType)
                }
                mBinding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, violationType)
                }
            }
        }
    }
}