package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.GenerateTaxNoticeResponse
import com.sgs.citytax.api.response.PendingServiceListResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPendingServiceInvoiceListBinding
import com.sgs.citytax.databinding.ServiceDetailsDialogBinding
import com.sgs.citytax.model.PendingServiceDetails
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.PendingServiceListAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.show_billing_dialog.*
import java.math.BigDecimal

class PendingServiceInvoiceListFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentPendingServiceInvoiceListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var adapter: PendingServiceListAdapter? = null
    private var mPendingServices: ArrayList<PendingServiceDetails> = arrayListOf()
    var pageIndex: Int = 1
    val pageSize: Int = 50
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    var dialog: Dialog? = null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_service_invoice_list, container, false)
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        setViews()
        bindData()
    }

    private fun setViews() {
        mBinding.rcvPendingServices.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = PendingServiceListAdapter(this)
        mBinding.rcvPendingServices.adapter = adapter
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        val details = PendingServiceDetails()
        details.isLoading = true
        adapter?.add(details)
        isLoading = true

        APICall.getPendingServiceList(pageSize, pageIndex, object : ConnectionCallBack<PendingServiceListResponse> {
            override fun onSuccess(response: PendingServiceListResponse) {
                mListener?.dismissDialog()
                if (response.list?.pendingList != null && response.list?.pendingList!!.isNotEmpty()) {
                    mPendingServices = response.list?.pendingList!!
                    val count: Int = mPendingServices.size
                    if (count < pageSize) {
                        hasMoreData = false
                    } else
                        pageIndex += 1
                    adapter?.remove(details)
                    adapter!!.addAll(mPendingServices)
                    isLoading = false

                } else {
                    adapter?.remove(details)
                    isLoading = false
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.llRootView -> {
                    val pendingList = obj as PendingServiceDetails
                    showDetailsPopUp(pendingList)

                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun showDetailsPopUp(pendingList: PendingServiceDetails) {
        val layoutInflater = LayoutInflater.from(activity)
        val mBinding: ServiceDetailsDialogBinding
        dialog = Dialog(requireContext(), R.style.AlertDialogTheme)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.setCancelable(true)
        mBinding = DataBindingUtil.inflate(layoutInflater, R.layout.service_details_dialog, ll_root_view, false)
        dialog?.setContentView(mBinding.root)


        pendingList.serviceRequestNumber?.let {
            mBinding.txtServiceRequestNumber.text = it.toString()
        }
        pendingList.serviceRequestDate?.let {
            mBinding.txtServiceRequestDate.text = formatDisplayDateTimeInMillisecond(it)
        }
        pendingList.estimatedAmount?.let {
            mBinding.txtEstimatedAmount.text = formatWithPrecision(it)
        }
        pendingList.advanceAmount?.let {
            mBinding.txtAdvanceAmount.text = formatWithPrecision(it)
        }
        val netReceivable = pendingList.estimatedAmount?.minus(pendingList.advanceAmount
                ?: BigDecimal.ZERO)
        var totalNetReceivable = BigDecimal.ZERO
        var commissionAmount:Double? = 0.0
        mBinding.txtNetReceivable.text = formatWithPrecision(netReceivable)

        pendingList.assignTo3rdParty?.let {
            if(it.equals("Y")){

                pendingList.commissionPercentage?.let {
                    //todo FOR NOW, HIDING THESE TWO - 27-01-22
                    mBinding.commissionPrctLayout.visibility=View.GONE
                    mBinding.commissionAmtLayout.visibility=View.GONE

                    val commissionTotal = pendingList.advanceAmount?.plus(netReceivable ?: BigDecimal.ZERO)

                    val cmsnPercentage= it
                    commissionAmount = cmsnPercentage.toDouble().div(100).times(commissionTotal?.toDouble() ?: 0.0)

                    mBinding.txtCommissionPercentage.text= getTariffWithPercentage(cmsnPercentage.toString()+"%")
                    mBinding.txtCommissionAmount.text= formatWithPrecision(commissionAmount)
                }


            }else{
                mBinding.commissionPrctLayout.visibility=View.GONE
                mBinding.commissionAmtLayout.visibility=View.GONE
            }
        }


        mBinding.edtExtraCharges.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()) {
                         totalNetReceivable = netReceivable?.add(currencyToDouble(s.toString())?.toInt()?.toBigDecimal())
                        mBinding.txtNetReceivable.text = formatWithPrecision(totalNetReceivable)
                        val commissionTotal = pendingList.advanceAmount?.plus(totalNetReceivable ?: BigDecimal.ZERO)
                        commissionAmount = pendingList.commissionPercentage?.toDouble()?.div(100)?.times(commissionTotal?.toDouble() ?: 0.0)
                        mBinding.txtCommissionAmount.text= formatWithPrecision(commissionAmount)
                    } else
                        mBinding.txtNetReceivable.text = formatWithPrecision(netReceivable)
                }
            }
        })

        mBinding.btnProceed.setOnClickListener (object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                dialog?.dismiss()
                val payment = MyApplication.resetPayment()
                payment.serviceEstimatedAmount = pendingList.estimatedAmount ?: BigDecimal.ZERO
                payment.amountDue = if (mBinding.edtExtraCharges.text.toString().isNotEmpty())totalNetReceivable else netReceivable ?: BigDecimal.ZERO
                payment.amountTotal =  if (mBinding.edtExtraCharges.text.toString().isNotEmpty())totalNetReceivable else netReceivable ?: BigDecimal.ZERO
                payment.customerID = pendingList.accountId ?: 0
                payment.paymentType = Constant.PaymentType.SERVICE_REQUEST
                payment.extraCharges = if (mBinding.edtExtraCharges.text.toString().isNotEmpty()) currencyToDouble(mBinding.edtExtraCharges.text.toString())?.toInt()?.toBigDecimal()!! else BigDecimal.ZERO
                payment.voucherNo = pendingList.serviceRequestNumber
                payment.minimumPayAmount = if (mBinding.edtExtraCharges.text.toString().isNotEmpty())totalNetReceivable else netReceivable ?: BigDecimal.ZERO
                pendingList.assignTo3rdParty?.let {
                    if(it.equals("y",ignoreCase = true)){
                        payment.commissionPercentage = pendingList.commissionPercentage
                        payment.commissionAmount = commissionAmount?.toBigDecimal()
                    }
                }
                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
            }
        })
        dialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            if (requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS && resultCode == Activity.RESULT_OK) {
                var response: GenerateTaxNoticeResponse? = null
                data?.extras?.let {
                    if (it.containsKey(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE))
                        response = it.getParcelable(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE)
                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, arrayListOf(response))
                    intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                    startActivity(intent)
                }
            }
        }
        adapter?.clear()
        pageIndex = 1
        bindData()
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
    }
}