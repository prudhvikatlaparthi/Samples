package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.ItemReturnImpoundmentListBinding
import com.sgs.citytax.model.PropertyTax4Business
import com.sgs.citytax.util.*

class ReturnImpoundmentListAdapter(val listener: Listener, val from: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mImpondmentReturnList: ArrayList<ImpondmentReturn> = arrayListOf()
    private val mItem = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_return_impoundment_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mList = mImpondmentReturnList[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as ViewHolder).bind(mList, listener, from, position)
            }

        }
    }

    override fun getItemCount(): Int {
        return mImpondmentReturnList.size
    }




    class ViewHolder(val mBinding: ItemReturnImpoundmentListBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(mImpondmentReturn: ImpondmentReturn, listener: Listener, from: String, position: Int) {
            if (from == "1") {
                mImpondmentReturn?.let {
                    mBinding.tvImpoundment.text = it.impoundmentType
                    if (it.applicableOnVehicle == "Y") {
                        mBinding.llVoucherno.visibility = View.VISIBLE
                        mBinding.tvVehicleNumber.text = it.vehicleNo
                        mBinding.tvOwner.text = it.vehicleOwner
                        mBinding.tvPhoneNumber.text = it.vehicleOwnerMobile

                    } else {
                        mBinding.llVoucherno.visibility = View.GONE
                        mBinding.tvVehicleNumber.text = ""
                        mBinding.tvOwner.text = it.goodsOwner
                        mBinding.tvPhoneNumber.text = it.goodsOwnerMobile
                    }
                    mBinding.tvTotalDue.text = formatWithPrecision(it.currentDue)

                    if (it.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code)
                    {
                        mBinding.llOwner.visibility = View.GONE
                        mBinding.llPhone.visibility = View.GONE
                        mBinding.llImpoundQty.visibility = View.VISIBLE
                        mBinding.llReturnQty.visibility = View.VISIBLE
                        mBinding.tvImpoundqty.text = getQuantity(it.quantity.toString())
                        mBinding.tvTotalDue.text = formatWithPrecision(it.currentDue)
                        if (it.pendingReturnQuantity == null)
                        mBinding.tvReturnQty.text = "0.0"
                        else
                        mBinding.tvReturnQty.text = getQuantity(it.pendingReturnQuantity.toString())
                    }
                }
            }
            else if (from == "2")
            {
                mBinding.llReturn.visibility = View.GONE
                mBinding.llViolation.visibility = View.VISIBLE

                mBinding.tvDate. text = formatDisplayDateTimeInMillisecond(mImpondmentReturn.transactiondate)
                mBinding.tvViolationClass. text =mImpondmentReturn.violationClass
                mBinding.tvViolationType. text =mImpondmentReturn.violationType
                mBinding.tvViolationDetails. text =mImpondmentReturn.violationDetails
               // mBinding.tvFineAmount. text = formatWithPrecision(mImpondmentReturn.currentDue)
                mBinding.tvFineAmount. text = formatWithPrecision(mImpondmentReturn.fineAmount)
            }
            mBinding.llRootView.setOnClickListener {
                listener.onItemClick(mImpondmentReturn, position)
            }

        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetList: ImpondmentReturn) {
            if (assetList.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(list: ImpondmentReturn?) {
        mImpondmentReturnList.add(list!!)
        notifyItemInserted(mImpondmentReturnList.size - 1)
    }

    fun addAll(mImpondmentReturnList: List<ImpondmentReturn?>) {
        for (mList in mImpondmentReturnList) {
            add(mList)
        }
    }

    fun remove(mList: ImpondmentReturn?) {
        val position: Int = mImpondmentReturnList.indexOf(mList)
        if (position > -1) {
            mImpondmentReturnList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<ImpondmentReturn> {
        return mImpondmentReturnList
    }

    fun clear() {
        mImpondmentReturnList = arrayListOf()
        notifyDataSetChanged()
    }
    fun update(list: List<ImpondmentReturn>) {
            mImpondmentReturnList.addAll(list)
        notifyDataSetChanged()
    }


    interface Listener {
        fun onItemClick(list: ImpondmentReturn, position: Int)
    }
}