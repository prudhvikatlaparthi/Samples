package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.MobiCashPaymentStatus
import com.sgs.citytax.api.response.MobiCashPayment
import com.sgs.citytax.api.response.MobiCashPaymentStatusResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPaymentRechargeBinding
import com.sgs.citytax.model.Payment
import com.sgs.citytax.model.PaymentBreakup
import com.sgs.citytax.ui.adapter.PaymentBreakUpAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_PAYMENT
import java.math.BigDecimal

class PaymentRechargeFragment : BaseFragment() {

    private lateinit var binding: FragmentPaymentRechargeBinding
    private lateinit var listener: Listener
    private lateinit var payment: Payment
    private var paymentBreakups: ArrayList<PaymentBreakup> = ArrayList()
    private lateinit var mContext: Context
    private lateinit var mCode: Constant.QuickMenu
    private var helper: LocationHelper? = null
    private var mobileCashPayment: MobiCashPayment? = null

    companion object {
        @JvmStatic
        fun newInstance(code: Constant.QuickMenu) = PaymentRechargeFragment().apply {
            mCode = code
        }
    }

    override fun initComponents() {
        bindData()
        initEvents()
        initPayment()
    }

    private fun initPayment() {
        payment = MyApplication.getPayment()
        paymentBreakups = payment.paymentBreakUps as ArrayList<PaymentBreakup>
        binding.recyclerView.adapter = PaymentBreakUpAdapter()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        listener = try {
            context as Listener
        } catch (e: Exception) {
            throw ClassCastException(context.toString() + "must implement Listener")
        }
    }

    override fun onResume() {
        super.onResume()
        helper = LocationHelper(requireContext(), binding.recyclerView, fragment = this)
    }

