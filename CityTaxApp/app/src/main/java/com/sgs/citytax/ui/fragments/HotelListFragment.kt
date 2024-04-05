package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.HotelDetailsListResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.HotelDetails
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.ui.adapter.HotelListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class HotelListFragment : BaseFragment(), View.OnClickListener, IClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null

    private var fromScreen: Constant.QuickMenu? = null
    private var isMultiple = false
    private var mTaskCode: String? = ""
    private var mNoOfHotels: Int = 0
    private var adapter: HotelListAdapter? = null
    private var hotelDetails: ArrayList<HotelDetails> = arrayListOf()
    private var mTaxRuleBookCode: String? = ""


    var pageIndex: Int = 1
    val pageSize: Int = 50
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            arguments?.getParcelable<TaskCode>(Constant.KEY_TASK_CODE).apply {
                this?.IsMultiple?.let {
                    isMultiple = it == 'Y' || it == ' '
                }
                this?.taskCode?.let {
                    mTaskCode = it
                }
            }
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE)!!
        }
        setViews()
        bindData()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_HOTELS) {
                adapter?.clear()
                bindData()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = HotelListAdapter(this, mListener?.screenMode)
        mBinding.recyclerView.adapter = adapter
    }

    private fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId) {
            mListener?.showProgressDialog()

            val details = HotelDetails()
            details.isLoading = true
            adapter?.add(details)
            isLoading = true

            APICall.getHotelDetails(ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId, pageSize, pageIndex, object : ConnectionCallBack<HotelDetailsListResponse> {
                override fun onSuccess(response: HotelDetailsListResponse) {
                    mListener?.dismissDialog()
                    if (response.hotelDetailsTable?.hotelDetails != null && response.hotelDetailsTable?.hotelDetails!!.isNotEmpty()) {
                        hotelDetails = response.hotelDetailsTable?.hotelDetails!!
                        val count: Int = hotelDetails.size
                        if (count < pageSize) {
                            hasMoreData = false
                        } else
                            pageIndex += 1
                        adapter?.remove(details)
                        adapter!!.addAll(hotelDetails)
                        isLoading = false

                        mNoOfHotels = hotelDetails.size

                        // todo commented for onsite requirement
                        /*if (!isMultiple && hotelDetails.size > 0)
                            mBinding.fabAdd.visibility = View.GONE
                        else
                            mBinding.fabAdd.visibility = View.VISIBLE

                        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
                            mBinding.fabAdd.visibility = View.GONE
                        }*/
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                        {
                            mBinding.fabAdd.visibility = View.GONE
                        }
                    } else {
                        adapter?.remove(details)
                        isLoading = false
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    adapter?.remove(details)
                    isLoading = false
                    mNoOfHotels = hotelDetails.size
                    mBinding.fabAdd.visibility = View.VISIBLE

                    // todo commented for onsite requirement
                    /*if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
                        mBinding.fabAdd.visibility = View.GONE
                    }*/

                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(this)
        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.txtEdit -> {
                    showHotelEntryScreen(obj as HotelDetails)
                }

                R.id.txtDelete -> {
                    val hotel = obj as HotelDetails
                    deleteHotel(hotel.hotelId ?: 0)
                }
            }
        }

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.fabAdd -> {
                    showHotelEntryScreen(null)
                }
            }
        }
    }

    private fun showHotelEntryScreen(hotelDetails: HotelDetails?) {
        val fragment = HotelEntryFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_HOTEL, hotelDetails)
        bundle.putInt(Constant.KEY_NUMBER_OF_HOTELS, mNoOfHotels)
        bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
        if (hotelDetails == null){
            mListener?.screenMode = Constant.ScreenMode.ADD
        }else{
            bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        }
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_HOTELS)
        mListener?.showToolbarBackButton(R.string.title_hotels)
        mListener?.addFragment(fragment, true)
    }

    private fun deleteHotel(hotelID: Int) {
        mListener?.showProgressDialog()
        APICall.deleteHotel(hotelID, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                adapter?.clear()
                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }


    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showToolbarBackButton(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }

}