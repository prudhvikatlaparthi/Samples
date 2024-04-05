package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetIndividualTaxNoticeHistory
import com.sgs.citytax.api.payload.GetPropertyTaxNoticeHistory
import com.sgs.citytax.api.payload.SearchFilter
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.api.response.PropertyTaxNoticeResponse
import com.sgs.citytax.databinding.FragmentTaxNoticeHistoryBinding
import com.sgs.citytax.model.TaxNoticeHistoryList
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.adapter.TaxNoticeHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Pagination

class TaxNoticeHistoryFragment : BaseFragment(), TaxNoticeHistoryAdapter.Listener {

    private lateinit var binding: FragmentTaxNoticeHistoryBinding
    private var rootView: View? = null
    private var listener: Listener? = null
    private val taxNoticeHistoryAdapter: TaxNoticeHistoryAdapter by lazy { TaxNoticeHistoryAdapter(this) }
    private lateinit var pagination: Pagination
    private val resultTaxNoticeHistoryList: MutableList<TaxNoticeHistoryList> = mutableListOf()
    private lateinit var mSycoTaxID: String
    private var mCode: Constant.QuickMenu? = null
    private var getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails? = null
    private var mVuComProperties: VuComProperties? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tax_notice_history, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(sycoTaxID: String, fromScreen: Constant.QuickMenu, getSearchIndividualTaxDetail: GetSearchIndividualTaxDetails?, vuComProperties: VuComProperties?) = TaxNoticeHistoryFragment().apply {
            mSycoTaxID = sycoTaxID
            mCode = fromScreen
            getSearchIndividualTaxDetails = getSearchIndividualTaxDetail
            mVuComProperties = vuComProperties
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement listener")
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun initComponents() {
        binding.rcvTaxNoticeHistory.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        binding.rcvTaxNoticeHistory.adapter = taxNoticeHistoryAdapter
        pagination = Pagination(1, 10, binding.rcvTaxNoticeHistory) { pageNumber, PageSize ->
            bindData(pageIndex = pageNumber, pageSize = PageSize)
        }
        bindData()
    }

    private fun cancelTaxNotice(taxNotice: TaxNoticeHistoryList, remarks: String? = "") {
        taxNotice.taxInvoiceID?.let {
            listener?.showProgressDialog()
            APICall.cancelTaxNotice(it.toInt(), remarks, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    if (resultTaxNoticeHistoryList.contains(taxNotice)) {
                        val index = resultTaxNoticeHistoryList.indexOf(taxNotice)
                        taxNotice.statusCode = Constant.TaxInvoices.CANCELLED.Status
                        taxNotice.status="Annulé"
                        resultTaxNoticeHistoryList[index] = taxNotice
                        taxNoticeHistoryAdapter.clearAdapter()
                        taxNoticeHistoryAdapter.updateAdapter(resultTaxNoticeHistoryList)
                        binding.rcvTaxNoticeHistory.scrollToPosition(index)
                    }
                    listener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    listener?.showAlertDialog(message)
                }
            })
        }
    }

    fun bindData(pageIndex: Int = 1, pageSize: Int = 10) {
        if (mCode == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY) {
            if (pageIndex == 1) {
                resultTaxNoticeHistoryList.clear()
                binding.rcvTaxNoticeHistory.scrollToPosition(0)
                pagination.resetInitialPageNumber()
                listener?.showProgressDialog()
            }
            val getIndividualTaxNoticeHistory = GetIndividualTaxNoticeHistory(pageindex = pageIndex, pageSize = pageSize, voucherNo = getSearchIndividualTaxDetails?.voucherNo?.toInt(), taxRuleBookCode = getSearchIndividualTaxDetails?.taxRuleBookCode)
            APICall.getIndividualTaxNoticeHistory(getIndividualTaxNoticeHistory, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    /*pagination.totalRecords = response.totalRecordCounts
                            ?: response.individualTaxNoticeHistoryList?.size ?: 1*/
                    response.totalRecordCounts?.let {
                        if (it !=0) {
                            pagination.totalRecords = it
                        } else {
                            pagination.stopPagination(0)
                        }
                    }

                    if (response.individualTaxNoticeHistoryList?.size ?: 0 > 0) {
                        pagination.setIsScrolled(false)
                        for ((index, _) in response.individualTaxNoticeHistoryList!!.withIndex()) {
                            response.individualTaxNoticeHistoryList!![index].sycoTaxId = mSycoTaxID
                        }
                        resultTaxNoticeHistoryList.addAll(response.individualTaxNoticeHistoryList!!)
                        taxNoticeHistoryAdapter.updateAdapter(resultTaxNoticeHistoryList)
                    } else {
                        pagination.stopPagination(0)
                        if (pageIndex == 1) {
                            listener?.showAlertDialog(getString(R.string.msg_no_data))
                        }
                    }
                    listener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    if (pageIndex == 1) {
                        listener?.showAlertDialog(message)
                    }
                }

            })
        } else if (mCode == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY || mCode == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY) {
            if (pageIndex == 1) {
                resultTaxNoticeHistoryList.clear()
                binding.rcvTaxNoticeHistory.scrollToPosition(0)
                pagination.resetInitialPageNumber()
                listener?.showProgressDialog()
            }
            val getTaxNoticeHistory = GetPropertyTaxNoticeHistory(voucherNo = mVuComProperties?.propertyID, taxRuleBookCode = mVuComProperties?.taxRuleBookCode, pageIndex = pageIndex, pageSize = pageSize)
            APICall.getPropertyTaxNoticeHistory(getTaxNoticeHistory, object : ConnectionCallBack<PropertyTaxNoticeResponse> {
                override fun onSuccess(response: PropertyTaxNoticeResponse) {
                    response.totalSearchedRecords?.let {
                        if (it !=0) {
                            pagination.totalRecords = it
                        } else {
                            pagination.stopPagination(0)
                        }
                    }
                    if (response.results?.propertyTaxNoticeHistory?.isNotEmpty() == true) {
                        val list = response.results?.propertyTaxNoticeHistory!!
                        for ((index, _) in list.withIndex()) {
                            list[index].sycoTaxId = mSycoTaxID
                        }
                        resultTaxNoticeHistoryList.addAll(list)
                        taxNoticeHistoryAdapter.updateAdapter(resultTaxNoticeHistoryList)
                    } else {
                        pagination.stopPagination(0)
                        if (pageIndex == 1) {
                            listener?.showAlertDialog(getString(R.string.msg_no_data))
                        }
                    }
                    listener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    if (pageIndex == 1) {
                        listener?.showAlertDialog(message)
                    }
                }

            })
        } else {
            if (pageIndex == 1) {
                resultTaxNoticeHistoryList.clear()
                binding.rcvTaxNoticeHistory.scrollToPosition(0)
                pagination.resetInitialPageNumber()
                listener?.showProgressDialog()
            }
            val searchFilter = SearchFilter(filterColumns = arrayListOf("SycotaxID"), query = mSycoTaxID, pageIndex = pageIndex, pageSize = pageSize)
            APICall.getBusinessTaxNoticeHistory(searchFilter, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    response.totalRecordCounts?.let {
                        if (it !=0) {
                            pagination.totalRecords = it
                        } else {
                            pagination.stopPagination(0)
                        }
                    }
                    if (response.taxNoticeHistoryList?.size ?: 0 > 0) {
                        pagination.setIsScrolled(false)
                        resultTaxNoticeHistoryList.addAll(response.taxNoticeHistoryList!!)
                        taxNoticeHistoryAdapter.updateAdapter(resultTaxNoticeHistoryList)
                    } else {
                        pagination.stopPagination(0)
                        if (pageIndex == 1) {
                            listener?.showAlertDialog(getString(R.string.msg_no_data))
                        }
                    }
                    listener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    if (pageIndex == 1) {
                        listener?.showAlertDialog(message)
                    }
                }
            })
        }
    }

    interface Listener {
        fun showProgressDialog()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun onItemClick(taxNoticeHistoryList: TaxNoticeHistoryList, position: Int)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

    override fun onItemClick(taxNoticeHistoryList: TaxNoticeHistoryList, position: Int) {
        listener?.onItemClick(taxNoticeHistoryList, position)
    }

    override fun onCancelTaxNotice(taxNotice: TaxNoticeHistoryList) {
        // region EditText
        val view = EditText(context)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 0, 16, 0)
        view.layoutParams = params
        view.hint = getString(R.string.hint_enter_remarks)
        // endregion
        listener?.showAlertDialog(R.string.remarks,
                R.string.save,
                View.OnClickListener {
                    val remarks = view.text?.toString()?.trim()
                    if (TextUtils.isEmpty(remarks)) {
                        view.error = getString(R.string.msg_enter_remarks)
                    } else {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        cancelTaxNotice(taxNotice, remarks)
                    }
                },
                R.string.cancel,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                0,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    cancelTaxNotice(taxNotice)
                },
                view)
    }


}