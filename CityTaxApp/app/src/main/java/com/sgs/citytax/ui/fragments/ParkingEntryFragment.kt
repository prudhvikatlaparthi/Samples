package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetPaymentPeriodForParking
import com.sgs.citytax.api.payload.NewTicketCreationData
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.GetPaymentPeriod
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentParkingEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.ParkingTicketReceiptActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_TAX_INVOICE_ID
import com.sgs.citytax.util.Constant.REQUEST_CODE_PAYMENT_SUCCESS
import kotlinx.android.synthetic.main.fragment_parking_entry.*
import java.math.BigDecimal
import java.util.*

class ParkingEntryFragment : BaseFragment() {
    private var mListener: FragmentCommunicator? = null
    private var mSycoTaxID: String? = ""
    lateinit var mBinding: FragmentParkingEntryBinding
    private var mParkingPlaces: ArrayList<AgentParkingPlace> = arrayListOf()
    private var mParkingTypes: ArrayList<ADMParkingType> = arrayListOf()
    private var mRateCycle: ArrayList<RateCycle> = arrayListOf()
    private var fromScreen: Constant.QuickMenu? = null
    private var vehicleDetails: VehicleDetails? = null
    private var paymentPeriod: GetPaymentPeriod? = null
    private var isValidParking: Boolean = true
    private var remark: String = ""
    private var mMinAmount: BigDecimal = BigDecimal.ZERO