    override fun onDetach() {
        super.onDetach()
        helper?.disconnect()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_recharge, container, false)
        initComponents()
        return binding.root
    }

    private fun doPaymentWithMobiCash(remarks: String? = "") {
        listener.showProgressDialog()
        val mobiCashPaymentStatus = MobiCashPaymentStatus()
        if ((MyApplication.getPrefHelper().agentTypeCode != Constant.AgentTypeCode.ISP.name && MyApplication.getPrefHelper().agentTypeCode != Constant.AgentTypeCode.SPR.name))
            mobiCashPaymentStatus.agentAccountID = MyApplication.getPrefHelper().accountId
        mobiCashPaymentStatus.custAccountID = payment.customerID
        mobileCashPayment?.mpin?.let {
            mobiCashPaymentStatus.mpin = it
        }

        mobileCashPayment?.transId?.let { it1 ->
            mobiCashPaymentStatus.transactionID = it1
        }

        mobileCashPayment?.requestId?.let { it1 ->
            mobiCashPaymentStatus.requestid = it1
        }
        helper?.fetchLocation()
        helper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.makeMobicashPaymentRequestStatus(mobiCashPaymentStatus, context, object : ConnectionCallBack<MobiCashPayment> {
                    override fun onSuccess(response: MobiCashPayment) {
                        listener.dismissDialog()
//                        if (response.paymentStatus == Constant.PaymentStatus.PAID.value)
                        if (response.status == "0") {
                            rechargeAgent(remarks, mobileCashPayment?.requestId,Constant.PaymentMode.MOBICASH.name)
                        }else{
                            listener.dismissDialog()
                            listener.showAlertDialog(response.message)
                        }
                    }

                    override fun onFailure(message: String) {
                        listener.dismissDialog()
                        listener.showAlertDialog(message)
                    }
                })
            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)
            }
        })
    }


    private fun rechargeAgent(remarks: String? = "", transactionID: String? = "",mode:String?="") {
        helper?.fetchLocation()
        helper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"

                APICall.agentSelfRecharge(MyApplication.getPrefHelper(),payment, remarks, transactionID,mode, context, object : ConnectionCallBack<Int> {
                    override fun onSuccess(response: Int) {
                        listener.dismissDialog()
                        listener.paymentSuccess(response)
                    }

                    override fun onFailure(message: String) {
                        listener.dismissDialog()
//                        listener.showAlertDialog(message)
                        listener.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            listener.paymentFail()
                        })

                    }
                })
            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_OK) {
            binding.txtRechargeAmount.text = formatWithPrecision(payment.amountPaid)
            paymentBreakups = payment.paymentBreakUps as ArrayList<PaymentBreakup>
            (binding.recyclerView.adapter as PaymentBreakUpAdapter).updateBreakUps(paymentBreakups)
            data?.extras?.let {
                if (it.containsKey(Constant.KEY_MOBICASH_PAYMENT))
                    mobileCashPayment = it.getParcelable<MobiCashPayment>(Constant.KEY_MOBICASH_PAYMENT)
            }
        }
        helper?.onActivityResult(requestCode, resultCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    fun onBackPressed() {
        MyApplication.resetPayment()
        listener.popBackStack()
        listener.finish()
    }

    private fun initEvents() {
        binding.btnWallet.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.WALLET
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this@PaymentRechargeFragment, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }
        })
        binding.btnMobiCash.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.MOBICASH
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this@PaymentRechargeFragment, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }
        })
        binding.btnProceed.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (payment.amountPaid <= BigDecimal.ZERO) {
                    var doubleVal = getDecimalVal(resources.getString(R.string.payment_cannot_be_done))
                    listener.showAlertDialog(getTextWithPrecisionVal(resources.getString(R.string.payment_cannot_be_done), doubleVal))
                    return
                }
                // region EditText
                val view = EditText(context)
                val params = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                params.setMargins(16, 0, 16, 0)
                view.layoutParams = params
                view.hint = getString(R.string.hint_enter_remarks)
                // endregion
                listener.showAlertDialog(R.string.remarks,
                    R.string.save,
                    View.OnClickListener {
                        val remarks = view.text?.toString()?.trim()
                        if (TextUtils.isEmpty(remarks)) {
                            view.error = getString(R.string.msg_enter_remarks)
                        } else {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                            if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name)
                                doPaymentWithMobiCash(remarks)
                            else if(paymentBreakups[0].paymentMode==Constant.PaymentMode.WALLET.name)
                                rechargeAgent(remarks,"",Constant.PaymentMode.ORANGE.name)
                            else
                                rechargeAgent(remarks)
                        }
                    },
                    R.string.cancel,
                    View.OnClickListener
                    {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                    },
                    /*R.string.skip_and_save*/0,
                    View.OnClickListener
                    {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name)
                            doPaymentWithMobiCash()
                        else if(paymentBreakups[0].paymentMode==Constant.PaymentMode.WALLET.name)
                            rechargeAgent("","",Constant.PaymentMode.ORANGE.name)
                        else
                            rechargeAgent()
                    },
                    view)
            }
        })
        binding.btnClose.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                onBackPressed()
            }
        })
        // region Swipe
        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (paymentBreakups.size != 0) {
                    val breakup = paymentBreakups[viewHolder.adapterPosition]
                    paymentBreakups.remove(breakup)
                    (binding.recyclerView.adapter as PaymentBreakUpAdapter).updateBreakUps(paymentBreakups)
                    binding.txtRechargeAmount.text = "0.00"
                    payment.amountPaid = BigDecimal.ZERO
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                                     viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                if (viewHolder.adapterPosition == -1) {
                    return
                }
                val xMarkMargin = 0
                val xMark: Drawable? = ContextCompat.getDrawable(mContext, R.drawable.ic_delete)
                xMark!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                val background = ColorDrawable(Color.GRAY)
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)
                val itemHeight = itemView.bottom - itemView.top
                val intrinsicWidth = xMark.intrinsicWidth
                val intrinsicHeight = xMark.intrinsicWidth
                val xMarkLeft = itemView.right - xMarkMargin - intrinsicWidth
                val xMarkRight = itemView.right - xMarkMargin
                val xMarkTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val xMarkBottom = xMarkTop + intrinsicHeight
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom)
                xMark.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        // endregion
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun bindData() {
        listener.showProgressDialog()
        APICall.getAgentBalance(object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                binding.txtAvailableBalance.text = formatWithPrecision(response)
                binding.txtRechargeAmount.text = formatWithPrecision(0.00)
                listener.dismissDialog()
            }

            override fun onFailure(message: String) {
                listener.dismissDialog()
                listener.showAlertDialog(message)
            }
        })
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun popBackStack()
        fun finish()
        fun paymentSuccess(advanceReceivedID: Int)
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: String?, listener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
        fun paymentFail()
    }
}