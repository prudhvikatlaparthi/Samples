package com.sgs.citytax.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.ItemSummaryDetailsBinding
import com.sgs.citytax.model.CRMAgentSummaryDetails
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision


class SummaryDetailsListAdapter(iClickListener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var iClickListener: IClickListener? = iClickListener
    private val mItem = 0
    private val mLoading = 1
    private var mCrmAgents: MutableList<CRMAgentSummaryDetails> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = SummaryDetailsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_summary_details, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val crmAgent: CRMAgentSummaryDetails = mCrmAgents[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as SummaryDetailsViewHolder).bind(crmAgent, iClickListener)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(crmAgent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mCrmAgents.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mCrmAgents[position].isLoading)
            mLoading
        else mItem
    }

    class SummaryDetailsViewHolder(var binding: ItemSummaryDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crmAgent: CRMAgentSummaryDetails, iClickListener: IClickListener?) {
            binding.tvAgentName.text = crmAgent.agentName
            binding.tvAgentType.text = crmAgent.agentType

            binding.llMobileNo.visibility = GONE
            binding.llEmail.visibility = GONE
            binding.llCashLimit.visibility = GONE
            binding.llCreditBalance.visibility = GONE

            crmAgent.mob?.let {
                if (!TextUtils.isEmpty(it.trim())) {
                    binding.tvMobileNo.text = it
                    binding.llMobileNo.visibility = VISIBLE
                }
            }

            crmAgent.email?.let { it ->
                if (!TextUtils.isEmpty(it.trim())) {
                    binding.tvEmail.text = it
                    binding.llEmail.visibility = VISIBLE
                }
            }

            if ("Y" == crmAgent.isPrepaid) {
                binding.llCreditBalance.visibility = VISIBLE
                binding.tvCreditBalance.text = formatWithPrecision(crmAgent.creditBalance)
            } else {
                binding.llCashLimit.visibility = VISIBLE
                binding.tvCashLimit.text = formatWithPrecision(crmAgent.capAmount)
            }

            if (iClickListener != null) {
                binding.collectionButton.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, crmAgent)
                }
                binding.incidentsButton.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, crmAgent)
                }
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crmAgent: CRMAgentSummaryDetails) {
            if (crmAgent.isLoading)
                binding.loadMoreProgress.visibility = VISIBLE
            else binding.loadMoreProgress.visibility = GONE
        }
    }

    fun add(crmAgent: CRMAgentSummaryDetails?) {
        crmAgent?.let {
            mCrmAgents.add(crmAgent)
        }
    }

    fun addAll(crmAgentList: List<CRMAgentSummaryDetails>?) {
        if (crmAgentList == null) {
            mCrmAgents.clear()
        } else {
            mCrmAgents.addAll(crmAgentList)
        }
    }

    fun remove(crmAgent: CRMAgentSummaryDetails?) {
        val position: Int = mCrmAgents.indexOf(crmAgent)
        if (position > -1) {
            mCrmAgents.removeAt(position)
        }
    }

}