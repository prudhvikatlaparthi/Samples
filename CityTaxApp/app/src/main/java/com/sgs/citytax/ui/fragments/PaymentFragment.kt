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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
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
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.GenerateTaxNoticeResponse
import com.sgs.citytax.api.response.MobiCashPayment
import com.sgs.citytax.api.response.StoreAndPayParkingTicketResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPaymentBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.Payment
import com.sgs.citytax.model.PaymentBreakup
import com.sgs.citytax.ui.adapter.PaymentBreakUpAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_PAYMENT
import java.math.BigDecimal

class PaymentFragment : BaseFragment() {

    private lateinit var binding: FragmentPaymentBinding
    private var paymentBreakups: ArrayList<PaymentBreakup> = ArrayList()
    private lateinit var payment: Payment
    private lateinit var helper: LocationHelper
    private lateinit var mContext: Context
    private lateinit var listener: Listener
    private lateinit var mCode: Constant.QuickMenu
    private var mobileCashPayment: MobiCashPayment? = null
    private var ignoreCheque: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(code: Constant.QuickMenu, ignoreCheque : Boolean = false) = PaymentFragment().apply {
            mCode = code
            this.ignoreCheque = ignoreCheque
        }
    }

    override fun initComponents() {
        initPayment()
        setViewControls()
        initEvents()
        bindData()
    }

    private fun setViewControls() {

        if(mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX){
            binding.clAmountLayout.visibility = GONE
        }
        if (mCode == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW)
            binding.btnCash.visibility = GONE
        else binding.btnCash.visibility =
            if (listener.getPrefHelper().isCashEnabled) View.VISIBLE else GONE

        binding.btnWallet.visibility =
            if (listener.getPrefHelper().isOrangeWalletPaymentEnabled) View.VISIBLE else GONE

        binding.btnMobiCash.visibility = View.VISIBLE
        if (mCode == Constant.QuickMenu.QUICK_MENU_WALLET_RECHARGE
            || mCode == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW
            /*|| mCode == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL*/
            || mCode == Constant.QuickMenu.QUICK_MENU_CREATE_ASSET_BOOKING
            || mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING
//            || mCode == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT
            || mCode == Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT
            || mCode == Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT
            || mCode == Constant.QuickMenu.QUICK_MENU_SERVICE
            || mCode == Constant.QuickMenu.QUICK_MENU_SERVICE_REQUEST_MASTER
            || mCode == Constant.QuickMenu.QUICK_MENU_PENDING_SERVICE_REQUESTS
            || ignoreCheque
//            || mCode == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX
//            || (mCode == Constant.QuickMenu.QUICK_MENU_SALES_TAX)//&& payment.cartItem?.item?.productTypeCode.equals("F"))
        )
            binding.btnCheque.visibility = GONE
        else
            binding.btnCheque.visibility = VISIBLE
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
        helper.disconnect()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helper.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        initComponents()
        return binding.root
    }

    private fun initEvents() {
        //binding.btnWallet.setOnClickListener(this)
        binding.btnWallet.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.WALLET
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this@PaymentFragment, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }
        })
        //binding.btnMobiCash.setOnClickListener(this)
        binding.btnMobiCash.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.MOBICASH
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this@PaymentFragment, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }
        })
        //binding.btnCash.setOnClickListener(this)
        binding.btnCash.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.CASH
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this@PaymentFragment, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }
        })
        //binding.btnCheque.setOnClickListener(this)
        binding.btnCheque.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.CHEQUE
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this@PaymentFragment, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }
        })
        //binding.btnProceed.setOnClickListener(this)
        binding.btnProceed.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                if (payment.amountPaid <= BigDecimal.ZERO) {
                    // listener.showAlertDialog(resources.getString(R.string.payment_cannot_be_done))
                    var doubleVal =
                        getDecimalVal(resources.getString(R.string.payment_cannot_be_done))
                    listener.showAlertDialog(
                        getTextWithPrecisionVal(
                            resources.getString(R.string.payment_cannot_be_done),
                            doubleVal
                        )
                    )
                    return
                }
                if(payment.paymentType == Constant.PaymentType.SALES_TAX ){
                    showConfirmationDialog()
                    return
                }
                // region EditText
                val view = EditText(context)
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(16, 0, 16, 0)
                view.layoutParams = params
                view.hint = getString(R.string.hint_enter_remarks)
                // endregion

                listener.showAlertDialog(
                    R.string.remarks,
                    R.string.save,
                    View.OnClickListener {
                        val remarks = view.text?.toString()?.trim()
                        if (TextUtils.isEmpty(remarks)) {
                            view.error = getString(R.string.msg_enter_remarks)
                        } else {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                            showConfirmationDialog(remarks)
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
                        showConfirmationDialog()
                    },
                    view
                )
            }
        })
        //binding.btnClose.setOnClickListener(this)
        binding.btnClose.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onBackPressed()
            }
        })
        // region Swipe
        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (paymentBreakups.size != 0) {
                        val breakup = paymentBreakups[viewHolder.adapterPosition]
                        paymentBreakups.remove(breakup)
                        (binding.recyclerView.adapter as PaymentBreakUpAdapter).updateBreakUps(
                            paymentBreakups
                        )
                        payment.amountDue = payment.amountDue + breakup.amount
                        payment.amountPaid = payment.amountPaid - breakup.amount
                        bindData()
                    }
                }

                override fun onChildDraw(
                    c: Canvas, recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    if (viewHolder.adapterPosition == -1) {
                        return
                    }
                    val xMarkMargin = 0
                    val xMark: Drawable? = ContextCompat.getDrawable(mContext, R.drawable.ic_delete)
                    xMark!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    val background = ColorDrawable(Color.GRAY)
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
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
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        // endregion
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun bindData() {
        binding.txtPaying.text = formatWithPrecision(payment.amountPaid)
        binding.txtDue.text = formatWithPrecision(payment.amountDue)
        binding.txtTotal.text = formatWithPrecision(payment.amountTotal)
    }

    /*override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btnCash -> {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.CASH
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }
            R.id.btnWallet -> {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.WALLET
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }

            R.id.btnMobiCash -> {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.MOBICASH
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }

            R.id.btnCheque -> {
                if (paymentBreakups.size > 0) {
                    listener.showAlertDialog(resources.getString(R.string.msg_multiple_payment_not_allowed))
                    return
                }
                payment.paymentMode = Constant.PaymentMode.CHEQUE
                val fragment = PaymentEditFragment.newInstance(mCode)
                fragment.setTargetFragment(this, REQUEST_CODE_PAYMENT)
                listener.addFragment(fragment, true)
            }

            R.id.btnClose -> {
                onBackPressed()
            }
            R.id.btnProceed -> {
                if (payment.amountPaid <= BigDecimal.ZERO) {

                    // listener.showAlertDialog(resources.getString(R.string.payment_cannot_be_done))
                    var doubleVal =
                        getDecimalVal(resources.getString(R.string.payment_cannot_be_done))
                    listener.showAlertDialog(
                        getTextWithPrecisionVal(
                            resources.getString(R.string.payment_cannot_be_done),
                            doubleVal
                        )
                    )
                    return
                }
                // region EditText
                val view = EditText(context)
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                                showConfirmationDialog(remarks)
                            }
                        },
                        R.string.cancel,
                        View.OnClickListener
                        {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                        },
                        R.string.skip_and_save,
                        View.OnClickListener
                        {
                            val dialog = (it as Button).tag as AlertDialog
                            dialog.dismiss()
                            showConfirmationDialog()
                        },
                        view)
            }
        }
    }*/

    private fun showConfirmationDialog(remarks: String? = "") {
        listener.showAlertDialog(R.string.are_you_sure_you_want_to_continue,
            R.string.yes,
            View.OnClickListener {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
                if (remarks == null || TextUtils.isEmpty(remarks))
                    savePayment()
                else
                    savePayment(remarks)
            },
            R.string.no,
            View.OnClickListener
            {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
            })
    }

    private fun savePayment(remarks: String? = "") {
        when {
            payment.paymentType == Constant.PaymentType.SALES_TAX -> {
                when (paymentBreakups[0].paymentMode) {
                    Constant.PaymentMode.MOBICASH.name -> doPaymentWithMobiCash(remarks)
                    else -> doPaymentForSalesTax(remarks)
                }
            }
            mCode == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW -> {
                if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name)
                    doPaymentWithMobiCash(remarks)
                else
                    doPaymentWithWalletLicenseRenew(
                        payment.userID,
                        remarks,
                        "",
                        Constant.PaymentMode.ORANGE.name
                    )
            }
            mCode == Constant.QuickMenu.QUICK_MENU_CREATE_ASSET_BOOKING ||
                    mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING -> {
                if (paymentBreakups[0].paymentMode == Constant.PaymentMode.CASH.name) {
                    doPaymentByCash4AssetBooking(remarks)
                } else if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name) {
                    doPaymentWithMobiCash(remarks)
                } else {
                    doPaymentWithWallet4AssetBooking(remarks, "", Constant.PaymentMode.ORANGE.name)
                }
            }
            mCode == Constant.QuickMenu.QUICK_MENU_SERVICE_REQUEST_MASTER ||
                    mCode == Constant.QuickMenu.QUICK_MENU_SERVICE -> {
                if (paymentBreakups[0].paymentMode == Constant.PaymentMode.CASH.name) {
                    doPaymentByCash4ServiceTax(remarks)
                } else if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name) {
                    doPaymentWithMobiCash(remarks)
                } else {
                    doPaymentWithWallet4ServiceTax(remarks, "", Constant.PaymentMode.ORANGE.name)
                }
            }
            mCode == Constant.QuickMenu.QUICK_MENU_PENDING_SERVICE_REQUESTS -> {
                when (payment.paymentBreakUps[0].paymentMode) {
                    Constant.PaymentMode.MOBICASH.name -> doPaymentWithMobiCash(remarks)
                    else -> generateServiceTaxNotice(remarks, "", Constant.PaymentMode.ORANGE.name)
                }
            }
            paymentBreakups[0].paymentMode == Constant.PaymentMode.CASH.name -> {
                when {
                    Constant.PaymentType.ROP === payment.paymentType -> {
                        createROPTaxInvoice()
                    }
                    else -> {
                        if (payment.paymentType == Constant.PaymentType.TICKET_PAY)
                            doPaymentWithCashForTicketPayment(remarks)
                        else if (payment.paymentType == Constant.PaymentType.PARKING_TICKET_PAY) {
                            if (mCode == Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
                                saveParkingTicketAndPayCash(remarks)
                            else
                                doPaymentWithCashForParkingTicketPayment(remarks)
                        } else
                            doPaymentWithCash(remarks)
                    }
                }
            }

            paymentBreakups[0].paymentMode == Constant.PaymentMode.CHEQUE.name -> {
                if (payment.paymentType == Constant.PaymentType.TICKET_PAY){
                    doPaymentWithChequeForTicketPayment(remarks)
                }else{

                    doPaymentWithCheque(remarks)
                }
            }
            else -> {
                if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name)
                    doPaymentWithMobiCash(remarks)
                else if (paymentBreakups[0].paymentMode == Constant.PaymentMode.WALLET.name) {
                    if (payment.paymentType == Constant.PaymentType.TICKET_PAY)
                        doPaymentWithWalletForTicketPayment(
                            remarks,
                            "",
                            Constant.PaymentMode.ORANGE.name
                        )
                    else if (payment.paymentType == Constant.PaymentType.PARKING_TICKET_PAY) {
                        if (mCode == Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
                            saveParkingTicketAndPayWallet(
                                remarks,
                                "",
                                Constant.PaymentMode.ORANGE.name
                            )
                        else
                            doPaymentWithWalletForParkingTicketPayment(
                                remarks,
                                "",
                                Constant.PaymentMode.ORANGE.name
                            )
                    } else
                        doPaymentWithWallet(remarks, "", Constant.PaymentMode.ORANGE.name)
                }

            }
        }
    }

    private fun doPaymentWithWallet4ServiceTax(
        remarks: String? = "",
        transactionID: String? = "",
        mode: String? = ""
    ) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByWallet4ServiceTax(
                    payment,
                    remarks,
                    transactionID,
                    mode,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    private fun doPaymentWithWallet4AssetBooking(
        remarks: String? = "",
        transactionID: String? = "",
        mode: String? = ""
    ) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByWallet4AssetBooking(
                    payment,
                    remarks,
                    transactionID,
                    mode,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    private fun doPaymentWithWalletForTicketPayment(
        remarks: String? = "",
        transactionID: String? = "",
        mode: String? = ""
    ) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByLawWalletForTicketPayment(
                    payment,
                    remarks,
                    transactionID,
                    mode,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    private fun saveParkingTicketAndPayWallet(
        remarks: String? = "",
        transactionID: String? = "",
        mode: String? = ""
    ) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"

                val ticketPaymentData = TicketPaymentData()
                ticketPaymentData.customerId = payment.customerID
                ticketPaymentData.productCode = payment.productCode
                ticketPaymentData.voucherNo = payment.voucherNo
                ticketPaymentData.TransactionNo = payment.TransactionNo
                ticketPaymentData.MinPayAmount = payment.minimumPayAmount
                ticketPaymentData.TransactionTypeCode = "TAXINVOICE"

                ticketPaymentData.remarks = remarks
                ticketPaymentData.ParkingPlaceID = payment.parkingPlaceID
                ticketPaymentData.vehno = payment.vehicleNo
                ticketPaymentData.ParkingAmount = payment.amountPaid

                val walletPaymentDetails = SALWalletPaymentDetails()
                if (payment.otp.isNotEmpty()) walletPaymentDetails.otp = payment.otp.toInt()
                walletPaymentDetails.mobileNo = payment.customerMobileNo
                walletPaymentDetails.amount = payment.amountPaid
                if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.TPA.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.PPS.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASA.name)
                    walletPaymentDetails.agentAccountID = MyApplication.getPrefHelper().accountId

                if (transactionID != null) walletPaymentDetails.transactionId = transactionID

                val paymentByWallet = PaymentByWallet()
                paymentByWallet.amount = payment.amountPaid
                if (mode != null) {
                    paymentByWallet.paymentModeCode = mode
                    paymentByWallet.walletCode = mode
                }
                if (transactionID != null) paymentByWallet.mobiTransactionID = transactionID


                val storeAndParkingTicketData = StoreAndParkingTicketData()
                storeAndParkingTicketData.isPaymentByWallet = true
                storeAndParkingTicketData.parkingTicket = ObjectHolder.ticketCreationData
                storeAndParkingTicketData.ticketPaymentModel = ticketPaymentData

                storeAndParkingTicketData.walletPaymentDetails = walletPaymentDetails
                storeAndParkingTicketData.wallet = paymentByWallet

                val storeAndPayParkingTicket = StoreAndPayParkingTicket()
                storeAndPayParkingTicket.data = storeAndParkingTicketData

                APICall.storeAndPayParkingTicket(
                    storeAndPayParkingTicket,
                    object : ConnectionCallBack<StoreAndPayParkingTicketResponse> {
                        override fun onSuccess(response: StoreAndPayParkingTicketResponse) {
                            listener.dismissDialog()

                            response.invoiceID?.let {
                                navigateToReceipt(it, response.advanceReceivedID ?: 0)
                            }

                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    private fun doPaymentWithWalletForParkingTicketPayment(
        remarks: String? = "",
        transactionID: String? = "",
        mode: String? = ""
    ) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByWalletForParkingTicketPayment(
                    payment,
                    remarks,
                    transactionID,
                    mode,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    private fun doPaymentWithWallet(
        remarks: String? = "",
        transactionID: String? = "",
        mode: String? = ""
    ) {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByWalletForTaxInvoices(
                    payment,
                    remarks,
                    transactionID,
                    mode,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.makeMobicashPaymentRequestStatus(
                    mobiCashPaymentStatus,
                    context,
                    object : ConnectionCallBack<MobiCashPayment> {
                        override fun onSuccess(response: MobiCashPayment) {
                            listener.dismissDialog()
                            if (response.status == "0") {
                                /*  if (payment.paymentType == Constant.PaymentType.SALES_TAX) {
                                    doPaymentForSalesTax(remarks, mobileCashPayment?.transactionID)
                                }
                                else if (mCode == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW) {
                                    doPaymentWithWalletLicenseRenew(payment.userID, remarks, mobileCashPayment?.transactionID)
                                }
                                else if (mCode == Constant.QuickMenu.QUICK_MENU_PENDING_SERVICE_REQUESTS) {
                                    generateServiceTaxNotice(remarks, mobileCashPayment?.transactionID)
                                }
                                else {
                                    doPaymentWithWallet(remarks, mobileCashPayment?.requestId)
                                }
    */
                                if (payment.paymentType == Constant.PaymentType.SALES_TAX) {
                                    doPaymentForSalesTax(remarks, mobileCashPayment?.requestId)
                                } else if (mCode == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW) {
                                    doPaymentWithWalletLicenseRenew(
                                        payment.userID,
                                        remarks,
                                        mobileCashPayment?.requestId,
                                        Constant.PaymentMode.MOBICASH.name
                                    )
                                } else if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_ASSET_BOOKING || mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING) {
                                    doPaymentWithWallet4AssetBooking(
                                        remarks,
                                        mobileCashPayment?.requestId,
                                        Constant.PaymentMode.MOBICASH.name
                                    )
                                } else if (mCode == Constant.QuickMenu.QUICK_MENU_SERVICE_REQUEST_MASTER || mCode == Constant.QuickMenu.QUICK_MENU_SERVICE) {
                                    doPaymentWithWallet4ServiceTax(
                                        remarks,
                                        mobileCashPayment?.requestId,
                                        Constant.PaymentMode.MOBICASH.name
                                    )
                                } else if (mCode == Constant.QuickMenu.QUICK_MENU_PENDING_SERVICE_REQUESTS) {
                                    generateServiceTaxNotice(
                                        remarks,
                                        mobileCashPayment?.requestId,
                                        Constant.PaymentMode.MOBICASH.name
                                    )
                                } else if (payment.paymentType == Constant.PaymentType.TICKET_PAY)
                                    doPaymentWithWalletForTicketPayment(
                                        remarks,
                                        mobileCashPayment?.requestId,
                                        Constant.PaymentMode.MOBICASH.name
                                    )
                                else if (payment.paymentType == Constant.PaymentType.PARKING_TICKET_PAY) {
                                    if (mCode == Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
                                        saveParkingTicketAndPayWallet(
                                            remarks,
                                            mobileCashPayment?.requestId,
                                            Constant.PaymentMode.MOBICASH.name
                                        )
                                    else
                                        doPaymentWithWalletForParkingTicketPayment(
                                            remarks,
                                            mobileCashPayment?.requestId,
                                            Constant.PaymentMode.MOBICASH.name
                                        )
                                } else
                                    doPaymentWithWallet(
                                        remarks,
                                        mobileCashPayment?.requestId,
                                        Constant.PaymentMode.MOBICASH.name
                                    )


                            } else {
                                listener.dismissDialog()
//                            listener.showAlertDialog(response.message)
                                listener.showAlertDialog(
                                    response.message,
                                    DialogInterface.OnClickListener { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                        listener.paymentFail()
                                    })

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

    private fun doPaymentWithWalletLicenseRenew(
        userID: String? = "",
        remarks: String?,
        transactionID: String? = "",
        mode: String? = ""
    ) {
        listener.showProgressDialog()
        APICall.storeAndPay4SubscriptionRenewal(
            payment,
            userID,
            remarks,
            transactionID,
            mode,
            payment.subscriptionRenewal,
            object : ConnectionCallBack<Int> {
                override fun onSuccess(response: Int) {
                    listener.dismissDialog()
                    listener.paymentSuccessForLicense(response)
                }

                override fun onFailure(message: String) {
                    listener.dismissDialog()
//                listener.showAlertDialog(message)
                    listener.showAlertDialog(
                        message,
                        DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            listener.paymentFail()
                        })
                }
            })
    }

    private fun createROPTaxInvoice() {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.createROPTaxInvoice(payment, context, object : ConnectionCallBack<Int> {
                    override fun onSuccess(response: Int) {
                        listener.dismissDialog()
                        navigateToPreviewScreen(response)
                    }

                    override fun onFailure(message: String) {
                        listener.dismissDialog()
//                        listener.showAlertDialog(message)
                        listener.showAlertDialog(
                            message,
                            DialogInterface.OnClickListener { dialogInterface, _ ->
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

    //for parking ticket payment
    private fun doPaymentWithCashForParkingTicketPayment(remarks: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByCash4ParkingTicketPayment(
                    payment,
                    remarks,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()

                            navigateToPreviewScreen(response)

                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    private fun saveParkingTicketAndPayCash(remarks: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"


                val ticketPaymentData = TicketPaymentData()
                ticketPaymentData.customerId = payment.customerID
                ticketPaymentData.productCode = payment.productCode
                ticketPaymentData.voucherNo = payment.voucherNo
                ticketPaymentData.TransactionNo = payment.TransactionNo
                ticketPaymentData.MinPayAmount = payment.minimumPayAmount
                ticketPaymentData.TransactionTypeCode = "TAXINVOICE"

                ticketPaymentData.remarks = remarks
                ticketPaymentData.ParkingPlaceID = payment.parkingPlaceID
                ticketPaymentData.vehno = payment.vehicleNo
                ticketPaymentData.ParkingAmount = payment.amountPaid

                val storeAndParkingTicketData = StoreAndParkingTicketData()
                storeAndParkingTicketData.isPaymentByCash = true
                storeAndParkingTicketData.parkingTicket = ObjectHolder.ticketCreationData
                storeAndParkingTicketData.ticketPaymentModel = ticketPaymentData
                val storeAndPayParkingTicket = StoreAndPayParkingTicket()
                storeAndPayParkingTicket.data = storeAndParkingTicketData

                APICall.storeAndPayParkingTicket(
                    storeAndPayParkingTicket,
                    object : ConnectionCallBack<StoreAndPayParkingTicketResponse> {
                        override fun onSuccess(response: StoreAndPayParkingTicketResponse) {
                            listener.dismissDialog()

                            response.invoiceID?.let {
                                navigateToReceipt(it, response.advanceReceivedID ?: 0)
                            }

                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    private fun navigateToReceipt(invoiceId: Int, advanceReceivedID: Int) {
        listener.navigateToReceipt(invoiceId, advanceReceivedID)
    }

    //for ticket payment
    private fun doPaymentWithCashForTicketPayment(remarks: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                if (payment.qty != null)
                    payment.qty = payment.qty

                APICall.paymentByCash4TicketPayment(
                    payment,
                    remarks,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()

                            navigateToPreviewScreen(response)

                        }

                        override fun onFailure(message: String) {
                            showAlertDialogWithFailureMsg(message)
                        }
                    })
            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)
            }
        })
    }

    private fun doPaymentWithChequeForTicketPayment(remarks: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                if (payment.qty != null)
                    payment.qty = payment.qty

                APICall.paymentByCheque4TicketPayment(
                    payment,
                    remarks,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                           showAlertDialogWithFailureMsg(message)
                        }
                    })
            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)

            }
        })
    }

    private fun showAlertDialogWithFailureMsg(message: String = "") {
        listener.dismissDialog()
        if (message.contains(getString(R.string.cheque), true)) {
            listener.showAlertDialog(
                message
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                listener.paymentFail()
            }
        } else {
            val doubleVal =
                getDecimalVal(message)
            listener.showAlertDialog(
                if (doubleVal != 0.0) getTextWithPrecisionVal(
                    message,
                    doubleVal
                ) else message
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                listener.paymentFail()
            }
        }
    }

    private fun doPaymentWithCash(remarks: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByCash4SalesInvoices(
                    payment,
                    remarks,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
                            if (message == getString(R.string.msg_no_internet)) {
                                listener.showAlertDialog(
                                    message,
                                    DialogInterface.OnClickListener { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                        listener.paymentFail()
                                    })
                            } else {
                                var doubleVal = getDecimalVal(message)
//                              listener.showAlertDialog(getTextWithPrecisionVal(message, doubleVal))
                                listener.showAlertDialog(
                                    getTextWithPrecisionVal(
                                        message,
                                        doubleVal
                                    ), DialogInterface.OnClickListener { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                        listener.paymentFail()
                                    })
                            }
                        }
                    })
            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)
            }
        })
    }

    private fun doPaymentWithCheque(remarks: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByCheque4TaxInvoices(
                    payment,
                    remarks,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    /*private fun doPaymentForSalesTax(remarks: String? = "", transactionId: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"

                val payload = Payload4SalesTax()
                payload.context = context

                val payment4SalesTax = Payment4SalesTax()
                payment4SalesTax.customerId = payment.customerID
                payment4SalesTax.productCode = payment.cartItem?.item?.productCode!!
                payment.cartItem?.quantity?.let {
                    payment4SalesTax.quantity = it
                }
                payment.cartItem?.item?.unitPrice?.let {
                    payment4SalesTax.unitPrice = it
                }
                if (paymentBreakups[0].paymentMode == Constant.PaymentMode.CHEQUE.name) {
                    payment4SalesTax.chequeamt = payment.amountPaid
                    payment4SalesTax.productCode = payment.productCode
                    payment4SalesTax.voucherNo = payment.voucherNo
                    payment4SalesTax.chequeDetails = payment.chequeDetails
                    payment4SalesTax.fileData = payment.fileData
                    payment4SalesTax.filenameWithExt = payment.filenameWithExt
                    payment4SalesTax.isPaymentByCheque = true
                } else {
                    payment4SalesTax.finalPrice = payment.amountPaid
                    if (payment.cartItem?.item?.validForMonths ?: 0 > 0) {
                        payment4SalesTax.validUpToDate = formatDateTime(
                            addMoths(
                                Date(),
                                payment.cartItem?.item?.validForMonths!!
                            )
                        )
                    }
                    // else {
                    if (paymentBreakups[0].paymentMode == Constant.PaymentMode.CASH.name) {
                        payment4SalesTax.isPaymentByCash = true
                        payment4SalesTax.isPaymentByWallet = false
                    } else {
                        payment4SalesTax.isPaymentByCash = false
                        payment4SalesTax.isPaymentByWallet = true

                        val walletPaymentDetails = SALWalletPaymentDetails()
                        if (payment.otp.isNotEmpty())
                            walletPaymentDetails.otp = payment.otp.toInt()
                        walletPaymentDetails.amount = payment.amountPaid
                        walletPaymentDetails.orgId = MyApplication.getPrefHelper().userOrgID
                        if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.TPA.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.PPS.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASA.name)
                            walletPaymentDetails.agentAccountID =
                                MyApplication.getPrefHelper().accountId


                        walletPaymentDetails.mobileNo = payment.customerMobileNo
                        transactionId?.let {
                            if (transactionId.isNotEmpty())
                                walletPaymentDetails.transactionId = it
                        }

                        payment4SalesTax.walletPaymentDetails = walletPaymentDetails

                        val wallet = PaymentByWallet()
                        wallet.amount = payment.amountPaid
                        if (paymentBreakups[0].paymentMode == Constant.PaymentMode.WALLET.name) {
                            wallet.paymentModeCode = Constant.PaymentMode.ORANGE.name
                            wallet.walletCode = Constant.PaymentMode.ORANGE.name
                        }
                        if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name) {
                            wallet.paymentModeCode = Constant.PaymentMode.MOBICASH.name
                            wallet.walletCode = Constant.PaymentMode.MOBICASH.name
                        }
                        transactionId?.let {
                            if (transactionId.isNotEmpty())
                                wallet.mobiTransactionID = transactionId
                        }


                        payment4SalesTax.wallet = wallet
                    }
                    // }
                }
                payload.payment4SalesTax = payment4SalesTax
                payload.payment4SalesTax?.remarks = remarks

                APICall.sellableProductGenInvAndPay(payload, object : ConnectionCallBack<Int> {
                    override fun onSuccess(response: Int) {
                        listener.dismissDialog()
                        navigateToPreviewScreen(response)
                    }

                    override fun onFailure(message: String) {
                        listener.dismissDialog()
//                        listener.showAlertDialog(message)
                        listener.showAlertDialog(
                            message,
                            DialogInterface.OnClickListener { dialogInterface, _ ->
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
    }*/

    private fun doPaymentForSalesTax(remarks: String? = "", transactionId: String? = "") {
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {

                val payload = payment.generateSalesTaxAndPayment
                payload?.context?.latitude = "$latitude"
                payload?.context?.longitude = "$longitude"

                if (paymentBreakups[0].paymentMode == Constant.PaymentMode.CASH.name) {
                    payload?.data?.isPaymentByCash = true
                    payload?.data?.isPaymentByWallet = false
                    payload?.data?.isPaymentByCheque = false
                } else if (paymentBreakups[0].paymentMode == Constant.PaymentMode.CHEQUE.name) {
                    payload?.data?.isPaymentByCash = false
                    payload?.data?.isPaymentByWallet = false
                    payload?.data?.isPaymentByCheque = true
                    payload?.data?.chequeDetails = payment.chequeDetails
                    payload?.data?.fileData = payment.fileData
                    payload?.data?.filenameWithExt = payment.filenameWithExt
                } else {
                    payload?.data?.isPaymentByCash = false
                    payload?.data?.isPaymentByWallet = true
                    payload?.data?.isPaymentByCheque = false

                    val walletPaymentDetails = SALWalletPaymentDetails()
                    if (payment.otp.isNotEmpty())
                        walletPaymentDetails.otp = payment.otp.toInt()
                    walletPaymentDetails.amount = payment.amountPaid
                    walletPaymentDetails.orgId = MyApplication.getPrefHelper().userOrgID
                    if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.TPA.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.PPS.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASA.name)
                        walletPaymentDetails.agentAccountID =
                            MyApplication.getPrefHelper().accountId


                    walletPaymentDetails.mobileNo = payment.customerMobileNo
                    transactionId?.let {
                        if (transactionId.isNotEmpty())
                            walletPaymentDetails.transactionId = it
                    }

                    payload?.data?.walletPaymentDetails = walletPaymentDetails

                    val wallet = PaymentByWallet()
                    wallet.amount = payment.amountPaid
                    if (paymentBreakups[0].paymentMode == Constant.PaymentMode.WALLET.name) {
                        wallet.paymentModeCode = Constant.PaymentMode.ORANGE.name
                        wallet.walletCode = Constant.PaymentMode.ORANGE.name
                    }
                    if (paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name) {
                        wallet.paymentModeCode = Constant.PaymentMode.MOBICASH.name
                        wallet.walletCode = Constant.PaymentMode.MOBICASH.name
                    }
                    transactionId?.let {
                        if (transactionId.isNotEmpty())
                            wallet.mobiTransactionID = transactionId
                    }


                    payload?.data?.wallet = wallet
                }

                APICall.generateSalesTaxAndPayment(
                    payload,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
                            if (message.contains(getString(R.string.cheque),true)) {
                                listener.showAlertDialog(
                                    message
                                ) { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    listener.paymentFail()
                                }
                            } else {
                                val doubleVal =
                                    getDecimalVal(message)
                                listener.showAlertDialog(
                                    if (doubleVal != 0.0) getTextWithPrecisionVal(
                                        message,
                                        doubleVal
                                    ) else message
                                ) { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    listener.paymentFail()
                                }
                            }
                        }
                    })

            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)
            }
        })
    }

    fun generateServiceTaxNotice(
        remarks: String?,
        transactionId: String? = "",
        mode: String? = ""
    ) {
        listener.showProgressDialog()
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                val payload = GenerateServiceTaxInvoice()
                payload.context = context
                payload.customerID = payment.customerID
                payload.amount = payment.serviceEstimatedAmount
                payload.TaxTypeCode = payment.paymentType.name
                payload.extraCharges = payment.extraCharges
                payload.voucherNo = payment.voucherNo
                payload.remarks = remarks

                var serviceCommission : ServiceCommission?= null
                if(payment.commissionPercentage!= BigDecimal.ZERO){
                    serviceCommission = ServiceCommission()
                    serviceCommission.commissionAmount = payment.commissionAmount
                    serviceCommission.commissionPercentage = payment.commissionPercentage
                }
                if (serviceCommission!=null){
                    payload.serviceCommission = serviceCommission
                }

                if (paymentBreakups[0].paymentMode == Constant.PaymentMode.CASH.name) {
                    payload.isPaymentByCash = true
                    payload.isPaymentByWallet == false
                } else if (paymentBreakups[0].paymentMode == Constant.PaymentMode.WALLET.name || paymentBreakups[0].paymentMode == Constant.PaymentMode.ORANGE.name || paymentBreakups[0].paymentMode == Constant.PaymentMode.MOBICASH.name) {
                    payload.isPaymentByCash = false
                    payload.isPaymentByWallet = true

                    val walletPaymentDetails = SALWalletPaymentDetails()
                    if (payment.otp.isNotEmpty())
                        walletPaymentDetails.otp = payment.otp.toInt()
                    walletPaymentDetails.amount = payment.amountPaid
                    walletPaymentDetails.orgId = MyApplication.getPrefHelper().userOrgID
                    // if((MyApplication.getPrefHelper().agentTypeCode==Constant.AgentTypeCode.TPA.name))
                    if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.TPA.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.PPS.name || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASA.name)
                        walletPaymentDetails.agentAccountID =
                            MyApplication.getPrefHelper().accountId


                    walletPaymentDetails.mobileNo = payment.customerMobileNo

                    transactionId?.let {
                        if (transactionId.isNotEmpty())
                            walletPaymentDetails.transactionId = it
                    }

                    payload.walletPaymentDetails = walletPaymentDetails

                    val wallet = PaymentByWallet()
                    wallet.amount = payment.amountPaid
                    if (mode != null) {
                        wallet.paymentModeCode = mode
                        wallet.walletCode = mode
                    }
                    transactionId?.let {
                        if (transactionId.isNotEmpty())
                            wallet.mobiTransactionID = transactionId
                    }


                    payload.wallet = wallet


                }
                APICall.generateServiceTaxNotice(
                    payload,
                    object : ConnectionCallBack<GenerateTaxNoticeResponse> {
                        override fun onSuccess(response: GenerateTaxNoticeResponse) {
                            listener.dismissDialog()
                            navigateToServicePreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
//                        listener.showAlertDialog(message)
                            val doublevalue = getDecimalVal(message)
                            listener.showAlertDialog(
                                getTextWithPrecisionVal(message,doublevalue),
                                DialogInterface.OnClickListener { dialogInterface, _ ->
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

    fun doPaymentByCash4ServiceTax(remarks: String?) {
        listener.showProgressDialog()
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByCash4ServiceTax(
                    payment,
                    remarks,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
                            val doubleVal =
                                getDecimalVal(message)
                            listener.showAlertDialog(
                                getTextWithPrecisionVal(message, doubleVal)
                            ) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                                listener.paymentFail()
                            }
                            /*listener.showAlertDialog(
                                message,
                                DialogInterface.OnClickListener { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    listener.paymentFail()
                                })*/
                        }
                    })
            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)
            }
        })
    }


    fun doPaymentByCash4AssetBooking(remarks: String?) {
        listener.showProgressDialog()
        helper.fetchLocation()
        helper.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                val context = SecurityContext()
                context.latitude = "$latitude"
                context.longitude = "$longitude"
                APICall.paymentByCash4AssetBooking(
                    payment,
                    remarks,
                    context,
                    object : ConnectionCallBack<Int> {
                        override fun onSuccess(response: Int) {
                            listener.dismissDialog()
                            navigateToPreviewScreen(response)
                        }

                        override fun onFailure(message: String) {
                            listener.dismissDialog()
                            val doubleVal =
                                getDecimalVal(message)
                            listener.showAlertDialog(getTextWithPrecisionVal(message, doubleVal))
                        }
                    })
            }

            override fun start() {
                listener.showProgressDialog(R.string.msg_processing_payment)
            }
        })
    }


    private fun navigateToPreviewScreen(advanceReceivedID: Int) {
        listener.paymentSuccess(advanceReceivedID, payment.currentTaxInvoiceNo)
    }

    private fun navigateToServicePreviewScreen(response: GenerateTaxNoticeResponse) {
        listener.onServiceGenerationSuccess(response)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_OK) {
            data?.extras?.let {
                if (it.containsKey(Constant.KEY_MOBICASH_PAYMENT))
                    mobileCashPayment =
                        it.getParcelable<MobiCashPayment>(Constant.KEY_MOBICASH_PAYMENT)
            }
            bindData()
            paymentBreakups = payment.paymentBreakUps as ArrayList<PaymentBreakup>
            (binding.recyclerView.adapter as PaymentBreakUpAdapter).updateBreakUps(paymentBreakups)
        }
        helper.onActivityResult(requestCode, resultCode)
    }

    fun onBackPressed() {
        MyApplication.resetPayment()
        listener.popBackStack()
        listener.finish()
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun popBackStack()
        fun getPrefHelper(): PrefHelper
        fun finish()
        fun paymentSuccess(advanceReceivedID: Int, taxInvoiceID: Int)
        fun navigateToReceipt(invoiceID: Int, advanceReceivedID: Int)
        fun onServiceGenerationSuccess(response: GenerateTaxNoticeResponse)
        fun paymentSuccessForLicense(advanceReceivedID: Int)
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: String?, listener: DialogInterface.OnClickListener)
        fun showAlertDialog(
            message: Int,
            positiveButton: Int,
            positiveListener: View.OnClickListener,
            negativeButton: Int,
            negativeListener: View.OnClickListener
        )

        fun showAlertDialog(
            message: Int,
            positiveButton: Int,
            positiveListener: View.OnClickListener,
            neutralButton: Int,
            neutralListener: View.OnClickListener,
            negativeButton: Int,
            negativeListener: View.OnClickListener,
            view: View
        )

        fun paymentFail()
    }

}