package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.NewTicketCreationData
import com.sgs.citytax.api.response.LastParkingAndOverStayChargeResponse
import com.sgs.citytax.api.response.ParkingTicketDetailsResponse
import com.sgs.citytax.api.response.ParkingTicketResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentLastParkingOverstayEntryBinding
import com.sgs.citytax.model.LastParkingTicketDetails
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.OverstayChargeDetails
import com.sgs.citytax.model.ParkingTicketPayloadData
import com.sgs.citytax.ui.ParkingTicketReceiptActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatDateTimeInMillisecond
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision
import java.math.BigDecimal

class LastParkingOverStayEntryFragment : BaseFragment() {
    private lateinit var mBinding: FragmentLastParkingOverstayEntryBinding
    private var mListener: Listener? = null
    private var vehicleNo: String? = ""
    private var overStayDetails: OverstayChargeDetails? = null
    private var lastParkingDetails: LastParkingTicketDetails? = null
    private var fromScreen: Constant.QuickMenu? = null

    private var currentDue: Double? = 0.0

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_last_parking_overstay_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        processIntent()
        getDetails()
        setListeners()
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_VEHICLE_NO))
                vehicleNo = it.getString(Constant.KEY_VEHICLE_NO)

            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun setViews() {
        currentDue = lastParkingDetails?.currentDue?.plus(overStayDetails?.amount ?: 0.0)
        if (lastParkingDetails != null) {
            if ((lastParkingDetails?.isPass == "N" || lastParkingDetails?.isPass==null) && currentDue ?: 0.0 > 0) {
                mBinding.btnSave.visibility = View.GONE
                mBinding.btnPay.visibility = View.VISIBLE
            } else {
                mBinding.btnSave.visibility = View.VISIBLE
                mBinding.btnPay.visibility = View.GONE
            }
        }

    }

    private fun getDetails() {
        mListener?.showProgressDialog()

        APICall.getLastParkingAndOverstayChargeDetails(vehicleNo, MyApplication.getPrefHelper().parkingPlaceID, object : ConnectionCallBack<LastParkingAndOverStayChargeResponse> {
            override fun onSuccess(response: LastParkingAndOverStayChargeResponse) {
                mListener?.dismissDialog()
                response.lastParkingTicketDetails?.let {
                    if (it != null) {
                        lastParkingDetails = it
                        bindLastTicketData(it)
                    }
                }

                response.overStayChargeDetails.let {
                    if (response.overStayChargeDetails != null) {
                        overStayDetails = it[0]
                        bindOverStayCharge(it[0])
                    } else {
                        mBinding.btnSave.visibility = View.GONE
                        context?.getString(R.string.msg_no_overcharge_data)?.let { it1 -> mListener?.showAlertDialog(it1) }
                    }
                }
                setViews()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindLastTicketData(lastParkingDetails: LastParkingTicketDetails?) {
        if (lastParkingDetails != null) {
            lastParkingDetails.parkingDate?.let {
                mBinding.txtParkingDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            lastParkingDetails.parkingStartDate?.let {
                mBinding.txtParkingStartDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            lastParkingDetails.parkingEndDate?.let {
                mBinding.txtParkingEndDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            lastParkingDetails.parkingPlace?.let {
                mBinding.txtParkingPlace.text = it
            }

            lastParkingDetails.parkingType?.let {
                mBinding.txtParkingType.text = it
            }

            lastParkingDetails.rateCyle?.let {
                mBinding.txtRateCycle.text = it
            }

            lastParkingDetails.amount?.let {
                mBinding.txtAmount.text = formatWithPrecision(it)
            }
        }
    }

    private fun bindOverStayCharge(overstayChargeDetails: OverstayChargeDetails?) {
        if (overstayChargeDetails != null) {
            overstayChargeDetails.parkingDate?.let {
                mBinding.txtOverStayParkingDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            overstayChargeDetails.parkingStartDate?.let {
                mBinding.txtOverStayParkingStartDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            overstayChargeDetails.parkingEndDate?.let {
                mBinding.txtOverStayParkingEndDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            overstayChargeDetails.parkingPlace?.let {
                mBinding.txtOverStayParkingPlace.text = it
            }

            overstayChargeDetails.parkingType?.let {
                mBinding.txtOverStayParkingType.text = it
            }

            overstayChargeDetails.rateCyle?.let {
                mBinding.txtOverStayRateCycle.text = it
            }

            overstayChargeDetails.amount?.let {
                mBinding.txtOverStayAmount.text = formatWithPrecision(it)
            }
        }
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener {
            mListener?.showProgressDialog()
            getParkingTicketDetails(vehicleNo)

        }

        mBinding.btnPay.setOnClickListener {
            val payment = MyApplication.resetPayment()
            ObjectHolder.ticketCreationData = getNewParkingTicketData()
            payment.amountDue = BigDecimal(currentDue ?: 0.0)
            payment.amountTotal = BigDecimal(currentDue ?: 0.0)
            payment.minimumPayAmount = BigDecimal(currentDue ?: 0.0)
            payment.paymentType = Constant.PaymentType.PARKING_TICKET_PAY
            payment.parkingPlaceID = MyApplication.getPrefHelper().parkingPlaceID
            payment.vehicleNo = overStayDetails?.vehicleNo.toString()
            payment.customerID = overStayDetails?.vehicleOwnerAccountId ?: 0

            val intent = Intent(requireContext(), PaymentActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
            startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)
        }
    }


    private fun getParkingTicketDetails(vehicleNo: String?) {
        val prefHelper = MyApplication.getPrefHelper()
        APICall.getParkingTicketDetails(vehicleNo, prefHelper.parkingPlaceID, "OUT", object : ConnectionCallBack<ParkingTicketDetailsResponse?> {
            override fun onSuccess(response: ParkingTicketDetailsResponse?) {
                mListener?.dismissDialog()
                callSave()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun callSave() {
        APICall.saveParkingTicket(preparePayload(), object : ConnectionCallBack<ParkingTicketResponse> {
            override fun onSuccess(response: ParkingTicketResponse) {
                mListener?.dismissDialog()
                navigateToPreviewScreen(response.invoiceId ?: 0)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToPreviewScreen(invoiceId: Int) {
        val fragment = ParkingTicketPreviewFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OVERSTAY)
        bundle.putInt(Constant.KEY_TAX_INVOICE_ID, invoiceId)
        fragment.arguments = bundle
        mListener?.addFragment(fragment, true)
        MyApplication.getPrefHelper().isFromHistory = false
    }

    private fun preparePayload(): ParkingTicketPayloadData {
        val payload = ParkingTicketPayloadData()

        if (!mBinding.edtHeaderRemarks.text.isNullOrEmpty())
            payload.remakrs = mBinding.edtHeaderRemarks.text.toString().trim()

        if (overStayDetails?.amount != null)
            payload.amount = overStayDetails?.amount ?: 0.0

        if (overStayDetails?.vehicleOwnerAccountId != null)
            payload.vehicleOwnerAccountId = overStayDetails?.vehicleOwnerAccountId

        if (overStayDetails?.vehicleNo != null)
            payload.vehicleNo = overStayDetails?.vehicleNo

        if (overStayDetails?.parkingEndDate != null)
            payload.parkingEndDate = formatDateTimeInMillisecond(overStayDetails?.parkingEndDate)

        if (overStayDetails?.parkingStartDate != null)
            payload.parkingStartDate = formatDateTimeInMillisecond(overStayDetails?.parkingStartDate)

        if (overStayDetails?.overStayRateCycleId != null)
            payload.overStayRateCycleId = overStayDetails?.overStayRateCycleId

        if (overStayDetails?.pricingRuleId != null)
            payload.pricingRuleId = overStayDetails?.pricingRuleId

        overStayDetails?.rateCycleId?.let {
            payload.rateCycleId = it
        }

        overStayDetails?.paringTypeId?.let {
            payload.parkingTypeId = it
        }

        overStayDetails?.userOrgBranchId?.let {
            payload.userOrgBranchId = it
        }

        overStayDetails?.parkingPlaceId?.let {
            payload.parkingPlaceId = it
        }

        overStayDetails?.parkingDate?.let {
            payload.parkingTicketDate = formatDateTimeInMillisecond(it)
        }

        overStayDetails?.overStayPricingRuleId?.let {
            payload.overStayPricingRuleId = it
        }

        overStayDetails?.tenurePeriod?.let {
            payload.tenurePeriod = it
        }

        lastParkingDetails?.parkingTicketID?.let {
            payload.parentParkingTicketID = it
        }

        return payload
    }

    private fun getNewParkingTicketData(): NewTicketCreationData {
        val payload = NewTicketCreationData()

        if (!mBinding.edtHeaderRemarks.text.isNullOrEmpty())
            payload.remark = mBinding.edtHeaderRemarks.text.toString().trim()

        if (overStayDetails?.amount != null)
            payload.amount = overStayDetails?.amount ?: 0.0

        if (overStayDetails?.vehicleOwnerAccountId != null)
            payload.VehicleOwnerAccountID = overStayDetails?.vehicleOwnerAccountId

        if (overStayDetails?.vehicleNo != null)
            payload.vehicleNo = overStayDetails?.vehicleNo ?: ""

        if (overStayDetails?.parkingEndDate != null)
            payload.parkingEndDate = formatDateTimeInMillisecond(overStayDetails?.parkingEndDate)

        if (overStayDetails?.parkingStartDate != null)
            payload.parkingStartDate = formatDateTimeInMillisecond(overStayDetails?.parkingStartDate)

        if (overStayDetails?.overStayRateCycleId != null)
            payload.overstayRateCycleID = overStayDetails?.overStayRateCycleId ?: 0

        if (overStayDetails?.pricingRuleId != null)
            payload.pricingRuleID = overStayDetails?.pricingRuleId ?: 0

        overStayDetails?.rateCycleId?.let {
            payload.rateCycleID = it
        }

        overStayDetails?.paringTypeId?.let {
            payload.parkingTypeID = it
        }

        overStayDetails?.userOrgBranchId?.let {
            payload.userOrgBrId = it
        }

        overStayDetails?.parkingPlaceId?.let {
            payload.parkingPlaceID = it
        }

        overStayDetails?.parkingDate?.let {
            payload.parkingTicketDate = formatDateTimeInMillisecond(it)
        }

        overStayDetails?.overStayPricingRuleId?.let {
            payload.overstayPricingRuleID = it
        }

        overStayDetails?.tenurePeriod?.let {
            payload.tenurePeriod = it
        }

        lastParkingDetails?.parkingTicketID?.let {
            payload.parentParkingTicketID = it
        }

        return payload
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS && resultCode == Activity.RESULT_OK && data != null && data.hasExtra(Constant.KEY_TAX_INVOICE_ID)) {
            val intent = Intent(requireContext(), ParkingTicketReceiptActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
            intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, data.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
            intent.putExtra(Constant.KEY_TAX_INVOICE_ID, data.getIntExtra(Constant.KEY_TAX_INVOICE_ID, 0))
            startActivity(intent)
            mListener?.finish()
        }
    }


    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showToast(message: String)
        fun showSnackbarMsg(message: String?)
    }
}