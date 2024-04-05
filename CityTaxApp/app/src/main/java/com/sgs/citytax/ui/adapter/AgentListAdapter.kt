package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemAgentBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.CRMAgents
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener


class AgentListAdapter(iClickListener: IClickListener, private val fromScreen: Constant.NavigationMenu) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val binderHelper = ViewBinderHelper()
    private var iClickListener: IClickListener? = iClickListener
    private val mItem = 0
    private val mLoading = 1
    private var mCrmAgents: ArrayList<CRMAgents> = arrayListOf()
    private lateinit var context: Context

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        context = parent.context
        when (viewType) {
            mItem -> {
                viewHolder = AgentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_agent, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val crmAgent: CRMAgents = mCrmAgents[position]
        when (getItemViewType(position)) {
            mItem -> {

                if (fromScreen == Constant.NavigationMenu.NAVIGATION_MENU_MY_AGENTS)
                    binderHelper.lockSwipe(position.toString())

                binderHelper.bind((holder as AgentViewHolder).binding.swipeLayout, position.toString())
                binderHelper.closeAll()
                holder.bind(crmAgent, iClickListener)
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


    class AgentViewHolder(var binding: ItemAgentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crmAgent: CRMAgents, iClickListener: IClickListener?) {
            binding.llEmail.visibility = GONE

            binding.tvAgentName.text = "${crmAgent.FirstName} ${crmAgent.MiddleName} ${crmAgent.LastName}"
            binding.tvMobileNo.text = "${crmAgent.mobileNo}"
            crmAgent.email?.let {
                binding.tvEmail.text = "${crmAgent.email}"
                binding.llEmail.visibility = VISIBLE
            }
            binding.tvAgentType.text = "${crmAgent.AgentType}"

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, crmAgent)
                }
                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, crmAgent)
                }
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crmAgent: CRMAgents) {
            if (crmAgent.isLoading)
                binding.loadMoreProgress.visibility = VISIBLE
            else binding.loadMoreProgress.visibility = GONE
        }
    }


    fun add(crmAgent: CRMAgents?) {
        mCrmAgents.add(crmAgent!!)
        notifyItemInserted(mCrmAgents.size - 1)
    }

    fun addAll(crmAgentList: List<CRMAgents?>) {
        for (agent in crmAgentList) {
            add(agent)
        }
    }

    fun clear() {
        mCrmAgents.clear()
        notifyDataSetChanged()
    }

    fun remove(crmAgent: CRMAgents?) {
        val position: Int = mCrmAgents.indexOf(crmAgent)
        if (position > -1) {
            mCrmAgents.removeAt(position)
            notifyItemRemoved(position)
        }
    }


}