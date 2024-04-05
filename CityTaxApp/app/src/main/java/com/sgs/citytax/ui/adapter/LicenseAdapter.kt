package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemLicenseBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.LicenseDetails
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class LicenseAdapter(val listener: IClickListener, val screenMode: Constant.ScreenMode?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mLicenses: ArrayList<LicenseDetails> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1
    private val binderHelper = ViewBinderHelper()

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = LicenseViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_license, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val licenseDetails: LicenseDetails = mLicenses[position]
        when (getItemViewType(position)) {
            mItem -> {
                binderHelper.bind((holder as LicenseViewHolder).mBinding.swipeLayout, position.toString())
                binderHelper.closeAll()
                (holder as LicenseViewHolder).bind(licenseDetails, listener, screenMode)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(licenseDetails)
            }
        }
    }

    override fun getItemCount(): Int {
        return mLicenses.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mLicenses[position].isLoading)
            mLoading
        else mItem
    }

    class LicenseViewHolder(val mBinding: ItemLicenseBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(licenseDetails: LicenseDetails, iClickListener: IClickListener?, screenMode: Constant.ScreenMode?) {
            licenseDetails.licenseNo?.let {
                mBinding.txtLicenseNumber.text = it
            }
            licenseDetails.licenseCategory?.let {
                mBinding.txtLicenseCategory.text = it
            }

            licenseDetails.estimatedTaxAmount?.let {
                mBinding.txtEstimatedAmount.text = formatWithPrecision(it)
            }

            licenseDetails.status?.let {
                mBinding.txtStatus.text = it
            }

            licenseDetails.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }

            if (screenMode == Constant.ScreenMode.VIEW) {
                mBinding.txtDelete.visibility = View.GONE
            }else{
                if(licenseDetails.renewPending=="N"){
                    mBinding.txtDelete.visibility = View.GONE
                }
            }

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, licenseDetails)
                    }
                })
                mBinding.txtDelete.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, licenseDetails)
                    }
                })

            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(details: LicenseDetails) {
            if (details.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(licenseDetails: LicenseDetails?) {
        mLicenses.add(licenseDetails!!)
        notifyItemInserted(mLicenses.size - 1)
    }

    fun addAll(licenseDetails: List<LicenseDetails?>) {
        for (mLicense in licenseDetails) {
            add(mLicense)
        }
    }

    fun remove(licenseDetails: LicenseDetails?) {
        val position: Int = mLicenses.indexOf(licenseDetails)
        if (position > -1) {
            mLicenses.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<LicenseDetails> {
        return mLicenses
    }

    fun clear() {
        mLicenses = arrayListOf()
        notifyDataSetChanged()
    }


}