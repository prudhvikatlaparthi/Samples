package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityAllTaxNoticesBinding
import com.sgs.citytax.model.AppReceiptPrint
import com.sgs.citytax.ui.adapter.AllTaxNoticesAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_DOCUMENT_NAME
import com.sgs.citytax.util.Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import com.sgs.citytax.util.Constant.KEY_VIOLATION_TICKET_RESPONSE
import java.util.*

class AllTaxNoticesActivity : BaseActivity(), IClickListener {

    private lateinit var mBinding: ActivityAllTaxNoticesBinding
    private var mAdapter: AllTaxNoticesAdapter? = null


    private var mTaxRuleBookCode: String = ""
    private var mAdvanceReceivedID: Int = 0
    private var mTaxReceiptID: Int = 0
//    private var mBookingRequestId: Int = 0
    private var mAssetRentId: Int = 0
    private var isMovable: Boolean = false
    private val printHelper = PrintHelper()
    private var mQuickMenuCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mNavigationMenuCode = Constant.NavigationMenu.NAVIGATION_MENU_NONE
    private var stopPrintAPI = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_tax_notices)

        processIntent()
        initViews()
    }

    private fun processIntent() {
        intent?.extras?.let { it ->
            // region Tax Notice
            if (it.containsKey(KEY_GENERATE_TAX_NOTICE_RESPONSE) && !it.getParcelableArrayList<GenerateTaxNoticeResponse>(KEY_GENERATE_TAX_NOTICE_RESPONSE).isNullOrEmpty()) {
                val list: ArrayList<GenerateTaxNoticeResponse> = it.getParcelableArrayList<GenerateTaxNoticeResponse>(KEY_GENERATE_TAX_NOTICE_RESPONSE) as ArrayList<GenerateTaxNoticeResponse>
                for (item in list) {
                    item.taxNoticeID?.let {
                        when (item.taxRuleBookCode?.toUpperCase(Locale.getDefault())) {
                            Constant.TaxRuleBook.CP.Code -> fetchCPDetails(it)
                            Constant.TaxRuleBook.CME.Code -> fetchCMEDetails(it)
                            Constant.TaxRuleBook.ROP.Code -> fetchROPDetails(it)
                            Constant.TaxRuleBook.PDO.Code -> fetchPDODetails(it)
                            Constant.TaxRuleBook.DEFAULT.Code -> fetchRoadTaxDetails(it)
                            Constant.TaxRuleBook.ADVERTISEMENT.Code -> fetchAdvertisementTaxNoticeDetails(it)
                            Constant.TaxRuleBook.CART.Code -> fetchCartTaxDetails(it)
                            Constant.TaxRuleBook.WEAPON.Code -> fetchWeaponTaxDetails(it)
                            Constant.TaxRuleBook.GAME.Code -> fetchGamingMachineTaxDetails(it)
                            Constant.TaxRuleBook.VT.Code -> fetchTicketIssueReceiptDetails(it)
                            Constant.TaxRuleBook.IMP.Code -> fetchImpoundmentReceiptDetails(it)
                            Constant.TaxRuleBook.IMP_RETURN.Code -> fetchImpoundmentReturnReceiptDetails(it)
                            Constant.TaxRuleBook.IMP_RETURN_ANIMAL.Code -> fetchImpoundmentAnimalReturnReceiptDetails(it,item.returnLineID!!)
                            Constant.TaxRuleBook.COM_PROP.Code -> PropertyLandTaxDetails(it)
                            Constant.TaxRuleBook.RES_PROP.Code -> PropertyLandTaxDetails(it)
                            Constant.TaxRuleBook.LAND_PROP.Code -> PropertyLandTaxDetails(it)
                            Constant.TaxRuleBook.LAND_CONTRIBUTION.Code -> PropertyLandTaxDetails(it)
                            Constant.TaxRuleBook.HOTEL.Code -> fetchHotelTaxNoticeDetails(it)
                            Constant.TaxRuleBook.SHOW.Code -> fetchShowTaxNoticeDetails(it)
                            Constant.TaxRuleBook.LICENSE.Code -> getLicenseRenewalTaxNoticeDetails(it)
                            Constant.TaxRuleBook.SERVICE.Code -> getServiceTaxNoticeDetails(it)
                            else -> {
                            }
                        }
                    }
                }
            }
            // endregion

            // region Tax Receipt
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_VIOLATION_ID))
                mAdvanceReceivedID = it.getInt(Constant.KEY_IMPOUNDMENT_VIOLATION_ID)
            if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID))
                mAdvanceReceivedID = it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID)
            if (it.containsKey(Constant.KEY_TAX_INVOICE_ID))
                mTaxReceiptID = it.getInt(Constant.KEY_TAX_INVOICE_ID)
            if (it.containsKey(Constant.KEY_BOOKING_REQUEST_ID))
                mAdvanceReceivedID = it.getInt(Constant.KEY_BOOKING_REQUEST_ID)
            if (it.containsKey(Constant.KEY_ASSET_RENT_ID))
                mAssetRentId = it.getInt(Constant.KEY_ASSET_RENT_ID)
            if (it.containsKey(Constant.KEY_IS_MOVABLE))
                isMovable = it.getBoolean(Constant.KEY_IS_MOVABLE)
            if (it.containsKey(Constant.KEY_TAX_RULE_BOOK_CODE)) {
                mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE, "")
                when (mTaxRuleBookCode.toUpperCase(Locale.getDefault())) {
                    Constant.TaxRuleBook.CP.Code -> fetchCPTaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.CME.Code -> fetchCMETaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.ROP.Code -> fetchROPTaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.PDO.Code -> fetchPDOTaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.DEFAULT.Code -> fetchRoadTaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.RECHARGE.Code -> fetchAgentReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.LICENSE_RENEWAL.Code -> fetchLicenseRenewalDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.SALES.Code -> fetchSalesTaxDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.PENALTY_WAIVE_OFF.Code -> fetchPenaltyReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.ADVERTISEMENT.Code -> fetchAdvertisementReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.SHOW.Code -> fetchShowReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.HOTEL.Code -> fetchHotelReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.OUTSTANDING_WAIVE_OFF.Code -> fetchInitialOutstandingReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.BOOKING_REQUEST.Code -> fetchBookingRequestReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.BOOKING_ADVANCE.Code -> fetchBookingAdvanceReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.CART.Code -> fetchCartTaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.GAME.Code -> fetchCartTaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.WEAPON.Code -> fetchCartTaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.ASSET_ASSIGNMENT.Code -> fetchAssetAssignmentReceiptDetails(mAssetRentId)
                    Constant.TaxRuleBook.ASSET_RETURN.Code -> fetchAssetReturnReceiptDetails(mAssetRentId)
                    Constant.TaxRuleBook.TICKET_PAYMENT.Code -> fetchTrafficTicketPaymentReceiptDetails(mAdvanceReceivedID, Constant.TaxRuleBook.TICKET_PAYMENT.Code)
                    Constant.TaxRuleBook.TICKET_PAYMENT_TRANSACTION.Code -> fetchTrafficTicketPaymentReceiptDetails(mAdvanceReceivedID, Constant.TaxRuleBook.TICKET_PAYMENT_TRANSACTION.Code)
                    Constant.TaxRuleBook.PARKING_TICKET_PAYMENT.Code -> fetchParkingTicketPaymentReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.COM_PROP.Code -> fetchPropertyTaxPaymentReceiptDetails(mAdvanceReceivedID, Constant.TaxRuleBook.COM_PROP.Code)
                    Constant.TaxRuleBook.RES_PROP.Code -> fetchPropertyTaxPaymentReceiptDetails(mAdvanceReceivedID, Constant.TaxRuleBook.RES_PROP.Code)
                    Constant.TaxRuleBook.LAND_PROP.Code -> fetchPropertyTaxPaymentReceiptDetails(mAdvanceReceivedID, Constant.TaxRuleBook.LAND_PROP.Code)
                    Constant.TaxRuleBook.LAND_CONTRIBUTION.Code -> fetchPropertyTaxPaymentReceiptDetails(mAdvanceReceivedID, Constant.TaxRuleBook.LAND_PROP.Code)
                    Constant.TaxRuleBook.LICENSE.Code -> fetchCMETaxReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.SERVICE_BOOKING_ADVANCE.Code -> fetchServiceBookingAdvanceReceiptDetails(mAdvanceReceivedID)
                    Constant.TaxRuleBook.SERVICE_REQUEST_BOOKING_DETAIL.Code -> fetchServiceRequestBookingDetails(mAdvanceReceivedID)


                    else -> {
                    }
                }
            }
            // endregion

            //region ViolationTicket
            if (it.containsKey(KEY_VIOLATION_TICKET_RESPONSE) && !it.getParcelableArrayList<ViolationTicketResponse>(KEY_VIOLATION_TICKET_RESPONSE).isNullOrEmpty()) {
                val list = it.getParcelableArrayList<ViolationTicketResponse>(KEY_VIOLATION_TICKET_RESPONSE) as ArrayList<ViolationTicketResponse>
                for (item in list) {
                    item.invoiceId?.let {
                        fetchTicketIssueReceiptDetails(it)
                    }
                }
            }
            //endregion

            if (it.containsKey(KEY_QUICK_MENU)) {
                when (val code = it.get(KEY_QUICK_MENU)) {
                    is Constant.QuickMenu -> mQuickMenuCode = code
                    is Constant.NavigationMenu -> mNavigationMenuCode = code
                }
            }
            if (it.containsKey(Constant.KEY_STOP_API_4_PRINT_ALLOW))
                stopPrintAPI = it.getBoolean(Constant.KEY_STOP_API_4_PRINT_ALLOW,false)

        }
    }

    private fun initViews() {
        mAdapter = AllTaxNoticesAdapter(this, isMovable, mQuickMenuCode,stopPrintAPI)
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {
            R.id.btnPrint -> {
                if (MyApplication.sunmiPrinterService != null) {
                    if (mTaxRuleBookCode.isEmpty()) {
                        disableEnablePrintButton(view, false)
                        insertPrintCount(view, position, getTaxNoticeId(obj), "Tax_Notice_Receipt", obj)
                    } else {
                        disableEnablePrintButton(view, false)
                        insertPrintCount(
                            view,
                            position,
                            mAdvanceReceivedID,
                            getReceiptCode(),
                            obj
                        )
                    }
                } else
                    showAlertDialog(getString(R.string.msg_print_not_support)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
            }
        }
    }

    private fun getReceiptCode() :String {
        return when(mTaxRuleBookCode){
            Constant.TaxRuleBook.SERVICE_REQUEST_BOOKING_DETAIL.Code -> {
                "Service_Booking_Request"
            }
            Constant.TaxRuleBook.SERVICE_BOOKING_ADVANCE.Code -> {
                "Service_Booking_Advance_Collection"
            }
            Constant.TaxRuleBook.SALES.Code -> {
                "Sales_Order_Receipt"
            }
            Constant.TaxRuleBook.PENALTY_WAIVE_OFF.Code -> {
                "Penalty_WaiveOff_Receipt"
            }
            else -> {
                "Payment_Receipt"
            }
        }

    }

    private fun disableEnablePrintButton(printButton: View?, enableButton: Boolean){
        printButton?.isEnabled = enableButton
    }

    private fun getTaxNoticeId(obj: Any): Int {
        when (obj) {
            is PDOTaxNoticeResponse -> {
                obj.pdoTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is ROPTaxNoticeResponse -> {
                obj.ropTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is CPTaxNoticeResponse -> {
                obj.cpTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceID ?: 0
                        }
                    }
                }
            }
            is CMETaxNoticeResponse -> {
                obj.cmeTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is RoadTaxNoticeResponse -> {
                obj.roadTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is HotelTaxNoticeResponse -> {
                obj.receiptDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }

            is ShowTaxNoticeResponse -> {
                obj.receiptDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            /*is SalesTaxNoticeResponse -> {
                obj.salesTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }*/
            is AdvertisementTaxNoticeResponse -> {
                obj.advertisementTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is GamingMachineTaxNoticeResponse -> {
                obj.gamingMachineTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceID ?: 0
                        }
                    }
                }
            }
            is CartTaxNoticeResponse -> {
                obj.cartTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceID ?: 0
                        }
                    }
                }
            }
            is WeaponTaxNoticeResponse -> {
                obj.weaponTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is TicketIssueReceiptResponse -> {
                obj.receiptDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is ImpoundmentReceiptResponse -> {
                obj.receiptTable.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is ImpoundmentReturnReceiptResponse -> {
                obj.receiptTable.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is PropertyTaxReceiptResponse -> {
                obj.table.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
            is PropertyLandTaxNoticeResponse -> {
                obj.propertLandTaxNoticeDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }

            is LicenseRenewakNoticeResponse -> {
                obj.receiptDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.taxInvoiceId ?: 0
                        }
                    }
                }
            }
        }
        return 0
    }


    private fun getDocumentName(obj: Any): String? {
        when (obj) {
            is ImpoundmentReceiptResponse -> {
                obj.receiptTable.let {
                    if (it.isNotEmpty()) {
                        it[0].let { item ->
                            return item.impoundmentNumber
                        }
                    }
                }
            }
            is TicketIssueReceiptResponse -> {
                obj.receiptDetails.let {
                    if (it.isNotEmpty()) {
                        it[0].let {
                            return it.ticketNo
                        }
                    }
                }
            }
            else -> return null
        }
        return null
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    //region Receipts
    private fun fetchROPDetails(invoiceID: Int) {
        APICall.getROPTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<ROPTaxNoticeResponse> {
            override fun onSuccess(response: ROPTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })

    }

    private fun fetchPDODetails(invoiceID: Int) {
        APICall.getPDOTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<PDOTaxNoticeResponse> {
            override fun onSuccess(response: PDOTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchCPDetails(invoiceID: Int) {
        APICall.getCPTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<CPTaxNoticeResponse> {
            override fun onSuccess(response: CPTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchCMEDetails(invoiceID: Int) {
        APICall.getCMETaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<CMETaxNoticeResponse> {
            override fun onSuccess(response: CMETaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })

    }

    private fun fetchRoadTaxDetails(invoiceID: Int) {
        APICall.getRoadTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<RoadTaxNoticeResponse> {
            override fun onSuccess(response: RoadTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchSalesTaxDetails(advanceReceivedID: Int) {
        APICall.getSalesTaxNoticePrintingDetails(0, advanceReceivedID, object : ConnectionCallBack<SalesTaxNoticeResponse> {
            override fun onSuccess(response: SalesTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchCMETaxReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchCartTaxReceiptDetails(advanceReceivedID: Int) {
        APICall.getCartTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<CartTaxReceiptResponse> {
            override fun onSuccess(response: CartTaxReceiptResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchCPTaxReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchROPTaxReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchPDOTaxReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchRoadTaxReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchLicenseRenewalDetails(advanceReceivedID: Int) {
        APICall.getLicenseRenewalReceiptDetails(advanceReceivedID, object : ConnectionCallBack<LicenseRenewalReceiptResponse> {
            override fun onSuccess(response: LicenseRenewalReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchAgentReceiptDetails(advanceReceivedID: Int) {
        APICall.getAgentRechargeReceiptDetails(advanceReceivedID, object : ConnectionCallBack<AgentRechargeReceiptResponse> {
            override fun onSuccess(response: AgentRechargeReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchPenaltyReceiptDetails(advanceReceivedID: Int) {
        APICall.getPenaltyReceiptDetails(advanceReceivedID, object : ConnectionCallBack<PenaltyWaiveOffReceiptResponse> {
            override fun onSuccess(response: PenaltyWaiveOffReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchAdvertisementTaxNoticeDetails(invoiceID: Int) {
        APICall.getAdvertisementTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<AdvertisementTaxNoticeResponse> {
            override fun onSuccess(response: AdvertisementTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchAdvertisementReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchShowReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchHotelReceiptDetails(advanceReceivedID: Int) {
        APICall.getTaxReceiptsDetails(0, advanceReceivedID, object : ConnectionCallBack<TaxReceiptsResponse> {
            override fun onSuccess(response: TaxReceiptsResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }


    private fun fetchInitialOutstandingReceiptDetails(advanceReceivedID: Int) {
        APICall.getOutstandingReceiptDetails(advanceReceivedID, object : ConnectionCallBack<InitialOutstandingWaiveOffReceiptResponse> {
            override fun onSuccess(response: InitialOutstandingWaiveOffReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchCartTaxDetails(invoiceID: Int) {
        APICall.getCartTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<CartTaxNoticeResponse> {
            override fun onSuccess(response: CartTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchWeaponTaxDetails(invoiceID: Int) {
        APICall.getWeaponTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<WeaponTaxNoticeResponse> {
            override fun onSuccess(response: WeaponTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun PropertyLandTaxDetails(invoiceID: Int) {
        APICall.getPropertyLandTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<PropertyLandTaxNoticeResponse> {
            override fun onSuccess(response: PropertyLandTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchGamingMachineTaxDetails(invoiceID: Int) {
        APICall.getGamingMachineTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<GamingMachineTaxNoticeResponse> {
            override fun onSuccess(response: GamingMachineTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchBookingRequestReceiptDetails(bookingRequestId: Int) {
        APICall.getBookingRequestReceiptDetails(bookingRequestId, object : ConnectionCallBack<BookingRequestReceiptResponse> {
            override fun onSuccess(response: BookingRequestReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchBookingAdvanceReceiptDetails(advanceReceivedID: Int) {
        APICall.getBookingAdvanceReceiptDetails(advanceReceivedID, object : ConnectionCallBack<BookingAdvanceReceiptResponse> {
            override fun onSuccess(response: BookingAdvanceReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }



    private fun fetchServiceBookingAdvanceReceiptDetails(advanceReceivedID: Int) {
        APICall.getServiceBookingAdvanceReceiptDetails(advanceReceivedID, object : ConnectionCallBack<ServiceBookingAdvanceReceiptResponse> {
            override fun onSuccess(response: ServiceBookingAdvanceReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchServiceRequestBookingDetails(advanceReceivedID: Int) {
        APICall.getServiceRequestBookingDetails(advanceReceivedID, object : ConnectionCallBack<ServiceRequestBookingReceiptResponse> {
            override fun onSuccess(response: ServiceRequestBookingReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchAssetAssignmentReceiptDetails(assetRentId: Int) {
        APICall.getAssetAssignmentAndReturnReceiptDetails(assetRentId, object : ConnectionCallBack<AssetRentAndReturnReceiptResponse> {
            override fun onSuccess(response: AssetRentAndReturnReceiptResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchAssetReturnReceiptDetails(assetRentId: Int) {
        APICall.getAssetAssignmentAndReturnReceiptDetails(assetRentId, object : ConnectionCallBack<AssetRentAndReturnReceiptResponse> {
            override fun onSuccess(response: AssetRentAndReturnReceiptResponse) {
                response.taxRuleBookCode = mTaxRuleBookCode
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchTicketIssueReceiptDetails(invoiceID: Int) {
        APICall.getTicketReceiptDetails(invoiceID, 0, object : ConnectionCallBack<TicketIssueReceiptResponse> {
            override fun onSuccess(response: TicketIssueReceiptResponse) {
                if (response.receiptDetails.isNotEmpty() && response.receiptDetails[0].signatureId != null && response.receiptDetails[0].signatureId != 0) {
                    getAWSPath(response.receiptDetails[0].signatureId ?: 0, response)
                } else
                    mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchImpoundmentReceiptDetails(invoiceID: Int) {
        APICall.getImpoundmentReceiptDetails(invoiceID, 0, object : ConnectionCallBack<ImpoundmentReceiptResponse> {
            override fun onSuccess(response: ImpoundmentReceiptResponse) {
                if (response.receiptTable.isNotEmpty() && response.receiptTable[0].signatureId != null && response.receiptTable[0].signatureId != 0) {
                    getAWSPath(response.receiptTable[0].signatureId ?: 0, response)
                } else
                    mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchImpoundmentReturnReceiptDetails(invoiceID: Int) {
        APICall.getImpoundmentReturnReceiptDetails(invoiceID, 0, object : ConnectionCallBack<ImpoundmentReturnReceiptResponse> {
            override fun onSuccess(response: ImpoundmentReturnReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }
    private fun fetchImpoundmentAnimalReturnReceiptDetails(impoundId: Int, returnLineID : Int) {
        APICall.getImpoundmentAnimalReturnReceiptDetails(impoundId, returnLineID, object : ConnectionCallBack<ImpoundmentReturnReceiptResponse> {
            override fun onSuccess(response: ImpoundmentReturnReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchTrafficTicketPaymentReceiptDetails(advanceReceivedID: Int, screenType: String) {
        APICall.getTicketPaymentReceiptDetails(0, advanceReceivedID, object : ConnectionCallBack<TicketPaymentReceiptResponse> {
            override fun onSuccess(response: TicketPaymentReceiptResponse) {
                mAdapter?.addItemWithType(response, screenType)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchParkingTicketPaymentReceiptDetails(advanceReceivedID: Int) {
        APICall.getParkingTicketPaymentReceiptDetails(0, advanceReceivedID, object : ConnectionCallBack<ParkingTicketPaymentReceiptResponse> {
            override fun onSuccess(response: ParkingTicketPaymentReceiptResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchPropertyTaxPaymentReceiptDetails(advanceReceivedID: Int, screenType: String) {
        APICall.getTaxPaymentReceiptDetails(0, advanceReceivedID, object : ConnectionCallBack<PropertyTaxReceiptResponse> {
            override fun onSuccess(response: PropertyTaxReceiptResponse) {
                mAdapter?.addItemWithType(response, screenType)
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun fetchHotelTaxNoticeDetails(invoiceID: Int) {
        APICall.getHotelTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<HotelTaxNoticeResponse> {
            override fun onSuccess(response: HotelTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun fetchShowTaxNoticeDetails(invoiceID: Int) {
        APICall.getShowTaxNoticePrintingDetails(invoiceID, 0, object : ConnectionCallBack<ShowTaxNoticeResponse> {
            override fun onSuccess(response: ShowTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }
    private fun getLicenseRenewalTaxNoticeDetails(invoiceID: Int) {
        APICall.getLicenseRenewalTaxNoticeDetails(invoiceID, 0, object : ConnectionCallBack<LicenseRenewakNoticeResponse> {
            override fun onSuccess(response: LicenseRenewakNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {
            }
        })
    }

    private fun getServiceTaxNoticeDetails(invoiceID: Int){
        APICall.getServiceTaxNoticePrintingDetails(invoiceID,0, object : ConnectionCallBack<ServiceTaxNoticeResponse>{
            override fun onSuccess(response: ServiceTaxNoticeResponse) {
                mAdapter?.addItem(response)
            }

            override fun onFailure(message: String) {

            }
        })
    }
    //endregion

    //region Downloading AWS path from Server
    private fun getAWSPath(documentID: Int, obj: Any) {
        APICall.downloadAWSPath(documentID, object : ConnectionCallBack<String> {
            override fun onSuccess(response: String) {
                when (obj) {
                    is TicketIssueReceiptResponse -> {
                        obj.receiptDetails.let {
                            if (it.isNotEmpty()) it[0].awsPath = response
                        }
                    }

                    is ImpoundmentReceiptResponse -> {
                        obj.receiptTable.let {
                            if (it.isNotEmpty()) it[0].awsPath = response
                        }
                    }
                }
                mAdapter?.addItem(obj)
            }

            override fun onFailure(message: String) {

            }
        })
    }
    //endregiion

    private fun insertPrintCount(printButtonView: View, position: Int, receiptId: Int, receiptCode: String, obj: Any) {
        //val view = mBinding.recyclerView.getChildAt(position).findViewById<LinearLayout>(R.id.llReceiptBody)
        val view = mBinding.recyclerView.layoutManager?.findViewByPosition(position)?.findViewById<LinearLayout>(R.id.llReceiptBody)
        if (view != null) {
            //val printBody = loadBitmapFromView(view)
            val printBody =view.drawToBitmap()
            val resizedPrintBody = resize(printBody)

            APICall.insertPrintRequest(getAppReceiptPrint(receiptId, receiptCode), object : ConnectionCallBack<Int> {
                override fun onSuccess(response: Int) {
                    disableEnablePrintButton(printButtonView, true)

                    if (mTaxRuleBookCode.isEmpty()) {
                        printHelper.printBitmap(resizedPrintBody)
                        val intent = Intent(this@AllTaxNoticesActivity, TaxNoticeCaptureActivity::class.java)
                        intent.putExtra("KEY_TAX_NOTICE_ID", receiptId.toString())
                        if (mQuickMenuCode != Constant.QuickMenu.QUICK_MENU_NONE) {
                            intent.putExtra(KEY_QUICK_MENU, mQuickMenuCode)
                            if (mQuickMenuCode == Constant.QuickMenu.QUICK_MENU_IMPONDMENT || mQuickMenuCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE){
                                intent.putExtra("KEY_TAX_NOTICE_ID", mAdvanceReceivedID.toString())
                                intent.putExtra(KEY_DOCUMENT_NAME, getDocumentName(obj))
                            }
                            startActivity(intent)
                            finish()
                        }
                        if (mNavigationMenuCode != Constant.NavigationMenu.NAVIGATION_MENU_NONE) {
                            intent.putExtra(KEY_QUICK_MENU, mNavigationMenuCode)
                            startActivity(intent)
                            finish()
                        }
                    }  else {
                        printHelper.printBitmap(resizedPrintBody)
                        if (mTaxRuleBookCode == Constant.TaxRuleBook.SERVICE_REQUEST_BOOKING_DETAIL.Code) {
                            val intent = Intent()
                            intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.SERVICE_REQUEST_BOOKING_DETAIL.Code)
                            setResult(Activity.RESULT_OK, intent)
                        }
                        finish()
                    }
                }

                override fun onFailure(message: String) {
                    disableEnablePrintButton(printButtonView, true)
                }
            })
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }


    private fun getAppReceiptPrint(Id: Int, receiptType: String): AppReceiptPrint {
        val appReceiptPrint = AppReceiptPrint()
        appReceiptPrint.printDateTime = getDate(Date(), DateTimeTimeZoneMillisecondFormat)
        appReceiptPrint.receiptCode = receiptType
        appReceiptPrint.primaryKeyValue = Id
        return appReceiptPrint
    }


}