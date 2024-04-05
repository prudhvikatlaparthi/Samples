package com.sgs.citytax.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetEstimatedImpoundAmount
import com.sgs.citytax.api.payload.GetImpondmentReturn
import com.sgs.citytax.api.response.EstimatedImpoundAmountResponse
import com.sgs.citytax.api.response.GetImpondmentReturnResponse
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAnimalReturnQtyBinding
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatWithPrecision

class PendingAnimalReturnPayFragment : DialogFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentAnimalReturnQtyBinding
    private var mListener: Listener? = null
    private lateinit var mContext: Context
    private lateinit var mResources: Resources
    var estimatedImpoundAmountResponse : EstimatedImpoundAmountResponse? =  null

    var impoundReturn: TicketHistory? = null
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
        fun newInstance(impoundmentReturn: TicketHistory) = PendingAnimalReturnPayFragment().apply {
            this.impoundReturn = impoundmentReturn
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_animal_return_qty, container, false)
        initComponents()
        return mBinding.root
    }


    fun initComponents() {
        setListeners()
    }


    private fun setListeners() {
        mBinding.btnCancel.setOnClickListener(this)
        mBinding.btnPay.setOnClickListener(this)
        mBinding.btnGet.setOnClickListener(this)
        mBinding.edtEstimatedImpoundCharge.setText(formatWithPrecision(0.0))
        mBinding.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtEstimatedImpoundCharge.setText("")
                mBinding.tvTotalAmount.setText("")
                mBinding.tvMinPayAmount.setText("")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

//                if (!s.isNullOrEmpty() && impoundReturn?.quantity.toString().toBigDecimal() < s.toString().toBigDecimal()) {
//                    mListener?.showAlertDialog(getString(R.string.error_max_quantity))
//                    mBinding.edtEstimatedImpoundCharge.setText("")
//                    mBinding.edtQuantity.setText("")
//                }

                if (!s.isNullOrEmpty() && impoundReturn?.quantity.toString().toBigDecimal() < s.toString().toBigDecimal()) {
                    mListener?.showAlertDialog(getString(R.string.error_max_quantity))
                    mBinding.edtEstimatedImpoundCharge.setText("")
                    mBinding.edtQuantity.setText("")
                }
            }

        })

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
            R.id.btnPay -> {
                if (validateView()) {

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
                    payment.qty = mBinding.edtQuantity.text?.toString()!!.toInt()

                    val intent = Intent(requireContext(), PaymentActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                    startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
                }
            }
            R.id.btnGet -> {
                if (validate()) {
                    fetchEstimatedQuantityAmount(impoundReturn)
                }
            }
            R.id.btnCancel -> {
                dismiss()
            }

        }
    }

    private fun fetchEstimatedQuantityAmount(impoundmentType: TicketHistory?) {
        val getEstimatedImpoundAmount = GetEstimatedImpoundAmount()

        var impoundTypeID = 0
        impoundmentType?.invoiceTransactionVoucherNo?.let {
            impoundTypeID = it
        }
        getEstimatedImpoundAmount.impoundmentid = impoundTypeID
        if (!TextUtils.isEmpty(mBinding.edtQuantity.text?.toString()?.trim()))
            getEstimatedImpoundAmount.quantity = mBinding.edtQuantity.text?.toString()?.trim()

        mListener?.showProgressDialog()
        APICall.getEstimatedImpoundAmount(getEstimatedImpoundAmount, object : ConnectionCallBack<EstimatedImpoundAmountResponse> {
            override fun onSuccess(response: EstimatedImpoundAmountResponse) {
                estimatedImpoundAmountResponse = response
                mBinding.edtEstimatedImpoundCharge.setText(formatWithPrecision(estimatedImpoundAmountResponse?.impoundmentCharge))
                mBinding.tvMinPayAmount.setText(formatWithPrecision(estimatedImpoundAmountResponse?.minPayAmount))
                mBinding.tvTotalAmount.setText(formatWithPrecision(estimatedImpoundAmountResponse?.paymentAmount))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedImpoundCharge.setText("")
                mBinding.tvMinPayAmount.setText("")
                mBinding.tvTotalAmount.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun validateView(): Boolean {


        if (mBinding.edtQuantity.text.toString() != null && TextUtils.isEmpty(mBinding.edtQuantity.text.toString().trim()) ) {
            mListener?.showToast(getString(R.string.error_enter_quantity))
            return false
        }
        if(mBinding.edtQuantity.text.toString().toDouble()<=0){
            mListener?.showToast(getString(R.string.msg_qty_greater_than_zero))
            return false
        }
        if (mBinding.edtEstimatedImpoundCharge.text.toString() != null && TextUtils.isEmpty(mBinding.edtEstimatedImpoundCharge.text.toString().trim()) ) {
            mListener?.showToast(getString(R.string.calculate_charge_amount))
            return false
        }
        return true
    }

    private fun validate(): Boolean {


        if (mBinding.edtQuantity.text.toString() != null && TextUtils.isEmpty(mBinding.edtQuantity.text.toString().trim())) {
            mListener?.showToast(getString(R.string.error_enter_quantity))
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0) > 0) {
                    Log.e("Ticket payment frag", ">>>>>>>> Payment response came")

                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.TICKET_PAYMENT.Code)
                    startActivity(intent)
                    dismiss()
                    mListener?.finish()
                }
            }
        }
    }
}