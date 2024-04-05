package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowPendingLicensesBinding
import com.sgs.citytax.model.PendingLicenses4Agent
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDate
import com.sgs.citytax.util.formatWithPrecision

class PendingLicensesMasterAdapter(iClickListener: IClickListener) : RecyclerView.Adapter<PendingLicensesMasterAdapter.ViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.row_pending_licenses, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /*fun update(list: List<PendingLicenses4Agent>) {
        for (item: PendingLicenses4Agent in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }*/

    class ViewHolder(var binding: RowPendingLicensesBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(details: PendingLicenses4Agent, iClickListener: IClickListener?) {
            binding.tvBusiness.text = details.accountName
            binding.tvSycoTaxID.text = details.sycoTaxID
            binding.tvLicenseNumber.text = details.licenseNumber
            binding.tvPendingCurrentInvoice.text = details.renewPending
            binding.tvValidFrom.text = formatDate(details.validFromDate)
            binding.tvValidUpTo.text = formatDate(details.validToDate)
            binding.tvCurrentDue.text = formatWithPrecision(details.currentDue)


            if (iClickListener != null) {
                binding.llContainer.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, details)
                }

            }

        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<PendingLicenses4Agent>() {
        override fun areItemsTheSame(oldItem: PendingLicenses4Agent, newItem: PendingLicenses4Agent): Boolean {
            return (oldItem.licenseNumber + oldItem.accountId) == (newItem.licenseNumber + newItem.accountId)
        }

        override fun areContentsTheSame(oldItem: PendingLicenses4Agent, newItem: PendingLicenses4Agent): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}
