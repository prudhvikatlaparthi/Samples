package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAgentMasterBinding
import com.sgs.citytax.model.CRMAgents
import com.sgs.citytax.ui.adapter.AgentListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class AgentMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentAgentMasterBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT

    private val prefHelper = MyApplication.getPrefHelper()
    private lateinit var mAdapter: AgentListAdapter
    var pageIndex: Int = 1
    val pageSize: Int = 100
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        //endregion
        setViews()
        bindData()
        setListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_agent_master, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        setHasOptionsMenu(true)
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = AgentListAdapter(this@AgentMasterFragment, Constant.NavigationMenu.NAVIGATION_MENU_AGENTS)
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun bindData(itemId: Int? = R.id.item_acive) {
        // region Filters
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

        if (itemId == R.id.item_acive) {
            filterColumn.columnName = "StatusCode"
            filterColumn.columnValue = "CRM_Agents.Active"
            filterColumn.srchType = "equal"
        } else {
            filterColumn.columnName = "StatusCode"
            filterColumn.columnValue = "CRM_Agents.Inactive"
            filterColumn.srchType = "like"
        }
        listFilterColumn.add(listFilterColumn.size, filterColumn)

        searchFilter.filterColumns = listFilterColumn

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = ""
        tableDetails.primaryKeyColumnName = ""
        tableDetails.selectColoumns = ""

        searchFilter.tableDetails = tableDetails
        // endregion

        val crmAgent = CRMAgents()
        crmAgent.isLoading = true
        mAdapter.add(crmAgent)
        isLoading = true

        APICall.getAgentsByAgentType(searchFilter, object : ConnectionCallBack<List<CRMAgents>> {
            override fun onSuccess(response: List<CRMAgents>) {

                val count: Int = response.size
                if (count < pageSize) {
                    hasMoreData = false
                } else
                    pageIndex += 1

                if (pageIndex == 1 && response.isEmpty())
                    mAdapter.clear()
                mAdapter.remove(crmAgent)
                mAdapter.addAll(response)

                if (count == 0)
                    Toast.makeText(context, R.string.msg_no_data, Toast.LENGTH_SHORT).show()
                isLoading = false
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                mAdapter.remove(crmAgent)
                isLoading = false
            }
        })
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = AgentEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this@AgentMasterFragment, Constant.REQUEST_CODE_AGENT_ON_BOARD)
                mListener?.addFragment(fragment, true)
            }
        })

    }


    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.txtEdit -> {
                val fragment = AgentEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putParcelable(Constant.KEY_AGENT, obj as CRMAgents?)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_AGENT_ON_BOARD)
                mListener?.addFragment(fragment, true)
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_acive -> {
                mAdapter.clear()
                bindData(R.id.item_acive)
                true
            }
            R.id.item_inactive -> {
                mAdapter.clear()
                bindData(R.id.item_inactive)
                true
            }else->{
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun onBackPressed() {
        mListener?.finish()
    }

    interface Listener {
        fun finish()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
    }

}
