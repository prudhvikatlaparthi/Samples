package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemImpoundmentTypesBinding
import com.sgs.citytax.model.MultipleImpoundmentTypes
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.formatWithPrecision

class ImpoundmentTypeAdapter(iClickListener: IClickListener) : RecyclerView.Adapter<ImpoundmentTypeAdapter.ViewHolder>() {

    private var mTypes: ArrayList<MultipleImpoundmentTypes> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_impoundment_types, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mBinderHelper.bind(holder.mBinding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mTypes[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mTypes.size
    }

    fun update(list: List<MultipleImpoundmentTypes>) {
        for (item: MultipleImpoundmentTypes in list)
            mTypes.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mTypes = arrayListOf()
        notifyDataSetChanged()
    }


    class ViewHolder(val mBinding: ItemImpoundmentTypesBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(impoundmentType: MultipleImpoundmentTypes, iClickListener: IClickListener?) {
            mBinding.txtAmount.text = formatWithPrecision("0")
            impoundmentType.fineAmount?.let {
                mBinding.txtAmount.text = formatWithPrecision(it)
            }
            mBinding.txtCharge.text = formatWithPrecision("0")
            impoundmentType.impoundmentCharge?.let {
                mBinding.txtCharge.text = formatWithPrecision(it)
            }
            mBinding.txtQuantity.text = "0"
            impoundmentType.quantity?.let {
                mBinding.txtQuantity.text = "$it"
            }
            impoundmentType.impoundmentType?.let {
                mBinding.txtImpoundmentType.text = it
            }

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener(object : OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, impoundmentType)
                    }
                })
                mBinding.txtDelete.setOnClickListener (object : OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, impoundmentType)
                    }
                })
            }
        }
    }
}