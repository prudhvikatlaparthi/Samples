package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentBusinessOwnerSearchBinding
import com.sgs.citytax.model.CRMAgents
import com.sgs.citytax.ui.adapter.AgentSearchAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class AgentSearchFragment : BaseFragment(), IClickListener, View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var mBinding: FragmentBusinessOwnerSearchBinding
    private var mListener: Listener? = null
    var pageIndex: Int = 1
    val pageSize: Int = 100
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var searchView: SearchView? = null

    private lateinit var mAdapter: AgentSearchAdapter

    override fun initComponents() {
        setViews()
        setListeners()
    }

    private fun setViews() {
        mBinding.llSearch.visibility = View.GONE
        mBinding.viewSearch.visibility = View.GONE
        mBinding.rcvOwnerSearchResult.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = AgentSearchAdapter(this)
        mBinding.rcvOwnerSearchResult.adapter = mAdapter
        getAgentSearchResult("")
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
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_search_incidents, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = (menu.findItem(R.id.action_search)?.actionView as SearchView)

        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        }
        searchView?.setOnQueryTextListener(this)
        menu.findItem(R.id.action_filter).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (mBinding.rcvOwnerSearchResult.adapter != null)
            (mBinding.rcvOwnerSearchResult.adapter as AgentSearchAdapter).filter.filter(query)
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_owner_search, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setListeners() {
        mBinding.btnSearch.setOnClickListener(this)
        mBinding.rcvOwnerSearchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                    val data: String = mBinding.edtownersearch.text.toString()
                    mListener?.hideKeyBoard()
                    if (validateView()) {
                        mAdapter.clear()
                        getAgentSearchResult(data)
                    }

                }
            }
        })

    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSearch) {
            val data: String = mBinding.edtownersearch.text.toString()
            mListener?.hideKeyBoard()
            if (validateView()) {
                mAdapter.clear()
                getAgentSearchResult(data)
            }
        }

    }

    fun onBackPressed() {
        mListener?.finish()
    }

    private fun getAgentSearchResult(data: String) {
        val crmAgent = CRMAgents()
        crmAgent.isLoading = true
        mAdapter.add(crmAgent)
        isLoading = true

        APICall.getChildAgentsForVerification(data, object : ConnectionCallBack<List<CRMAgents>> {
            override fun onSuccess(response: List<CRMAgents>) {
                if (response.isNotEmpty()) {
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
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                mAdapter.remove(crmAgent)
                isLoading = false
            }
        })
    }

    private fun validateView(): Boolean {

        if (mBinding.edtownersearch.text.toString() != null && TextUtils.isEmpty(mBinding.edtownersearch.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.error_message))
            return false
        }
        return true
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    interface Listener {
        fun finish()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
//        mBinding.edtownersearch.setText("")
//        mAdapter.clear()
        val fragment = AgentEntryFragment()

        //region SetArguments
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT)
        bundle.putParcelable(Constant.KEY_AGENT, obj as CRMAgents?)
        fragment.arguments = bundle
        //endregion
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_AGENT_VERIFICATION)
        mListener?.addFragment(fragment, true)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_AGENT_VERIFICATION) {
                setViews()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}