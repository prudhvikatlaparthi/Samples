package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GenericGetDetailsBySycotax
import com.sgs.citytax.api.payload.GetPendingPropertyVerificationRequests
import com.sgs.citytax.api.payload.PropertyVerificationData
import com.sgs.citytax.api.payload.StorePropertyData
import com.sgs.citytax.api.response.PropertyDetailsBySycoTax
import com.sgs.citytax.api.response.PropertyPendingVerificationResponse
import com.sgs.citytax.databinding.FragmentPropertyPendingListBinding
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.model.PendingRequestList
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.ui.adapter.PropertyPendingListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class PropertyPendingListFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentPropertyPendingListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var adapter: PropertyPendingListAdapter? = null
    private var mPendingLists: ArrayList<PendingRequestList> = arrayListOf()
    var pageIndex: Int = 1
    var pageSize: Int = 20
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    private var propertyDetails: StorePropertyData? = null
    private var address: ArrayList<GeoAddress>? = arrayListOf()

    private var propertyLocation: PropertyDetailLocation? = null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_pending_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        setViews()
        bindData()
        initialiseListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_PROPERTY_DOC_VERIFICATION && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                var isPhysicalVerificationPending = false
                var pendingList: PendingRequestList? = null
                if (data.hasExtra(Constant.KEY_IS_PHYSICAL_VERIFICATION_PENDING))
                    isPhysicalVerificationPending = data.getBooleanExtra(Constant.KEY_IS_PHYSICAL_VERIFICATION_PENDING, false)
                if (data.hasExtra(Constant.KEY_PENDING_PROPERTY_LIST))
                    pendingList = data.getParcelableExtra(Constant.KEY_PENDING_PROPERTY_LIST)

                if (isPhysicalVerificationPending) {
                    adapter?.clear()
                    bindData(isPhysicalVerificationPending, false, pendingList)
                } else {
                    adapter?.clear()
                    bindData(false, false, pendingList)
                }
            } else {
                adapter?.clear()
                bindData()
                showOptionSearch(true)
            }
        } else if (requestCode == Constant.REQUEST_CODE_PROPERTY_PHYSICAL_VERIFICATION && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                var isDocumentVerificationPending = false
                var pendingList: PendingRequestList? = null
                if (data.hasExtra(Constant.KEY_IS_DOC_VERIFICATION_PENDING))
                    isDocumentVerificationPending = data.getBooleanExtra(Constant.KEY_IS_DOC_VERIFICATION_PENDING, false)

                if (data.hasExtra(Constant.KEY_PENDING_PROPERTY_LIST))
                    pendingList = data.getParcelableExtra(Constant.KEY_PENDING_PROPERTY_LIST)

                if (isDocumentVerificationPending) {
                    adapter?.clear()
                    bindData(false, isDocumentVerificationPending, pendingList)

                } else {
                    adapter?.clear()
                    bindData(false, false, pendingList)
                }
            } else {
                adapter?.clear()
                bindData()
                showOptionSearch(true)
            }
        } else {
            adapter?.clear()
            bindData()
            showOptionSearch(true)
        }

    }

    private fun showOptionSearch(status:Boolean) {
        mListener?.showSearchOption(status)
    }

    fun setViews() {
        mBinding.rcvProperties.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = PropertyPendingListAdapter(this)
        mBinding.rcvProperties.adapter = adapter
    }

    private fun initialiseListeners() {
        mBinding.rcvProperties.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun bindData(isPhysicalVerificationPending: Boolean = false, isDocumentVerificationPending: Boolean = false, pendingList: PendingRequestList? = null) {
        mBinding.rcvProperties.visibility = View.GONE
        mListener?.showProgressDialog()
        val request = GetPendingPropertyVerificationRequests()

        var propertyVerificationData = PropertyVerificationData()
        propertyVerificationData.propertyVerificationRequestID = propertyLocation?.propertyVerificationRequestID
        propertyVerificationData.propertyTypeID = propertyLocation?.propertyID
        propertyVerificationData.propertySycotaxID = propertyLocation?.PropertySycotaxID
        propertyVerificationData.propertyOwner = propertyLocation?.owner
        propertyVerificationData.fromDate = propertyLocation?.fromDate
        propertyVerificationData.toDate = propertyLocation?.toDate

        request.pageIndex = pageIndex
        request.pageSize = pageSize
        request.data = propertyVerificationData

        val list = PendingRequestList()
        list.isLoading = false
        adapter?.add(list)
        isLoading = true

        APICall.getPendingPropertyList(request, object : ConnectionCallBack<PropertyPendingVerificationResponse> {
            override fun onSuccess(response: PropertyPendingVerificationResponse) {
                mBinding.rcvProperties.visibility = View.VISIBLE
                mListener?.dismissDialog()
                if (isPhysicalVerificationPending && pendingList != null) {
                    navigateToPhysicalVerificationScreen(pendingList, propertyDetails, address)
                } else if (isDocumentVerificationPending && pendingList != null) {
                    navigateToDocumentVerificationScreen(pendingList, propertyDetails, address)
                } else if (!isPhysicalVerificationPending
                        && !isDocumentVerificationPending
                        && pendingList != null && pendingList.isPhysicalVerified && pendingList.isDocumentVerified) {
                    navigateToReceiptScreen(pendingList)
                }
                if (response.verificationList?.requestList != null && response.verificationList?.requestList!!.isNotEmpty()) {
                    mPendingLists = response.verificationList?.requestList!!
                    val count: Int = mPendingLists.size
                    if (count < pageSize) {
                        hasMoreData = false
                    } else
                        pageIndex += 1
                    adapter?.remove(list)
                    adapter!!.addAll(mPendingLists)
                    isLoading = false
                } else {
                    adapter?.remove(list)
                    isLoading = false
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                adapter?.remove(list)
                isLoading = false
            }
        })
    }

    fun filterData(location: PropertyDetailLocation?) {
        propertyLocation = location
        //Log.e("sycotaxid frag selected",">>>>>>>>>${propertyLocation?.PropertySycotaxID}")
        //Log.e("fromDate frag selected",">>>>>>>>>${propertyLocation?.fromDate}")
        // Calling bindData with filtered list
        adapter?.clear()
        pageIndex=1
        bindData()

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.ll_root_view -> {
                    val pendingList = obj as PendingRequestList
                    searchPropertyDetailsBySycoTax(pendingList.sycoTaxId ?: "", pendingList)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun searchPropertyDetailsBySycoTax(sycoTaxID: String, pendingList: PendingRequestList) {
        val genericGetDetailsBySycotax = GenericGetDetailsBySycotax()
        genericGetDetailsBySycotax.sycoTaxId = sycoTaxID
        mListener?.showProgressDialog()
        APICall.searchPropertyDetailsBySycoTax(genericGetDetailsBySycotax, object : ConnectionCallBack<PropertyDetailsBySycoTax> {
            override fun onSuccess(response: PropertyDetailsBySycoTax) {
                mListener?.dismissDialog()
                propertyDetails = response.propertyDetails
                address = response.address as ArrayList<GeoAddress>
                navigateToVerificationScreens(pendingList, propertyDetails, address)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToVerificationScreens(pendingList: PendingRequestList, propertyDetails: StorePropertyData?, address: ArrayList<GeoAddress>?) {
        val documentVerificationStatusCode = pendingList.documentVerificationStatusCode
        val physicalVerificationStatusCode = pendingList.physicalVerificationStatusCode

        //region Navigation
        if (pendingList.allowPhysicalVerification == "Y" && pendingList.allowDocumentVerification == "Y") {
            if (documentVerificationStatusCode == Constant.PropertyStatusCode.Approved.code
                    && physicalVerificationStatusCode == Constant.PropertyStatusCode.Approved.code) {
                mListener?.showAlertDialog(getString(R.string.msg_already_aproved))
            } else if (documentVerificationStatusCode == Constant.PropertyStatusCode.Rejected.code
                    && physicalVerificationStatusCode == Constant.PropertyStatusCode.Rejected.code) {
                mListener?.showAlertDialog(getString(R.string.msg_already_rejected))
            } else if (documentVerificationStatusCode.isNullOrEmpty() && physicalVerificationStatusCode.isNullOrEmpty()) {
                pendingList.isDocumentVerified = false
                pendingList.isPhysicalVerified = false
                mListener?.showAlertDialog(R.string.msg_proceed_with,
                        R.string.document_verification,
                        View.OnClickListener {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                            navigateToDocumentVerificationScreen(pendingList, propertyDetails, address)
                        },
                        R.string.cancel,
                        View.OnClickListener {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                        },
                        R.string.physical_verification,
                        View.OnClickListener {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                            navigateToPhysicalVerificationScreen(pendingList, propertyDetails, address)
                        })
            } else if (!physicalVerificationStatusCode.isNullOrEmpty() && documentVerificationStatusCode.isNullOrEmpty()) {
                navigateToDocumentVerificationScreen(pendingList, propertyDetails, address)

            } else if (!documentVerificationStatusCode.isNullOrEmpty() && physicalVerificationStatusCode.isNullOrEmpty()) {
                navigateToPhysicalVerificationScreen(pendingList, propertyDetails, address)
            }
        } else if (pendingList.allowDocumentVerification == "Y" && pendingList.allowPhysicalVerification == "N") {
            if (documentVerificationStatusCode == Constant.PropertyStatusCode.Approved.code) {
                mListener?.showAlertDialog(getString(R.string.msg_already_aproved))
            } else {
                navigateToDocumentVerificationScreen(pendingList, propertyDetails, address)
            }
        } else if (pendingList.allowPhysicalVerification == "Y" && pendingList.allowDocumentVerification == "N") {
            if (physicalVerificationStatusCode == Constant.PropertyStatusCode.Approved.code) {
                mListener?.showAlertDialog(getString(R.string.msg_already_aproved))
            } else {
                navigateToPhysicalVerificationScreen(pendingList, propertyDetails, address)
            }
        }
        //endregion
    }

    private fun navigateToPhysicalVerificationScreen(pendingList: PendingRequestList, propertyDetails: StorePropertyData?, address: ArrayList<GeoAddress>?) {
        showOptionSearch(false)
        if (pendingList.taxRuleBookCode == Constant.TaxRuleBook.COM_PROP.Code || pendingList.taxRuleBookCode == Constant.TaxRuleBook.RES_PROP.Code) {
            val fragment = PropertyTaxEntryFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putInt(Constant.KEY_PRIMARY_KEY, pendingList.propertyId ?: 0)
            bundle.putParcelable(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
            bundle.putString(Constant.KEY_SYCO_TAX_ID, propertyDetails?.propertySycotaxID)
            bundle.putParcelable(Constant.KEY_PROPERTY_DETAILS, propertyDetails)
            bundle.putParcelableArrayList(Constant.KEY_ADDRESS, address)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_PHYSICAL_VERIFICATION)
            mListener?.addFragment(fragment, true)
        } else {
            val fragment = LandTaxEntryFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putInt(Constant.KEY_PRIMARY_KEY, pendingList.propertyId ?: 0)
            bundle.putParcelable(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
            bundle.putString(Constant.KEY_SYCO_TAX_ID, propertyDetails?.propertySycotaxID)
            bundle.putParcelable(Constant.KEY_PROPERTY_DETAILS, propertyDetails)
            bundle.putParcelableArrayList(Constant.KEY_ADDRESS, address)
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_PHYSICAL_VERIFICATION)
            mListener?.addFragment(fragment, true)
        }
    }

    private fun navigateToDocumentVerificationScreen(pendingList: PendingRequestList, propertyDetails: StorePropertyData?, address: ArrayList<GeoAddress>?) {
        showOptionSearch(false)
        val fragment = PropertyDocumentVerificationFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putInt(Constant.KEY_PRIMARY_KEY, pendingList.propertyId ?: 0)
        bundle.putParcelable(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
        bundle.putString(Constant.KEY_SYCO_TAX_ID, propertyDetails?.propertySycotaxID)
        bundle.putParcelable(Constant.KEY_PROPERTY_DETAILS, propertyDetails)
        bundle.putParcelableArrayList(Constant.KEY_ADDRESS, address)
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_DOC_VERIFICATION)
        mListener?.addFragment(fragment, true)
    }

    private fun navigateToReceiptScreen(pendingList: PendingRequestList) {
        val fragment = PropertyVerificationReceiptFragment()
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
        fragment.arguments = bundle
        mListener?.addFragment(fragment, true)
    }

    interface Listener {
        fun showProgressDialog()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener?, view: View)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener?)
        fun showSearchOption(show:Boolean)
    }
}