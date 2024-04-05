package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemAdvertisementsBinding
import com.sgs.citytax.model.VUCRMAdvertisements
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class AdvertisementAdapter(var advertisements: ArrayList<VUCRMAdvertisements>, iClickListener: IClickListener, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<AdvertisementAdapter.AdvertisementViewHolder>() {

    private val binderHelper = ViewBinderHelper()
    private var iClickListener: IClickListener? = iClickListener

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertisementViewHolder {
        return AdvertisementViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_advertisements, parent, false))
    }

    override fun onBindViewHolder(holder: AdvertisementViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW) {
            holder.mBinding.txtDelete.visibility = View.GONE
        }
        binderHelper.bind(holder.mBinding.swipeLayout, position.toString())
        binderHelper.closeAll()
        holder.bind(advertisements[position], iClickListener)
    }

    override fun getItemCount(): Int {
        return advertisements.size
    }


    class AdvertisementViewHolder(val mBinding: ItemAdvertisementsBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(advertisement: VUCRMAdvertisements, iClickListener: IClickListener?) {
            mBinding.txtAdvertisementId.text = advertisement.advertisementId.toString()
            mBinding.txtAdvertisementName.text = advertisement.advertisementTypeName
            mBinding.txtQuantity.text = advertisement.quantity.toString()
            mBinding.txtActive.text= if (advertisement.active.equals("Y")) mBinding.txtActive?.context?.resources?.getString(R.string.yes) else mBinding.txtActive?.context?.resources?.getString(R.string.no)
            if(advertisement.allowDelete=="N") {
                mBinding.txtDelete.visibility = View.GONE
            }
            advertisement.startDate?.let {
                mBinding.txtStartDate.text = formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyy)
            }
            advertisement.estimatedTax?.let {
                mBinding.llEstimatedAmount.visibility = View.VISIBLE
                mBinding.tvEstimatedAmount.text = formatWithPrecision(it)
            }

            advertisement.Length?.let {
                if(advertisement.unitcode == Constant.UnitCode.EA.name)
                {
                    mBinding.llLength.visibility = View.GONE
                }
                else {
                    mBinding.txtLength.text = formatWithPrecisionCustomDecimals(it, false, 3)
                }
            }

            advertisement.wdth?.let {
                if(advertisement.unitcode == Constant.UnitCode.EA.name)
                {
                    mBinding.llWidth.visibility = View.GONE
                }
                else {
                    mBinding.txtWidth.text = formatWithPrecisionCustomDecimals(it, false, 3)
                }

            }

            advertisement.TaxableMatter?.let {
                mBinding.txtTaxableMatter.text = formatWithPrecisionCustomDecimals(it, false,3)
            }

            advertisement.unit?.let {
                mBinding.txtUnit.text = it
            }

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener(object : OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener?.onClick(v!!, adapterPosition, advertisement)
                    }
                })
                mBinding.txtDelete.setOnClickListener(object : OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener?.onClick(v!!, adapterPosition, advertisement)
                    }
                })
            }
        }
    }
}