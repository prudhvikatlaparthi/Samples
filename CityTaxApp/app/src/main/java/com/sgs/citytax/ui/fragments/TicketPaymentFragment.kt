package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPaymentTicketBinding
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.TicketPaymentListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision
import java.math.BigDecimal

//this is Track On Payment module
class TicketPaymentFragment : BaseFragment(), View.OnClickListener, TicketPaymentListAdapter.Listener, IClickListener {
    private lateinit var mBinding: FragmentPaymentTicketBinding
    private var mListener: Listener? = null
    private var adapter: TicketPaymentListAdapter? = null
    private var mImpondmentReturnHistory: ArrayList<ImpondmentReturn> = arrayListOf()
    lateinit var selectedSpinCombiValue: String
    lateinit var selectedSpinCombiCode: String

    var maxAmt: BigDecimal = BigDecimal.ZERO
    var minAmt: BigDecimal = BigDecimal.ZERO
    var currentDue: BigDecimal = BigDecimal.ZERO
    var count: Int = 0
    var chequePaidCount: Int = 0
    private var isPayFull: Boolean = false

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

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_ticket, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            mImpondmentReturnHistory = it.getParcelableArrayList<ImpondmentReturn>(Constant.KEY_IMPOUNDMENT_RETURN) as ArrayList<ImpondmentReturn>
            selectedSpinCombiValue = it.getString(Constant.KEY_SELECTED_COMBI_VALUE).toString()
            selectedSpinCombiCode = it.getString(Constant.KEY_SELECTED_COMBI_CODE).toString()
        }
        setViews()
        bindData()
    }

    private fun setViews() {
        //mBinding.btnCancel.setOnClickListener(this)
        mBinding.payFull.setOnClickListener(this)
        mBinding.rcvImpundmentsList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = TicketPaymentListAdapter(this, this, "2")
        mBinding.rcvImpundmentsList.adapter = adapter
    }

    fun bindData() {

        if (mImpondmentReturnHistory.size > 0) {
            adapter!!.addAll(mImpondmentReturnHistory)

            for (ImpondmentReturn in mImpondmentReturnHistory) {
                if (ImpondmentReturn.violatorTypeCode !=  Constant.ViolationTypeCode.ANIMAL.code) {
                    maxAmt = maxAmt.add(ImpondmentReturn.amount)
                    if(ImpondmentReturn.chequeStatusCode == Constant.CheckStatus.NEW.value){
                        chequePaidCount++
                    }else{
                        minAmt = minAmt.add(ImpondmentReturn.minmumPayAmount)
                        currentDue = currentDue.add(ImpondmentReturn.currentDue)
                    }
                }

                if (ImpondmentReturn.violatorTypeCode ==  Constant.ViolationTypeCode.ANIMAL.code) {
                    count++
                    if(ImpondmentReturn.chequeStatusCode == Constant.CheckStatus.NEW.value){
                        chequePaidCount++
                    }
                }
            }
            mBinding.tvTotalMinAmount.text = formatWithPrecision(minAmt)
            mBinding.tvTotalCurrentDues.text = formatWithPrecision(currentDue)
            mBinding.tvTotalDue.text = formatWithPrecision(maxAmt)
        } else {
            mBinding.rcvImpundmentsList.adapter = null
        }

        if (count == mImpondmentReturnHistory.size || chequePaidCount == mImpondmentReturnHistory.size) {
            mBinding.payFull.visibility = View.GONE
            mBinding.llPayAll.visibility=View.GONE
        }
        /*val getImpondmentReturnHistory = GetImpondmentReturnHistory()

        mImpondmentReturn?.let {
            if (it.applicableOnVehicle == "Y") {
                getImpondmentReturnHistory.onlydue = "Y"
                getImpondmentReturnHistory.filterType = selectedSpinCombiCode
                getImpondmentReturnHistory.filterString = selectedSpinCombiValue //"09iop"

            }
            //mBinding.tvTotalDue.text = formatWithPrecision(it.currentDue)
        }

        mListener?.showProgressDialog()
        APICall.getImpondmentReturnHistory(getImpondmentReturnHistory, object : ConnectionCallBack<GetLAWTaxTransactionsList> {
            override fun onSuccess(response: GetLAWTaxTransactionsList) {
                mListener?.dismissDialog()
                if (response.results != null && response.results.isNotEmpty()) {
                    mImpondmentReturnHistory = response?.results
                    adapter!!.addAll(mImpondmentReturnHistory)

                    for (ImpondmentReturn in mImpondmentReturnHistory) {
                        maxAmt = maxAmt.add(ImpondmentReturn.amount)
                        minAmt = minAmt.add(ImpondmentReturn.minmumPayAmount)
                        currentDue = currentDue.add(ImpondmentReturn.currentDue)
                    }
                    mBinding.tvTotalMinAmount.text = formatWithPrecision(minAmt)
                    mBinding.tvTotalCurrentDues.text = formatWithPrecision(currentDue)
                    mBinding.tvTotalDue.text = formatWithPrecision(maxAmt)

                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                mBinding.rcvImpundmentsList.adapter = null
            }
        })*/
    }


    interface Listener {
        fun popBackStack()
        fun finish()
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pay_full -> {
                val payment = MyApplication.resetPayment()

                payment.amountDue = currentDue
                payment.amountTotal = currentDue
                payment.minimumPayAmount = minAmt
                payment.paymentType = Constant.PaymentType.TICKET_PAY
                payment.SearchType = selectedSpinCombiCode
                payment.SearchValue = selectedSpinCombiValue

                if (mImpondmentReturnHistory.size > 0)
                    payment.customerID = mImpondmentReturnHistory?.get(0).accounttId!!
                isPayFull = true
                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)

            }
            R.id.btnCancel -> {
                mListener?.popBackStack()
            }

        }

    }

    override fun onItemClick(list: ImpondmentReturn, position: Int) {

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.btn_pay -> {
                isPayFull = false
                val impoundReturn = obj as ImpondmentReturn
                if (impoundReturn.violatorTypeCode !=  Constant.ViolationTypeCode.ANIMAL.code) {
                    val payment = MyApplication.resetPayment()

                    payment.amountDue = impoundReturn.currentDue!!
                    payment.amountTotal = impoundReturn.currentDue!!
                    payment.minimumPayAmount = impoundReturn.minmumPayAmount!!
                    payment.customerID = impoundReturn.transactionNo!!
                    payment.paymentType = Constant.PaymentType.TICKET_PAY
                    payment.productCode = impoundReturn.prodcode!!
                    payment.TransactionTypeCode = impoundReturn.transactiontypcode!!
                    payment.TransactionNo = impoundReturn.transactionNo!!
                    payment.customerID = impoundReturn?.accounttId!!
                    payment.SearchType = selectedSpinCombiCode
                    payment.SearchValue = selectedSpinCombiValue
                    val intent = Intent(requireContext(), PaymentActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                    startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
                }

                else {
                    //impoundReturn.violatorTypeCode ==  Constant.ViolationTypeCode.ANIMAL.code &&
                    if((impoundReturn.paidQuantity==impoundReturn.quantity) && (impoundReturn.currentDue!! > BigDecimal.ZERO)){
                        val payment = MyApplication.resetPayment()

                        payment.amountDue = impoundReturn.currentDue!!
                        payment.amountTotal = impoundReturn.currentDue!!
                        if(impoundReturn.minmumPayAmount!!<= BigDecimal.ZERO) {
                            payment.minimumPayAmount = impoundReturn.currentDue!!
                        }
                        else{
                            payment.minimumPayAmount = impoundReturn.minmumPayAmount!!
                        }
                        payment.customerID = impoundReturn?.transactionNo!!
                        payment.paymentType = Constant.PaymentType.TICKET_PAY
                        payment.productCode = impoundReturn?.prodcode!!
                        payment.TransactionTypeCode = impoundReturn?.transactiontypcode!!
                        payment.TransactionNo = impoundReturn?.transactionNo!!
                        payment.customerID = impoundReturn?.accounttId!!
                        payment.qty = 0
                        payment.SearchType = selectedSpinCombiCode
                        payment.SearchValue = selectedSpinCombiValue

                        val intent = Intent(requireContext(), PaymentActivity::class.java)
                        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                        startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
                    }else {
                        val dialogFragment: AnimalReturnPayFragment = AnimalReturnPayFragment.newInstance(impoundReturn)
                        dialogFragment.show(childFragmentManager, AnimalReturnPayFragment::class.java.simpleName)
                    }
                }
            }
        }
    }

    /*
    val payment = MyApplication.resetPayment()

                    payment.amountDue = estimatedImpoundAmountResponse?.paymentAmount!!
                    payment.amountTotal = estimatedImpoundAmountResponse?.paymentAmount!!
                    payment.minimumPayAmount = estimatedImpoundAmountResponse?.minPayAmount!!
                    payment.customerID = impoundReturn?.transactionNo!!
                    payment.paymentType = Constant.PaymentType.TICKET_PAY
                    payment.productCode = impoundReturn?.prodcode!!
                    payment.TransactionTypeCode = impoundReturn?.transactiontypcode!!
                    payment.TransactionNo = impoundReturn?.transactionNo!!
                    payment.customerID = impoundReturn?.accounttId!!
                    payment.qty = mBinding.edtQuantity.text.toString().toInt()

                    val intent = Intent(requireContext(), PaymentActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                    startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
                    */

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0) > 0) {
                    Log.e("Ticket payment frag", ">>>>>>>> Payment response came")


                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.TICKET_PAYMENT.Code)
                    intent.putExtra(Constant.KEY_STOP_API_4_PRINT_ALLOW, isPayFull)
                    startActivity(intent)
                    mListener?.finish()
                }
            }
        }
    }
}