package com.sgs.citytax.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetImpondmentReturn
import com.sgs.citytax.api.payload.GetImpondmentReturnHistory
import com.sgs.citytax.api.payload.GetPenaltyTransactions
import com.sgs.citytax.api.response.GetImpondmentReturnResponse
import com.sgs.citytax.api.response.GetLAWTaxTransactionsList
import com.sgs.citytax.api.response.GetLawPenaltyTransactionsResponse
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.databinding.FragmentImpoundmentsComboStaticInfoBinding
import com.sgs.citytax.model.ComComboStaticValues
import com.sgs.citytax.model.LawPenalties
import com.sgs.citytax.ui.ImpoundmentReturnHistoryActivity
import com.sgs.citytax.ui.PenaltyWaiveOffActivity
import com.sgs.citytax.ui.TicketPaymentActivity
import com.sgs.citytax.ui.TrackOnTaxNoticeHistoryActivity
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatWithPrecision

class ImpoundmentsComboStaticsDialogFragment : DialogFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentImpoundmentsComboStaticInfoBinding
    private var mListener: Listener? = null
    private var mCOMComboStaticValues: ArrayList<ComComboStaticValues> = ArrayList()
    private lateinit var mContext: Context
    private lateinit var mResources: Resources
    var fromScreen: Any? = ""
    private var mImpondmentReturnHistory: ArrayList<ImpondmentReturn> = arrayListOf()
    var pageIndex: Int = 1
    val pageSize: Int = 10
    lateinit var selectedSpinCombiValue: String
    lateinit var selectedSpinCombiCode: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
            mContext = context
            mResources = resources
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(locations: ArrayList<ComComboStaticValues>, fromScreen: Any?) = ImpoundmentsComboStaticsDialogFragment().apply {
            this.mCOMComboStaticValues = locations
            this.fromScreen = fromScreen
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_impoundments_combo_static_info, container, false)
        initComponents()
        return mBinding.root
    }


    fun initComponents() {
//        arguments?.let {
//            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
//        }
        filterData()
        setListeners()
    }


    private fun filterData() {

        val mAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mCOMComboStaticValues)
        mBinding.spnType.adapter = mAdapter
    }

    private fun setListeners() {
        mBinding.btnClear.setOnClickListener(this)
        mBinding.btnApply.setOnClickListener(this)
        mBinding.spnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mBinding.edtText.setText("")
            }

        }

    }

    interface Listener {
        fun popBackStack()
        fun finish()
        fun showToast(message: String)
        fun showSnackbarMsg(message: String)
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnApply -> {
                if (validateView()) {
                    val mComComboStaticValues: ComComboStaticValues = mBinding.spnType.selectedItem as ComComboStaticValues
                    selectedSpinCombiValue = mBinding.edtText.text.toString()
                    selectedSpinCombiCode = mComComboStaticValues.code.toString()
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_LAW_PENALTY_WAIVE_OFF) {
                        getLawPenaltyTransactions(mComComboStaticValues.code, mBinding.edtText.text.toString())
                    } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY) {
                        getLawTaxTransactionList(mComComboStaticValues.code, mBinding.edtText.text.toString())
                    } else
                        getImpondmentReturnList(mComComboStaticValues.code, mBinding.edtText.text.toString())

                }
            }
            R.id.btnClear -> {
                mBinding.edtText.setText("")
            }
        }
    }

    private fun getLawTaxTransactionList(code: String?, data: String) {
        val getImpondmentReturnHistory = GetImpondmentReturnHistory()
                getImpondmentReturnHistory.onlydue = "Y"
                getImpondmentReturnHistory.filterType = code
                getImpondmentReturnHistory.filterString = data //"09iop"

            //mBinding.tvTotalDue.text = formatWithPrecision(it.currentDue)

        mListener?.showProgressDialog()
        APICall.getImpondmentReturnHistory(getImpondmentReturnHistory, object : ConnectionCallBack<GetLAWTaxTransactionsList> {
            override fun onSuccess(response: GetLAWTaxTransactionsList) {
                mListener?.dismissDialog()
                if (response.results != null && response.results.isNotEmpty()) {
                    mImpondmentReturnHistory = response?.results as ArrayList<ImpondmentReturn>

                    if(fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY){
                        val intent = Intent(context, TrackOnTaxNoticeHistoryActivity::class.java)
                        intent.putExtra(Constant.KEY_TRACK_ON_TAX_NOTICE_HISTORY, mImpondmentReturnHistory)
                        startActivity(intent)
                        activity?.finish()
                    }else {
                        val intent = Intent(mContext, TicketPaymentActivity::class.java)
                        intent.putParcelableArrayListExtra(Constant.KEY_VIOLATION_VALUE, mImpondmentReturnHistory)
                        intent.putExtra(Constant.KEY_SELECTED_COMBI_VALUE, selectedSpinCombiValue)
                        intent.putExtra(Constant.KEY_SELECTED_COMBI_CODE, selectedSpinCombiCode)
                        mContext.startActivity(intent)
                        mListener?.finish()
                    }
                } else {
                    mListener?.showAlertDialog(
                        getString(R.string.no_records_found),
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                            mListener?.finish()
                        }
                    )
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (!TextUtils.isEmpty(message)) {
                    mListener?.showAlertDialog(
                        message,
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                            mListener?.finish()
                        }
                    )
                } else {
                    mListener?.showAlertDialog(
                        getString(R.string.no_records_found),
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                            mListener?.finish()
                        }
                    )
                }
            }
        })
    }

    private fun getImpondmentReturnList(code: String?, data: String) {
        val getImpondmentReturn = GetImpondmentReturn()
        getImpondmentReturn.columnName = code
        getImpondmentReturn.columnValue = data
        getImpondmentReturn.pageIndex = pageIndex
        getImpondmentReturn.pageSize = pageSize

        mListener?.showProgressDialog()
        dismiss()
        APICall.getImpondmentReturnList(getImpondmentReturn, object : ConnectionCallBack<GetImpondmentReturnResponse> {
            override fun onSuccess(response: GetImpondmentReturnResponse) {
                mListener?.dismissDialog()

                if (response.results !=null && response.results?.getImpondmentReturnList != null && response.results!!.getImpondmentReturnList.isNotEmpty()) {
                    val builder = AlertDialog.Builder(mContext)
                    builder.setTitle(R.string.title_return_impondment)
                    val adapter = ArrayAdapter(mContext, android.R.layout.simple_list_item_1, response.results!!.getImpondmentReturnList)
                    builder.setAdapter(adapter) { dialog, which ->
                        dialog.dismiss()
                        navigateToNextScreen(response.results!!.getImpondmentReturnList[which])
                    }
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    mListener?.showAlertDialog(mContext.getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        mListener?.finish()
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    mListener?.finish()
                })

            }
        })

    }

    private fun getLawPenaltyTransactions(code: String?, data: String) {
        val getPenaltyTransactions = GetPenaltyTransactions()
        getPenaltyTransactions.filterType = code
        getPenaltyTransactions.filterString = data

        mListener?.showProgressDialog()
        dismiss()
        APICall.getLawPenaltyTransactions(getPenaltyTransactions, object : ConnectionCallBack<GetLawPenaltyTransactionsResponse> {
            override fun onSuccess(response: GetLawPenaltyTransactionsResponse) {
                mListener?.dismissDialog()

                if (response.Penalties != null && response.Penalties.isNotEmpty()) {
                    navigateToNextPenaltyScreen(response.Penalties)
                } else {
                    mListener?.showAlertDialog(mContext.getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        mListener?.finish()
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                var messageData=message
                if(messageData=="")
                    messageData =mContext.getString(R.string.no_record)
                mListener?.showAlertDialog(messageData, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    mListener?.finish()
                })


            }
        })

    }

    private fun navigateToNextPenaltyScreen(lawPenalties: ArrayList<LawPenalties>) {
        val intent = Intent(mContext, PenaltyWaiveOffActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_LAW_TAX_DETAILS, lawPenalties)
        mContext.startActivity(intent)
        mListener?.finish()

    }

    private fun navigateToNextScreen(impondmentReturn: ImpondmentReturn) {
        /*if (fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT) {
            val intent = Intent(mContext, TicketPaymentActivity::class.java)
            intent.putExtra(Constant.KEY_VIOLATION_VALUE, impondmentReturn)
            intent.putExtra(Constant.KEY_SELECTED_COMBI_VALUE, selectedSpinCombiValue)
            intent.putExtra(Constant.KEY_SELECTED_COMBI_CODE, selectedSpinCombiCode)
            mContext.startActivity(intent)
            mListener?.finish()
        } else {*/
            val intent = Intent(mContext, ImpoundmentReturnHistoryActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            intent.putExtra(Constant.KEY_IMPOUNDMENT_RETURN, impondmentReturn)
            mContext.startActivity(intent)
            activity?.finish()

        //}
    }

    private fun validateView(): Boolean {

        if (mBinding.spnType.selectedItem == null || mBinding.spnType.selectedItemPosition == 0) {
            mListener?.showToast(getString(R.string.msg_provide) + " " + (getString(R.string.search_details)))
            return false
        }

        if (mBinding.edtText.text.toString() != null && TextUtils.isEmpty(mBinding.edtText.text.toString().trim())) {
            mListener?.showToast(getString(R.string.error_message))
            return false
        }
        return true
    }
}