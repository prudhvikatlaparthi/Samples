package com.sgs.citytax.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityMyAgentsBinding
import com.sgs.citytax.model.CRMAgents
import com.sgs.citytax.ui.adapter.AgentListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener


class MyAgentsActivity : BaseActivity(), IClickListener {

    private lateinit var binding: ActivityMyAgentsBinding

    var pageIndex: Int = 1
    val pageSize: Int = 100
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var agentTypeList: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showToolbarBackButton(R.string.title_my_agents)
        if (prefHelper.superiorTo.trim().isNotEmpty())
            agentTypeList = prefHelper.superiorTo.split(",").toTypedArray()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_agents)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding.recyclerView.adapter = AgentListAdapter(this@MyAgentsActivity, Constant.NavigationMenu.NAVIGATION_MENU_MY_AGENTS)
        bindData("")
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    fun bindData(agentType: String) {
        val searchFilter = AdvanceSearchFilter()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex
        searchFilter.query = null

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()

        var filterColumn = FilterColumn()
        if (prefHelper.agentTypeCode == Constant.AgentTypeCode.ASO.name)
            filterColumn.columnName = "AssociationID"
        else filterColumn.columnName = "ParentAgentID"
        filterColumn.columnValue = prefHelper.agentID.toString()
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)

        filterColumn = FilterColumn()
        filterColumn.columnName = "StatusCode"
        filterColumn.columnValue = "CRM_Agents.Active"
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)

        filterColumn = FilterColumn()
        if (agentType != "") {
            filterColumn.columnName = "AgentType"
            filterColumn.columnValue = agentType
            filterColumn.srchType = "equal"

            listFilterColumn.add(listFilterColumn.size, filterColumn)
        }

        searchFilter.filterColumns = listFilterColumn

        var tableDetails = TableDetails()
        tableDetails.tableOrViewName = ""
        tableDetails.primaryKeyColumnName = ""
        tableDetails.selectColoumns = ""

        searchFilter.tableDetails = tableDetails

        val crmAgent = CRMAgents()
        crmAgent.isLoading = true
        (binding.recyclerView.adapter as AgentListAdapter).add(crmAgent)
        isLoading = true

        APICall.getAgentsByAgentType(searchFilter, object : ConnectionCallBack<List<CRMAgents>> {
            override fun onSuccess(response: List<CRMAgents>) {
                val count: Int = response.size
                if (count < pageSize) {
                    hasMoreData = false
                } else
                    pageIndex += 1

                if (pageIndex == 1 && response.isEmpty())
                    (binding.recyclerView.adapter as AgentListAdapter).clear()
                (binding.recyclerView.adapter as AgentListAdapter).remove(crmAgent)
                (binding.recyclerView.adapter as AgentListAdapter).addAll(response)

                if (count == 0)
                    Toast.makeText(this@MyAgentsActivity, R.string.msg_no_data, Toast.LENGTH_SHORT).show()
                isLoading = false
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
                (binding.recyclerView.adapter as AgentListAdapter).remove(crmAgent)
                isLoading = false
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear();
        var i: Int = 0

        when (prefHelper.agentTypeCode) {
            //Constant.AgentTypeCode.ISP.name -> menu?.add(0, Menu.NONE, 0, this.resources.getString(R.string.supervisor))
            else -> {
                for (s in agentTypeList) {
                    menu?.add(0, Menu.NONE, i, s)
                    i++
                }
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
        }

        var i = 0
        for (itemId in agentTypeList) {
            if (id == i) {
                bindData(item.title.toString())
                break
            } else {
                i++
            }
        }
        (binding.recyclerView.adapter as AgentListAdapter).clear()
        pageIndex = 1
        hasMoreData = true

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View, position: Int, obj: Any) {

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }
}
