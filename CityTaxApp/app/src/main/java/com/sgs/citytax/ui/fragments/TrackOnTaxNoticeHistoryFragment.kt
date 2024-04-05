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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.databinding.FragmentTrackOnTaxNoticeHistoryBinding
import com.sgs.citytax.model.TaxNoticeDetail
import com.sgs.citytax.ui.adapter.TrackOnTaxNoticeHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class TrackOnTaxNoticeHistoryFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentTrackOnTaxNoticeHistoryBinding
    private var mListener: Listener? = null
    //private var mTaxNotices: ArrayList<TaxNoticeDetail>? = null
    private var mImpondmentReturnHistory: ArrayList<ImpondmentReturn> = arrayListOf()

    companion object {
        fun newInstance() = TrackOnTaxNoticeHistoryFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_on_tax_notice_history, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            mImpondmentReturnHistory = it.getParcelableArrayList<ImpondmentReturn>(Constant.KEY_TRACK_ON_TAX_NOTICE_HISTORY) as ArrayList<ImpondmentReturn>
        }
        bindData()
    }

    private fun bindData() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mImpondmentReturnHistory?.let {
            mBinding.recyclerView.adapter = TrackOnTaxNoticeHistoryAdapter(this, it)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val v = EditText(context)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 0, 16, 0)
        v.layoutParams = params
        v.hint = getString(R.string.hint_enter_remarks)
        // endregion
        mListener?.showAlertDialog(R.string.remarks,
                R.string.save,
                View.OnClickListener {
                    val remarks = v.text?.toString()?.trim()
                    if (TextUtils.isEmpty(remarks)) {
                        v.error = getString(R.string.msg_enter_remarks)
                    } else {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        cancelTaxNotice(obj as ImpondmentReturn, remarks)
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
                    cancelTaxNotice(obj as ImpondmentReturn)
                },
                v)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun cancelTaxNotice(taxNotice: ImpondmentReturn, remarks: String? = "") {
        taxNotice.transactionNo?.let { it ->
            mListener?.showProgressDialog()
            APICall.cancelTaxNotice(it.toInt(), remarks, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    val list: ArrayList<ImpondmentReturn> = arrayListOf()
                    mImpondmentReturnHistory?.let {
                        list.addAll(it)
                    }
                    if (list.contains(taxNotice)) {
                        val index = list.indexOf(taxNotice)
                        taxNotice.statusCode = Constant.TaxInvoices.CANCELLED.Status
                        list[index] = taxNotice
                    }
                    mImpondmentReturnHistory = arrayListOf()
                    mImpondmentReturnHistory?.addAll(list)
                    bindData()
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    interface Listener {
        fun showProgressDialog()
        fun showAlertDialog(message: String)
        fun dismissDialog()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

}