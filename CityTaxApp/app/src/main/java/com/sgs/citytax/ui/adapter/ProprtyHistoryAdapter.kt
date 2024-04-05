package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.VuComPropertyMaster
import com.sgs.citytax.databinding.ItemPropertyHistoryBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener

//class ProprtyHistoryAdapter(var propertyList: ArrayList<VuComPropertyMaster>) : RecyclerView.Adapter<ProprtyHistoryAdapter.NotesViewHolder>() {
class ProprtyHistoryAdapter(val listener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPropertyList: ArrayList<VuComPropertyMaster> = arrayListOf()
    private var itemClickListener: IClickListener? = listener
    private val mItem = 0

    private val mBinderHelper = ViewBinderHelper()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    class ViewHolder(var binding: ItemPropertyHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mVuComPropertyMaster: VuComPropertyMaster) {
            binding.tvPropetyType.text = mVuComPropertyMaster.propertyName
            binding.tvPropertyName.text = mVuComPropertyMaster.propertyType
            binding.tvSurveyNumber.text = mVuComPropertyMaster.surveyNo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_property_history, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return mPropertyList.size
    }

    override fun getItemViewType(position: Int): Int {
        return mItem
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transaction = mPropertyList[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as ViewHolder).bind(transaction)
            }
        }
    }

    fun add(transaction: VuComPropertyMaster) {
        mPropertyList.add(transaction)
        notifyItemInserted(mPropertyList.size - 1)
    }

    fun addAll(transactions: List<VuComPropertyMaster>) {
        for (transaction in transactions) {
            add(transaction)
        }
    }

    fun remove(transaction: VuComPropertyMaster?) {
        val position: Int = mPropertyList.indexOf(transaction)
        if (position > -1) {
            mPropertyList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}