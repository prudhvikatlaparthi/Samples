package com.sgs.citytax.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemAgentSearchBinding
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.model.CRMAgents
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener
import java.util.*
import kotlin.collections.ArrayList

class AgentSearchAdapter(var iClickListener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private val binderHelper = ViewBinderHelper()
    private val mItem = 0
    private val mLoading = 1
    private var crmAgents: ArrayList<CRMAgents> = arrayListOf()
    private lateinit var context: Context
    private var filteredList: List<CRMAgents> = arrayListOf()
    init {
        binderHelper.setOpenOnlyOne(true)
        filteredList = crmAgents
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        context = parent.context
        when (viewType) {
            mItem -> {
                viewHolder = AgentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_agent_search, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        binderHelper.lockSwipe(position.toString())
        val agents = filteredList[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as AgentViewHolder).bind(agents, iClickListener)

            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(agents)
            }
        }
    }

    class AgentViewHolder(var binding: ItemAgentSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crmAgent: CRMAgents, iClickListener: IClickListener?) {
            binding.llEmail.visibility = View.GONE
            binding.tvAgentName.text = "${crmAgent.agentName}"
            binding.tvMobileNo.text = "${crmAgent.mobileNo}"
            crmAgent.email?.let {
                binding.tvEmail.text = "${crmAgent.email}"
                binding.llEmail.visibility = View.VISIBLE
            }
            binding.tvAgentType.text = "${crmAgent.AgentType}"
            crmAgent.agentCode?.let {
                binding.llAgentCode.visibility = View.VISIBLE
                binding.tvAgentCode.text = crmAgent.agentCode
            }
            binding.tvStatus.text = "${crmAgent.Status}"

            if (iClickListener != null) {
                binding.container.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, crmAgent)
                }
            }
        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(crmAgent: CRMAgents) {
            if (crmAgent.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (crmAgents[position].isLoading)
            mLoading
        else mItem
    }

    fun add(crmAgent: CRMAgents) {
        crmAgents.add(crmAgent)
        notifyItemInserted(crmAgents.size - 1)

    }

    fun addAll(crmAgent: List<CRMAgents>) {
        for (agent in crmAgent) {
            add(agent)
        }
    }

    fun clear() {
        crmAgents.clear()
        notifyDataSetChanged()
    }

    fun remove(agents: CRMAgents?) {
        val position: Int = crmAgents.indexOf(agents)
        if (position > -1) {
            crmAgents.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                filteredList = if (TextUtils.isEmpty(constraint)) {
                    crmAgents
                } else {
                    val newFilteredList: MutableList<CRMAgents> = ArrayList()
                    for (o in crmAgents) {
                        if (o.mobileNo.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                                || o.email.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                                || o.FirstName.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                                || o.MiddleName.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
                                || o.LastName.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                            newFilteredList.add(o)
                        }
                    }
                    newFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                filterResults.count = filteredList.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredList = results.values as List<CRMAgents>
                notifyDataSetChanged()
            }

        }
    }

}