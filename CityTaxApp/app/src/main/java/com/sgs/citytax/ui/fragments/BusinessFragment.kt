package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
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
import com.sgs.citytax.api.payload.GetTaxPayerDetails
import com.sgs.citytax.api.payload.StoreCustomerB2B
import com.sgs.citytax.api.response.BusinessVerificationResponse
import com.sgs.citytax.databinding.FragmentBusinessListBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaxPayerDetails
import com.sgs.citytax.ui.RegisterBusinessActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.BusinessListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_REFRESH
import com.sgs.citytax.util.Constant.KEY_SYCO_TAX_ID
import com.sgs.citytax.util.Constant.REQUEST_CODE_BUSINESS_MASTER
import com.sgs.citytax.util.Constant.REQUEST_CODE_SCANNER
import com.sgs.citytax.util.Event
import java.math.BigDecimal

class BusinessFragment : BaseFragment(),
        View.OnClickListener {
    private lateinit var mBinding: FragmentBusinessListBinding
    private var mListener: Listener? = null
    private var mAdapter: BusinessListAdapter? = null
    private var mtaxPayerDetails: ArrayList<TaxPayerDetails> = arrayListOf()

    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    var selectedPosition: Int = 0
    var status: String = ""
    var filterString: String = ""
    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None
    private var directNavigation = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_BUSINESS_MODE)) {
                businessMode =
                    arguments?.getSerializable(Constant.KEY_BUSINESS_MODE) as? Constant.BusinessMode ?: Constant.BusinessMode.None
            }
        }
        mBinding.rcvBusinessList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = BusinessListAdapter(object : BusinessListAdapter.Listener {
            override fun onItemClick(taxPayerDetails: TaxPayerDetails, position: Int) {
                performItemClick(taxPayerDetails,position)
            }

        })
        mBinding.rcvBusinessList.adapter = mAdapter
        bindData()
        initialiseListeners()
    }

    private fun performItemClick(taxPayerDetails: TaxPayerDetails, position: Int) {
        if (businessMode == Constant.BusinessMode.BusinessActivate) {
            var currentInvoiceDue = BigDecimal.ZERO
            //Initial Current year outstanding > 0 && CurrentInvoiceDue == 0
            if ((taxPayerDetails.businessDues?.businessDueSummary?.get(0)?.initialOutstandingCurrentYearDue!! > BigDecimal.ZERO
                        && taxPayerDetails.businessDues?.businessDueSummary?.get(0)?.currentInvoiceDue?.compareTo(
                    BigDecimal(0)
                ) == 0) ||
                taxPayerDetails.businessDues?.businessDueSummary?.get(0)?.initialOutstandingCurrentYearDue!!.compareTo(
                    BigDecimal(0)
                ) == 0
                && taxPayerDetails.businessDues?.businessDueSummary?.get(0)?.currentInvoiceDue?.compareTo(
                    BigDecimal(0)
                ) == 0
            ) {
                currentInvoiceDue = BigDecimal.ZERO
            } else {
                currentInvoiceDue = BigDecimal.ONE
            }
            val intent = Intent(requireContext(), RegisterBusinessActivity::class.java)
            //region Set Arguments
            ObjectHolder.registerBusiness.sycoTaxID = taxPayerDetails.sycoTaxID ?: ""
            ObjectHolder.registerBusiness.vuCrmAccounts = taxPayerDetails.vuCrmAccounts
            ObjectHolder.registerBusiness.vuCrmAccounts?.email = taxPayerDetails.email
            ObjectHolder.registerBusiness.vuCrmAccounts?.phone = taxPayerDetails.number
            ObjectHolder.registerBusiness.vuCrmAccounts?.estimatedTax = taxPayerDetails.estimatedTax
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
            intent.putExtra(Constant.KEY_BUSINESS_MODE, Constant.BusinessMode.BusinessActivate)
            intent.putExtra(Constant.KEY_CUSTOMER_ID, taxPayerDetails.sycoTaxID)
            if (currentInvoiceDue > BigDecimal.ZERO) {
                intent.putExtra(Constant.KEY_EDIT, true)
            } else {
                intent.putExtra(Constant.KEY_EDIT, false) //disable
            }
            //endregion
            startActivityForResult(intent,REQUEST_CODE_BUSINESS_MASTER)
        } else {
            val fragment = BusinessSummaryApprovalFragment()
            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(
                Constant.KEY_QUICK_MENU,
                Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS
            )
            bundle.putParcelable(Constant.KEY_STORE_CUSTOMER_B2B, getData(taxPayerDetails))
            bundle.putBoolean(Constant.KEY_IS_FIRST, true)
            bundle.putInt(Constant.KEY_POSITION, position)
            fragment.arguments = bundle
            fragment.setTargetFragment(
                this@BusinessFragment,
                Constant.REQUEST_CODE_BUSINESS_VERIFICATION
            )
            //endregion
            mListener?.showToolbarBackButton(R.string.menu_business)
            mListener?.addFragment(fragment, true)
        }
    }

    private fun getData(taxPayerDetails: TaxPayerDetails): StoreCustomerB2B {
        val storeCustomerB2B = StoreCustomerB2B()
        val vuCrmAccount = taxPayerDetails.vuCrmAccounts
        vuCrmAccount?.let {
            storeCustomerB2B.organization?.organization = it.accountName
            storeCustomerB2B.organization?.accountID = it.accountId!!
            storeCustomerB2B.organization?.organizationID = it.organizationId!!
            storeCustomerB2B.organization?.sycotaxID = it.sycoTaxID
            storeCustomerB2B.organization?.statusCode = it.statusCode
            storeCustomerB2B.organization?.status = it.status
            storeCustomerB2B.organization?.segmentId = it.segmentId
            storeCustomerB2B.organization?.parentOrganizationID = it.parentOrganizationID
            storeCustomerB2B.organization?.phone = it.phone
            storeCustomerB2B.organization?.telCode = it.telephoneCode.toString()
            storeCustomerB2B.organization?.email = it.email
            storeCustomerB2B.organization?.emailVerified = it.emailVerified
            storeCustomerB2B.organization?.phoneVerified = it.phoneVerified
            storeCustomerB2B.organization?.hotelDesFinanceID = it.hotelDesFinanceID
            storeCustomerB2B.organization?.status = it.status
            storeCustomerB2B.organization?.statusCode = it.statusCode
            storeCustomerB2B.organization?.activityDomainID = it.activityDomainID
            storeCustomerB2B.organization?.webSite = it.website
            storeCustomerB2B.organization?.ifu = it.ifu
            storeCustomerB2B.organization?.remarks = it.remarks
            it.accountId?.let {
                storeCustomerB2B.organization?.accountID = it
            }
            it.organizationId?.let {
                storeCustomerB2B.organization?.organizationID = it
            }
            storeCustomerB2B.organization?.sycotaxID = it.sycoTaxID
            storeCustomerB2B.organization?.email = it.email
            storeCustomerB2B.organization?.activityDomainName = it.activityDomain
            storeCustomerB2B.organization?.latitude = it.latitude?.toDouble()
            storeCustomerB2B.organization?.longitude = it.longitude?.toDouble()
            storeCustomerB2B.organization?.geoAddressID = it.geoAddressID
            storeCustomerB2B.organization?.activityClassID = it.activityClassID
            storeCustomerB2B.organization?.activityClassName = it.activityClassName
            storeCustomerB2B.organization?.tradeNo = it.tradeNo
        }
        return storeCustomerB2B
    }


    fun initialiseListeners() {
        mBinding.btnSearch.setOnClickListener(this)
        mBinding.btnScan.setOnClickListener(this)
        mBinding.rcvBusinessList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mBinding.rcvBusinessList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun bindData() {
        getTaxPayerDetailsForVerification(filterString)
    }


    private fun getTaxPayerDetailsForVerification(filter: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)

        val taxPayerDetails = GetTaxPayerDetails()
        taxPayerDetails.filterString = filter
        taxPayerDetails.pageIndex = pageIndex
        taxPayerDetails.pageSize = pageSize
        if (businessMode == Constant.BusinessMode.BusinessActivate) {
            taxPayerDetails.inactive = "N"
        } else {
            taxPayerDetails.emailverifd = "N"
            taxPayerDetails.mobverifd = "N"
        }
        isLoading = true

        APICall.getTaxPayerDetailsForVerification(taxPayerDetails, object : ConnectionCallBack<BusinessVerificationResponse> {
            override fun onSuccess(response: BusinessVerificationResponse) {
                mListener?.dismissDialog()
                response.results?.results?.let {
                    if (it != null) {
                        mtaxPayerDetails = it as ArrayList
                        val count: Int = mtaxPayerDetails.size
                        if (count < pageSize) {
                            hasMoreData = false
                        } else
                            pageIndex += 1
                        mAdapter?.addAll(it)
                        mAdapter?.notifyDataSetChanged()
                        isLoading = false
                        if (it.isNotEmpty() && it.size == 1 && directNavigation) {
                            directNavigation = false
                            performItemClick(it[0], 0)
                        }
                        return
                    }
                }
                mListener?.showAlertDialog(getString(R.string.msg_no_data), DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
            }

            override fun onFailure(message: String) {
                directNavigation = false
                mListener?.dismissDialog()
                isLoading = false
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                })

            }
        })
    }

    fun onBackPressed() {
        mListener?.finish()
    }

    interface Listener {
        fun finish()
        fun showProgressDialog(message: Int)
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCANNER) {
                val event = Event.instance
                val mData = event.intent
                mData?.let {
                    pageIndex = 1
                    mAdapter?.clear()
                    filterString = mData.getStringExtra(KEY_SYCO_TAX_ID) ?: ""
                    mBinding.edtownersearch.setText(filterString)
                    mListener?.hideKeyBoard()
                    directNavigation = true
                    bindData()
                    event.clearData()
                }
            } else if (requestCode == REQUEST_CODE_BUSINESS_MASTER){
                data?.let {
                    if (it.getBooleanExtra(KEY_REFRESH,false)){
                        pageIndex = 1
                        mAdapter?.clear()
                        mBinding.edtownersearch.setText("")
                        filterString = ""
                        bindData()
                    }
                }
            } else {
                data.let {
                    if (it != null) {
                        selectedPosition = it.getIntExtra(Constant.KEY_POSITION, 0)
                        status = it.getStringExtra(Constant.KEY_STATUS) ?: ""
                        pageIndex = 1
                        mAdapter?.clear()
                        mBinding.edtownersearch.setText("")
                        filterString = ""
                        bindData()
                        /*if (status.equals(getString(R.string.active))) {
                            mAdapter?.remove(selectedPosition)
                        } else {
                            mAdapter?.update(
                                selectedPosition,
                                data?.getStringExtra(Constant.KEY_STATUS)
                            )

                        }*/
                    }

                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSearch) {
            pageIndex = 1
            mAdapter?.clear()
            filterString = mBinding.edtownersearch.text.toString()
            mListener?.hideKeyBoard()
            getTaxPayerDetailsForVerification(filterString)
        } else if (v?.id == R.id.btnScan) {
            val intent = Intent(context, ScanActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
            intent.putExtra(
                Constant.KEY_BUSINESS_MODE,
                Constant.BusinessMode.BusinessActivateVerifyScan
            )
            startActivityForResult(intent, REQUEST_CODE_SCANNER)
        }
    }

}
