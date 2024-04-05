package com.sgs.citytax.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.SearchFilter
import com.sgs.citytax.databinding.FragmentSummaryDetailsBinding
import com.sgs.citytax.model.CRMAgentSummaryDetails
import com.sgs.citytax.ui.AgentSummaryDetailsActivity
import com.sgs.citytax.ui.CollectionHistoryActivity
import com.sgs.citytax.ui.IncidentActivity
import com.sgs.citytax.ui.adapter.SummaryDetailsListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.LogHelper

class AgentSummaryDetailsFragment : BaseFragment(), IClickListener {

    var rootView: View? = null
    private var agentSummaryDetailsActivity: AgentSummaryDetailsActivity? = null
    private lateinit var binding: FragmentSummaryDetailsBinding
    var crmSummaryAgents: ArrayList<CRMAgentSummaryDetails>? = arrayListOf()
    private var fromScreen: Constant.QuickMenu? = null

    private var summaryDetailsListAdapter: SummaryDetailsListAdapter? = null

    var pageIndex: Int = 1
    val pageSize: Int = 20
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(fromScreen: Constant.QuickMenu?) = AgentSummaryDetailsFragment().apply {
            fromScreen?.let {
                this.fromScreen = fromScreen
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        agentSummaryDetailsActivity = activity as AgentSummaryDetailsActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_summary_details, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    override fun initComponents() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(agentSummaryDetailsActivity!!, LinearLayoutManager.VERTICAL))
        summaryDetailsListAdapter = SummaryDetailsListAdapter(iClickListener = this@AgentSummaryDetailsFragment)
        binding.recyclerView.adapter = summaryDetailsListAdapter
        bindData()
        initializeListeners()
    }

    fun bindData() {
        val searchFilter = SearchFilter()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex

        val summaryDetails = CRMAgentSummaryDetails()
        summaryDetails.isLoading = true
        summaryDetailsListAdapter?.add(summaryDetails)
        notifyAdapter()
        isLoading = true

        APICall.getSummaryDetails(searchFilter, object : ConnectionCallBack<List<CRMAgentSummaryDetails>> {
            override fun onSuccess(response: List<CRMAgentSummaryDetails>) {
                try {
                    crmSummaryAgents = response as ArrayList<CRMAgentSummaryDetails>
                    val count: Int = crmSummaryAgents!!.size
                    if (count < pageSize) {
                        hasMoreData = false
                    } else
                        pageIndex += 1
                    summaryDetailsListAdapter?.remove(summaryDetails)
                    summaryDetailsListAdapter?.addAll(crmSummaryAgents!!)
                    notifyAdapter()
                    isLoading = false
                } catch (ex: Exception) {
                    LogHelper.writeLog(exception = ex)
                }
            }

            override fun onFailure(message: String) {
                agentSummaryDetailsActivity?.dismissDialog()
                //agentSummaryDetailsActivity?.showAlertDialog(message)
                summaryDetailsListAdapter?.remove(summaryDetails)
                notifyAdapter()
                isLoading = false
            }
        })
    }

    private fun notifyAdapter() {
        binding.recyclerView?.post {
            summaryDetailsListAdapter?.notifyDataSetChanged()
        }
    }

    private fun initializeListeners() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                    bindData()
                }
            }
        })

    }

    fun handleBackClick() {
        agentSummaryDetailsActivity?.finish()
        agentSummaryDetailsActivity?.popBackStack()
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.collection_button -> {
                val intent = Intent(agentSummaryDetailsActivity, CollectionHistoryActivity::class.java)
                intent.putExtra("mode", true)
                intent.putExtra("s_agent_acctid", (obj as CRMAgentSummaryDetails).acctid)
                startActivity(intent)
            }
            R.id.incidents_button -> {
                val intent = Intent(agentSummaryDetailsActivity, IncidentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                intent.putExtra("s_agent_acctid", (obj as CRMAgentSummaryDetails).acctid)
                startActivity(intent)
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

}
