package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemRopPdoBinding
import com.sgs.citytax.model.ROPListItem
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class ROPPDOAdapter(iClickListener: IClickListener, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<ROPPDOAdapter.MasterListViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mClickListener: IClickListener? = iClickListener
    private var mArrayList: ArrayList<ROPListItem> = arrayListOf()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterListViewHolder {
        return MasterListViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_rop_pdo, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: MasterListViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<ROPListItem>) {
        for (item: ROPListItem in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList.clear()
        notifyDataSetChanged()
    }

    class MasterListViewHolder(var binding: ItemRopPdoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ROPListItem, iClickListener: IClickListener?) {
            binding.llStartDate.visibility = GONE
            binding.tvOccupancy.text = item.occupancyName
            binding.tvTaxableMatter.text = formatWithPrecisionCustomDecimals(item.taxableMatter.toString(), false, 3)
            binding.tvActive.text = if (item.act.equals("Y"))  binding.tvActive?.context?.resources?.getString(R.string.yes) else binding.tvOccupancy?.context?.resources?.getString(R.string.no)
            if(item.allowDelete=="N") {
                binding.txtDelete.visibility = View.GONE
            }
            item.startDate?.let {
                binding.tvStartDate.text = displayFormatDate(it)
                binding.llStartDate.visibility = VISIBLE
            }
            binding.llEstimatedAmount.visibility = item.estimatedTax?.let {
                binding.tvEstimatedAmount.text = formatWithPrecision(it)
                VISIBLE
            } ?: GONE
            binding.llMarket.visibility = GONE
            item.market?.let {
                binding.llMarket.visibility = VISIBLE
                binding.tvMarket.text =item.market
            }
            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, item)
                    }
                })
                binding.txtDelete.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, item)
                    }
                })
            }
        }

    }
}