package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemPendingPropertyListBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.PendingRequestList
import com.sgs.citytax.util.*

class PropertyPendingListAdapter(val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList: ArrayList<PendingRequestList> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = PendingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_pending_property_list, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val list: PendingRequestList = mList[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as PendingViewHolder).bind(list, listener)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(list)
            }
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].isLoading)
            mLoading
        else mItem
    }

    class PendingViewHolder(val mBinding: ItemPendingPropertyListBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(list: PendingRequestList, iClickListener: IClickListener?) {
            list.propertyName?.let {
                mBinding.txtName.text = it
            }
            list.ownerName?.let {
                mBinding.txtOwnerName.text = it
            }
            list.status?.let {
                mBinding.txtStatus.text = it
            }
           /* list.taxRuleBookCode?.let {
                if(list.taxRuleBookCode== Constant.TaxRuleBook.COM_PROP.Code)
                mBinding.txtPropertyType.text = mBinding.txtPropertyType.context.getString(R.string.txt_commercial)

                if(list.taxRuleBookCode== Constant.TaxRuleBook.RES_PROP.Code)
                    mBinding.txtPropertyType.text = mBinding.txtPropertyType.context.getString(R.string.txt_residential)

                if(list.taxRuleBookCode== Constant.TaxRuleBook.LAND_PROP.Code)
                    mBinding.txtPropertyType.text = mBinding.txtPropertyType.context.getString(R.string.txt_land)
            }*/

            list.propertyType?.let {
                    mBinding.txtPropertyType.text = list.propertyType
            }
            list.propertyVerificationRequestId?.let {
                mBinding.txtRequestID.text = it.toString()
            }
            list.propertyVerificationReqDate?.let {
                mBinding.txtVerificationRequestDate.text = displayFormatDate(getDate(it, DateFormat, DateTimeTimeZoneMillisecondFormat))
            }
            list.sycoTaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }

            if (iClickListener != null) {
                mBinding.llRootView.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, list)
                }
            }

        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: PendingRequestList) {
            if (list.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(list: PendingRequestList?) {
        mList.add(list!!)
        notifyItemInserted(mList.size - 1)
    }

    fun addAll(pendingList: List<PendingRequestList?>) {
        for (mPendingList in pendingList) {
            add(mPendingList)
        }
    }

    fun remove(mPendingList: PendingRequestList?) {
        val position: Int = mList.indexOf(mPendingList)
        if (position > -1) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<PendingRequestList> {
        return mList
    }

    fun clear() {
        mList = arrayListOf()
        notifyDataSetChanged()
    }

}