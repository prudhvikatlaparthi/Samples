package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.OrgData
import com.sgs.citytax.api.response.ParkingTicketDetailsResponse
import com.sgs.citytax.api.response.ParkingTicketReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentParkingTicketPreviewBinding
import com.sgs.citytax.model.AppReceiptPrint
import com.sgs.citytax.model.ParkingInOutsData
import com.sgs.citytax.model.ParkingTicketDetails
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.ParkingTicketPaymentActivity
import com.sgs.citytax.ui.ParkingTicketReceiptActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*


class ParkingTicketPreviewFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentParkingTicketPreviewBinding
    private var parkingDetails: ParkingTicketDetails? = null
    private var mListener: Listener? = null
    private var invoiceId: Int? = 0
    private var advanceReceivedID: Int? = 0
    private var vehicleNo: String? = ""
    private val printHelper = PrintHelper()
    private var fromScreen: Constant.QuickMenu? = null
    val prefHelper = MyApplication.getPrefHelper()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_parking_ticket_preview, container, false)
        initComponents()
        return mBinding.root
    }

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

    override fun initComponents() {
        mListener?.hideToolbar()
        processIntent()
        setViews()
        getReceiptDetails()
        setListeners()
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_TAX_INVOICE_ID))
                invoiceId = it.getInt(Constant.KEY_TAX_INVOICE_ID)

            if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID))
                advanceReceivedID = it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID)


            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

            if (it.containsKey(Constant.KEY_PARKING_TICKET_DETAILS))
                parkingDetails = it.getParcelable(Constant.KEY_PARKING_TICKET_DETAILS)
        }
    }

    private fun setViews() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT) {
            mBinding.btnSaveTicket.visibility = View.VISIBLE
        } else {
            mBinding.btnSaveTicket.visibility = View.GONE
        }

        if (parkingDetails?.currentDue == 0.0) {
            mBinding.btnCollectPayment.visibility = View.GONE
        } else {
            mBinding.btnCollectPayment.visibility = View.VISIBLE
        }

        if (parkingDetails?.isPass.equals("N")) {
            if (parkingDetails?.currentDue == 0.0) {
                mBinding.btnCollectPayment.visibility = View.GONE
            } else if (parkingDetails?.currentDue ?: 0.0 > 0.0) {
                mBinding.btnCollectPayment.visibility = View.GONE
                mBinding.btnSaveTicket.visibility = View.GONE
                mBinding.btnPayAndExit.visibility = View.VISIBLE
            }
        }

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT){
            mBinding.btnCollectPayment.visibility = View.GONE
            mBinding.btnSaveTicket.visibility = View.GONE
            mBinding.btnPayAndExit.visibility = View.GONE
            mBinding.btnPrint.visibility = View.VISIBLE
        }

    }

    private fun getReceiptDetails() {
        mListener?.showProgressDialog()
        APICall.getParkingReceiptDetails(invoiceId
                ?: 0, 0, object : ConnectionCallBack<ParkingTicketReceiptResponse> {
            override fun onSuccess(response: ParkingTicketReceiptResponse) {
                response.receiptDetails.let {
                    vehicleNo = it[0].vehicleNumber
                    bindData(it[0],response.orgData)
                }
                mListener?.dismissDialog()

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData(receiptDetails: ParkingTicketDetails?, orgData: List<OrgData>?) {

        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = orgData
        )

        val owner = mBinding.txtVehicleOwnerLabel.context.getString(R.string.vehicle_owner)
        mBinding.txtVehicleOwnerLabel.text = String.format("%s%s", owner, com.sgs.citytax.util.getString(R.string.colon))

        val addressLabel = mBinding.titleAddressLabel.context.getString(R.string.title_address)
        mBinding.titleAddressLabel.text = String.format("%s%s", addressLabel, com.sgs.citytax.util.getString(R.string.colon))

        val sector = mBinding.txtSectorLabel.context.getString(R.string.sector)
        mBinding.txtSectorLabel.text = String.format("%s%s", sector, com.sgs.citytax.util.getString(R.string.colon))

        val state = mBinding.titleStateLabel.context.getString(R.string.state)
        mBinding.titleStateLabel.text = String.format("%s%s", state, com.sgs.citytax.util.getString(R.string.colon))

        val city = mBinding.titleCityLabel.context.getString(R.string.city)
        mBinding.titleCityLabel.text = String.format("%s%s", city, com.sgs.citytax.util.getString(R.string.colon))

        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text = String.format("%s%s", collectBy, com.sgs.citytax.util.getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport


        if (receiptDetails != null) {

            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            receiptDetails.ticketDate?.let {
                mBinding.txtDateOfParking.text = displayFormatDate(it)
            }

            receiptDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), receiptDetails.vehicleSycoTaxId
                        ?: ""))
            }

            receiptDetails.parkingStartDate?.let {
                mBinding.txtParkingStartTime.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.parkingEndDate?.let {
                mBinding.txtParkingEndTime.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.ticketNo?.let {
                mBinding.txtParkingNumber.text = it.toString()
            }

            receiptDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it
            }

            receiptDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                    mBinding.txtPrintCounts.text = it.toString() //TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
//                    mBinding.txtPrintCounts.text = "" //TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }

            receiptDetails.vehicleNumber?.let {
                mBinding.txtVehicleNo.text = it
            }

            receiptDetails.vehicleSycoTaxId?.let {
                mBinding.txtVehicleSycoTaxID.text = it
            }

            receiptDetails.vehicleOwner?.let {
                mBinding.txtVehicleOwner.text = it
            }

            receiptDetails.sycoTaxID?.let {
                mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }

            receiptDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            receiptDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }

            //region Address
            var address: String? = ""

            if (!receiptDetails.state.isNullOrEmpty()) {
                mBinding.txtState.text = receiptDetails.state
                address += receiptDetails.state
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            if (!receiptDetails.city.isNullOrEmpty()) {
                mBinding.txtCity.text = receiptDetails.city
                address += receiptDetails.city
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            //region Zone
            if (!receiptDetails.zone.isNullOrEmpty()) {
                mBinding.txtArdt.text = receiptDetails.zone
                address += receiptDetails.zone
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }
            //endregion

            //region Sector
            if (!receiptDetails.sector.isNullOrEmpty()) {
                mBinding.txtSector.text = receiptDetails.sector
                address += receiptDetails.sector
                address += ","
            } else {
                mBinding.txtSector.text = ""
                address += ""
            }
            //endregion

            //region plot
            if (!receiptDetails.plot.isNullOrEmpty()) {
                mBinding.txtSection.text = receiptDetails.plot
                address += receiptDetails.plot
                address += ","
            } else {
                mBinding.txtSection.text = ""
                address += ""
            }
            //endregion

            //region block
            if (!receiptDetails.block.isNullOrEmpty()) {
                address += receiptDetails.block
                mBinding.txtLot.text = receiptDetails.block
                address += ","
            } else {
                mBinding.txtLot.text = ""
                address += ""
            }
            //endregion

            //region door no
            if (!receiptDetails.doorNo.isNullOrEmpty()) {
                mBinding.txtParcel.text = receiptDetails.doorNo
                address += receiptDetails.doorNo
            } else {
                mBinding.txtParcel.text = ""
                address += ""
            }
            //endregion


            address?.let {
                mBinding.txtAddress.text = it
            }

            //endregion

            receiptDetails.parkingPlace?.let {
                mBinding.txtParkingPlace.text = it
            }

            receiptDetails.parkingType?.let {
                mBinding.txtParkingType.text = it
            }

            receiptDetails.parkingRate?.let {
                mBinding.txtParkingRate.text = getTariffWithCurrency(it)
            }

            receiptDetails.parkingAmount?.let {
                mBinding.txtParkingAmount.text = formatWithPrecision(it)
                mBinding.txtAmountImposed.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
            }

            receiptDetails.pendingAmount?.let {
                mBinding.txtPendingToBePaid.text = formatWithPrecision(it)
            }

            receiptDetails.generatedBy?.let {
                mBinding.txtCollectedBy.text = it
            }

            receiptDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(receiptDetails.taxInvoiceId!!, mBinding.btnPrint)
            }

        }
    }

    private fun setListeners() {
        mBinding.btnPrint.setOnClickListener(this)
        mBinding.btnCollectPayment.setOnClickListener(this)
        mBinding.btnSaveTicket.setOnClickListener(this)
        mBinding.btnPayAndExit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnCollectPayment -> {
                    val intent = Intent(context, ParkingTicketPaymentActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                    //intent.putExtra(Constant.KEY_SYCO_TAX_ID, searchID)
                    intent.putExtra(Constant.KEY_VEHICLE_NO, vehicleNo)
                    intent.putExtra(Constant.KEY_PARKING_PLACE_ID, MyApplication.getPrefHelper().parkingPlaceID)
                    startActivity(intent)
                    activity?.finish()
                }
                R.id.btnPrint -> {
                    if (MyApplication.sunmiPrinterService != null)
                        insertPrintCount(invoiceId ?: 0)
                    else
                        mListener?.showAlertDialog(getString(R.string.msg_print_not_support), DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                }
                R.id.btnSaveTicket -> {
                    getParkingTicketDetails(vehicleNo)
                }
                R.id.btnPayAndExit -> {
                    navigateToPaymentScreen()
                }
                else -> {
                }
            }
        }
    }

    private fun navigateToPaymentScreen() {
        val payment = MyApplication.resetPayment()
        payment.amountDue = parkingDetails?.currentDue?.toBigDecimal() ?: BigDecimal.ZERO
        payment.amountTotal = parkingDetails?.currentDue?.toBigDecimal() ?: BigDecimal.ZERO
        payment.minimumPayAmount = parkingDetails?.currentDue?.toBigDecimal() ?: BigDecimal.ZERO
        payment.paymentType = Constant.PaymentType.PARKING_TICKET_PAY
        payment.parkingPlaceID = prefHelper.parkingPlaceID
        payment.vehicleNo = vehicleNo
        payment.customerID = parkingDetails?.vehicleOwnerAccountId ?: 0

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT)
        startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)

    }

    private fun getParkingTicketDetails(vehicleNo: String?) {
        APICall.getParkingTicketDetails(vehicleNo, prefHelper.parkingPlaceID, "OUT", object : ConnectionCallBack<ParkingTicketDetailsResponse?> {
            override fun onSuccess(response: ParkingTicketDetailsResponse?) {
                mListener?.showAlertDialog(getString(R.string.vehicle_out_successfully), DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
            }
        })
    }

    private fun insertPrintCount(receiptId: Int, receiptCode: String = "Tax_Notice_Receipt") {
        val view = mBinding.llPreview
        val printBody = loadBitmapFromView(view)
        val resizedPrintBody = resize(printBody)
        APICall.insertPrintRequest(getAppReceiptPrint(receiptId, receiptCode), object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                printHelper.printBitmap(resizedPrintBody)
               // getReceiptDetails()
                //navigateToReceiptActivity()
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
                    navigateToReceiptActivity()
            }

            override fun onFailure(message: String) {

            }
        })
    }



    private fun navigateToReceiptActivity() {
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedID ?: 0)
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.PARKING_TICKET_PAYMENT.Code)
        startActivity(intent)
        mListener?.finish()
    }

    private fun getAppReceiptPrint(Id: Int, receiptType: String): AppReceiptPrint {
        val appReceiptPrint = AppReceiptPrint()
        appReceiptPrint.printDateTime = getDate(Date(), DateTimeTimeZoneMillisecondFormat)
        appReceiptPrint.receiptCode = receiptType
        appReceiptPrint.primaryKeyValue = Id
        return appReceiptPrint
    }

    private fun storeVehicleOut(advanceReceivedID:Int) {
        val data = ParkingInOutsData()
        data.parkingTicketID = parkingDetails?.parkingTicketId
// data.inTime = ""
        data.outTime = formatDateTimeInMillisecond(Date())
        APICall.storeParkingInOuts(data, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.showSnackbarMsg(getString(R.string.vehicle_out_successfully))
                val intent = Intent(requireContext(), ParkingTicketReceiptActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SAVE_PARKING_AND_COLLECT)
                intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedID)
                intent.putExtra(Constant.KEY_TAX_INVOICE_ID, invoiceId)
                startActivity(intent)
                mListener?.finish()
            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS && data != null && data.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID)) {
            val advanceReceivedID = data.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID,0)
          //  val invoiceID = data.getIntExtra(Constant.KEY_TAX_INVOICE_ID,0)
            mBinding.btnCollectPayment.visibility = View.GONE
            mBinding.btnSaveTicket.visibility = View.GONE
            mBinding.btnPayAndExit.visibility = View.GONE
            storeVehicleOut(advanceReceivedID)

        }
    }

    interface Listener {
        fun hideToolbar()
        fun showToolbarBackButton(title: Int)
        fun showProgressDialog()
        fun dismissDialog()
        fun showSnackbarMsg(message: String)
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun finish()
    }
}