    companion object {
        fun newInstance() = ParkingEntryFragment()
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                mSycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)
        }
        //endregion
        setEvents()
        processIntent()
        bindSpinner()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as FragmentCommunicator
            else context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_parking_entry, container, false)
        initComponents()
        return mBinding.root
    }


    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU)) {
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            }
            if (it.containsKey(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)) {
                vehicleDetails = it.getParcelable(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)
            }
        }
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("ADM_ParkingTypes", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mParkingTypes.addAll(response.parkingTypes)
                val parkingPlace = AgentParkingPlace(MyApplication.getPrefHelper().parkingPlaceID, MyApplication.getPrefHelper().parkingPlace)
                mParkingPlaces.add(parkingPlace)
                if (mParkingPlaces.isNullOrEmpty())
                    mBinding.spnParkingPlace.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mParkingPlaces)
                    mBinding.spnParkingPlace.adapter = adapter
                }
                if (mParkingTypes.isNullOrEmpty())
                    mBinding.spnParkingType.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mParkingTypes)
                    mBinding.spnParkingType.adapter = adapter
                }
                bindData()
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnParkingPlace.adapter = null
                mBinding.spnParkingType.adapter = null
                mListener?.dismissDialog()
            }

        })
    }

    private fun bindData() {
        mBinding.edtStartDate.setMinDate(Calendar.getInstance().timeInMillis)
        mBinding.edtStartDate.setDisplayDateFormat(Constant.DateFormat.DFddMMyyyyHHmmss.value)
        mBinding.edtTicketDate.setText(formatDate(Calendar.getInstance().time, Constant.DateFormat.DFddMMyyyyHHmmss))
        vehicleDetails?.let {
            mBinding.edtVehicleNo.setText(it.vehicleNumber)
            mBinding.edtOwner.setText(it.accountName)
        }
        mBinding.edtStartDate.setText(formatDate(Calendar.getInstance().time, Constant.DateFormat.DFddMMyyyyHHmmss))
        fetchPaymentPeriod()
    }

    private fun fetchPaymentPeriod() {
        var startDate = ""
        if (mBinding.edtStartDate.text != null && !TextUtils.isEmpty(mBinding.edtStartDate.text.toString()))
            startDate = formatDate(mBinding.edtStartDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyyHHmmss, Constant.DateFormat.DFyyyyMMddHHmmss)

        if (TextUtils.isEmpty(startDate))
            return

        var ruleID: Int? = null
        var rateCycleID: Int? = null
        var parkingTypeID: Int? = null
        val parkingPlaceID: Int? = MyApplication.getPrefHelper().parkingPlaceID
        if (mBinding.spnParkingType.selectedItem != null) {
            val parkingType = mBinding.spnParkingType.selectedItem as ADMParkingType?
            parkingType?.pricingRuleID?.let {
                if (it != -1)
                    ruleID = it
            }
            parkingType?.rateCycleID?.let {
                if (it != -1)
                    rateCycleID = it
            }
            parkingType?.parkingTypeID?.let {
                if (it != -1)
                    parkingTypeID = it
            }
        }

        val getPaymentPeriodForParking = GetPaymentPeriodForParking()
        getPaymentPeriodForParking.ruleID = ruleID
        getPaymentPeriodForParking.rateCycleID = rateCycleID
        getPaymentPeriodForParking.startDate = startDate
        getPaymentPeriodForParking.parkingPlaceID = parkingPlaceID
        getPaymentPeriodForParking.parkingTypeID = parkingTypeID

        mListener?.showProgressDialog()
        APICall.getPaymentPeriodForParking(getPaymentPeriodForParking, object : ConnectionCallBack<GetPaymentPeriod> {
            override fun onSuccess(response: GetPaymentPeriod) {
                paymentPeriod = response
                mBinding.edtAmount.setText(formatWithPrecision(response.amount))
                response.paymentPeriods?.paymentDate?.let { it ->
                    var endDate = ""
                    for (item in it)
                        item.endDate?.let {
                            endDate = it
                        }
                    mBinding.edtEndDate.setText(formatDate(endDate, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyHHmmss))
                }
                response.minPayAmount?.let {
                    mMinAmount = it
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun setEvents() {
        mBinding.spnParkingType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fetchPaymentPeriod()

                val parkingType = parent?.selectedItem as ADMParkingType?
                parkingType?.let {
                    mRateCycle = arrayListOf()
                    val rateCycle = RateCycle()
                    rateCycle.rateCycle = it.rateCycle
                    rateCycle.rateCycleID = it.rateCycleID
                    mRateCycle.add(rateCycle)
                    mBinding.chkPass.isChecked = parkingType.isPass != null && parkingType.isPass == "Y"
                    if (mRateCycle.isNullOrEmpty())
                        mBinding.spnRateCycle.adapter = null
                    else {
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mRateCycle)
                        mBinding.spnRateCycle.adapter = adapter
                    }
                }
            }
        }

        mBinding.edtStartDate.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) {
                    fetchPaymentPeriod()
                }
            }
        }
        mBinding.btnSave.setOnClickListener {
            if (validate()) {
                showRemarkDialog()
            }
        }
    }

    private fun showRemarkDialog() {
        val view = EditText(context)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 0, 16, 0)
        view.layoutParams = params
        view.hint = getString(R.string.hint_enter_remarks)
        mListener?.showAlertDialog(R.string.remarks, R.string.save, View.OnClickListener {
            val remarks = view.text?.toString()?.trim()
            if (TextUtils.isEmpty(remarks)) {
                view.error = getString(R.string.msg_enter_remarks)
            } else {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
                remark = remarks.toString()
                saveParkingEntry()
            }
        },
                R.string.cancel, View.OnClickListener
        {
            val dialog = (it as Button).tag as AlertDialog
            dialog.dismiss()
        },
                /*R.string.skip_and_save*/0, View.OnClickListener
        {
            val dialog = (it as Button).tag as AlertDialog
            dialog.dismiss()
            saveParkingEntry()
        },
                view)
    }

    private fun getNewTicketCreationData(): NewTicketCreationData {
        val admParkingType = mBinding.spnParkingType.selectedItem as ADMParkingType
        val rateCycle = mBinding.spnRateCycle.selectedItem as RateCycle
        val data = NewTicketCreationData()
        data.tenurePeriod = paymentPeriod?.paymentPeriods?.period!!
        data.parkingTicketDate = serverFormatDateTimeInMilliSecond(mBinding.edtTicketDate.text.toString())
        data.parkingPlaceID = MyApplication.getPrefHelper().parkingPlaceID
        data.parkingTypeID = admParkingType.parkingTypeID
        data.pricingRuleID = admParkingType.pricingRuleID
        data.rateCycleID = rateCycle.rateCycleID ?: 0
        data.parkingStartDate = serverFormatDateTimeInMilliSecond(mBinding.edtStartDate.text.toString())
        data.parkingEndDate = serverFormatDateTimeInMilliSecond(mBinding.edtEndDate.text.toString())
        data.amount = currencyToDouble(mBinding.edtAmount.text.toString())?.toDouble()!!
        data.vehicleNo = vehicleDetails?.vehicleNumber.toString()
        data.VehicleOwnerAccountID = vehicleDetails?.accountId!!
        data.userOrgBrId = MyApplication.getPrefHelper().userOrgBranchID
        if (admParkingType.overstayPricingRuleID != null) {
            data.overstayPricingRuleID = admParkingType.overstayPricingRuleID.toInt()
        }
        if (admParkingType.overstayRateCycleID != null) {
            data.overstayRateCycleID = admParkingType.overstayRateCycleID.toInt()
        }
        data.remark = remark
        data.isPass = if (chkPass?.isChecked != null && chkPass!!.isChecked) "Y" else "N"

        return data
    }

    private fun saveParkingEntry() {
        ObjectHolder.ticketCreationData = getNewTicketCreationData()
        ObjectHolder.remarks = remark

        val payment = MyApplication.resetPayment()

        payment.amountDue = BigDecimal(currencyToDouble(mBinding.edtAmount.text.toString())?.toDouble()!!)
        payment.amountTotal = BigDecimal(currencyToDouble(mBinding.edtAmount.text.toString())?.toDouble()!!)
        payment.minimumPayAmount = mMinAmount
        payment.TransactionTypeCode = "TAXINVOICE"
        payment.paymentType = Constant.PaymentType.PARKING_TICKET_PAY
        payment.parkingPlaceID = MyApplication.getPrefHelper().parkingPlaceID
        payment.vehicleNo = vehicleDetails?.vehicleNumber.toString()
        payment.customerID = vehicleDetails?.accountId!!

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
        startActivityForResult(intent, REQUEST_CODE_PAYMENT_SUCCESS)
    }

    private fun validate(): Boolean {
        if (TextUtils.isEmpty(mBinding.edtStartDate.text)) {
            mListener?.showToast(getString(R.string.msg_provide_valid) + " " + getString(R.string.start_date))
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT_SUCCESS && resultCode == Activity.RESULT_OK && data != null && data.hasExtra(KEY_TAX_INVOICE_ID)) {
            val intent = Intent(requireContext(), ParkingTicketReceiptActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
            intent.putExtra(KEY_TAX_INVOICE_ID, data.getIntExtra(KEY_TAX_INVOICE_ID, 0))
            intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, data.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
            startActivity(intent)
            mListener?.finish()
        }
    }

}