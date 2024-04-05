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
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentHandoverDueNoticesBinding
import com.sgs.citytax.model.HandoverDueNoticesList
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.adapter.DueandAgreementDocumentAdapter
import com.sgs.citytax.ui.adapter.HandoverDueNoticesAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Pagination

class HandoverDueNoticesFragment : BaseFragment(), HandoverDueNoticesAdapter.Listener {

    private lateinit var binding: FragmentHandoverDueNoticesBinding
    private var rootView: View? = null
    private var listener: Listener? = null
    private val handoverDueNoticeAdapter: HandoverDueNoticesAdapter by lazy { HandoverDueNoticesAdapter(this) }
    private lateinit var pagination: Pagination
    private val resultdueNotice: MutableList<HandoverDueNoticesList> = mutableListOf()
    private lateinit var mSycoTaxID: String
    private var mCode: Constant.QuickMenu? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_handover_due_notices, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(sycoTaxID: String, fromScreen: Constant.QuickMenu) = HandoverDueNoticesFragment().apply {
            mSycoTaxID = sycoTaxID
            mCode = fromScreen
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
        binding.rcvHandoverDueNotice.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        binding.rcvHandoverDueNotice.adapter = handoverDueNoticeAdapter
        pagination = Pagination(1, 10, binding.rcvHandoverDueNotice) { pageNumber, PageSize ->
            bindData(pageIndex = pageNumber, pageSize = PageSize)
        }
        bindData()
    }

    fun bindData(pageIndex: Int = 1, pageSize: Int = 10) {
        if (mCode == Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES) {
            if (pageIndex == 1) {
                resultdueNotice.clear()
                binding.rcvHandoverDueNotice.scrollToPosition(0)
                pagination.resetInitialPageNumber()
                listener?.showProgressDialog()
            }
            APICall.getHandoverDueNotices(mSycoTaxID,pageIndex,pageSize, object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    response.totalRecordCounts?.let {
                        if (it !=0) {
                            pagination.totalRecords = it
                        } else {
                            pagination.stopPagination(0)
                        }
                    }
                    if (response.handoverDueNoticesList?.size ?: 0 > 0) {
                        pagination.setIsScrolled(false)

                        resultdueNotice.addAll(response.handoverDueNoticesList!!)
                        handoverDueNoticeAdapter.updateAdapter(resultdueNotice)
                    } else {
                        pagination.stopPagination(0)
                        if (pageIndex == 1) {
                            listener?.showAlertDialogFailure("", R.string.due_notice_handed_over, DialogInterface.OnClickListener { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                activity?.finish()
                            })
                            handoverDueNoticeAdapter.clearAdapter();
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
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
        fun showToolbarBackButton(message: Int)
        fun showAlertDialogFailure(message: String, noRecordsFound: Int, onClickListener: DialogInterface.OnClickListener)

    }

    override fun onItemClick(dueNotice: HandoverDueNoticesList, position: Int) {
//        val dialogFragment: HandoverDueDialogFragment = HandoverDueDialogFragment.newInstance(dueNotice)
//        dialogFragment.show(childFragmentManager, HandoverDueNoticesFragment::class.java.simpleName)

        val fragment = HandoverDueDocumentsMasterFragment()
        //region SetArguments
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
        bundle.putString(Constant.KEY_PRIMARY_KEY, dueNotice.dueNoticeID.toString() ?: "")
        bundle.putString(Constant.KEY_REFFERENCENO, dueNotice.noticeReferenceNo.toString() ?: "")
        ObjectHolder.dueNoticeID = dueNotice.dueNoticeID
        bundle.putBoolean(Constant.KEY_DISPLAY,true)
        fragment.arguments = bundle
        //endregion
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
        listener?.showToolbarBackButton(R.string.handover_documents)
        listener?.addFragment(fragment, true)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            bindData()
        }
    }
}