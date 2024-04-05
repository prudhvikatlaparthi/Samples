package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.*
import com.sgs.citytax.ui.viewHolder.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class AllTaxNoticesAdapter(
    private val iClickListener: IClickListener,
    val isMovable: Boolean,
    val fromScreen: Constant.QuickMenu,
    private val stopPrintAPI: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList: ArrayList<Any> = arrayListOf()
    private var screenType: String = ""

    fun addItem(list: Any) {
        mList.add(list)
        notifyDataSetChanged()
    }

    fun addItemWithType(list: Any, screenType: String) {
        mList.add(list)
        this.screenType = screenType
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Constant.TaxNotice.CME.Type -> {
                CMETaxViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_cme_tax_notice, parent, false))
            }
            Constant.TaxNotice.CP.Type -> {
                CPTaxViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_cp_tax_notice, parent, false))
            }
            Constant.TaxNotice.ROP.Type -> {
                ROPTaxViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_rop_tax_notice, parent, false))
            }
            Constant.TaxNotice.PDO.Type -> {
                PDOTaxViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_pdo_tax_notice, parent, false))
            }
            Constant.TaxNotice.ROAD_TAX.Type -> {
                RoadTaxViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_road_tax_notice, parent, false))
            }
            Constant.TaxNotice.COM_PROP.Type -> {
                PropertyLandTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_property_tax_notice, parent, false))
            }
            Constant.TaxNotice.SALES_TAX.Type -> {
                SalesTaxViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_sales_tax_notice, parent, false))
            }
            Constant.TaxNotice.ADVERTISEMENT_TAX.Type -> {
                AdvertisementTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_advertisement_tax_notice, parent, false))
            }
            Constant.TaxNotice.HOTEL.Type -> {
                HotelTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_hotel_tax_notice, parent, false))
            }
            Constant.TaxNotice.SHOW.Type -> {
                ShowTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_show_tax_notice, parent, false))
            }
            Constant.TaxNotice.LICENSE.Type -> {
                LicenseRenewalTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_license_renewal_tax_notice, parent, false))
            }
            Constant.TaxNotice.SERVICE_NOTICE.Type -> {
                ServiceTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_service_tax_notice, parent, false))
            }

            Constant.TaxNotice.CART_TAX.Type -> {
                CartTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_cart_tax_notice, parent, false))
            }
            Constant.TaxNotice.GAME_TAX.Type -> {
                GamingMachineTaxNoticeViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_gaming_tax_notice, parent, false))
            }
            Constant.TaxNotice.WEAPON_TAX.Type -> {
                WeaponTaxNoticetViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_weapon_tax_notice, parent, false))
            }
            Constant.TaxReceipt.CART_RECEIPT.Type -> {
                CartTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_cart_payment_receipt, parent, false))
            }
            Constant.TaxReceipt.GAME_RECEIPT.Type -> {
                GamingTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_gaming_payment_receipt, parent, false))
            }
            Constant.TaxReceipt.WEAPON_RECEIPT.Type -> {
                WeaponTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_weapon_payment_receipt, parent, false))
            }

            Constant.TaxReceipt.CME.Type -> {
                CMETaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_cme_tax_receipt, parent, false))
            }
            Constant.TaxReceipt.CP.Type -> {
                CPTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_cp_tax_receipt, parent, false))
            }
            Constant.TaxReceipt.LICENSE.Type -> {
                LicenseTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_license_tax_receipt, parent, false))
            }
            Constant.TaxReceipt.ROP.Type -> {
                ROPTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_rop_tax_receipt, parent, false))
            }
            Constant.TaxReceipt.PDO.Type -> {
                PDOTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_pdo_tax_receipt, parent, false))
            }
            Constant.TaxReceipt.LICENSE_RENEWAL.Type -> {
                PayPointLicenseRenewalViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_license_renewal_payment_receipt, parent, false))
            }
            Constant.TaxReceipt.RECHARGE.Type -> {
                AgentRechargeReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_agent_wallet_recharge_receipt, parent, false))
            }
            Constant.TaxReceipt.ROAD_TAX.Type -> {
                RoadTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_road_tax_receipt, parent, false))
            }
            Constant.TaxReceipt.PENALTY_WAIVE_OFF.Type -> {
                PenaltyWaiveOffReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_penalty_waive_off_receipt, parent, false))
            }
            Constant.TaxReceipt.ADVERTISEMENT.Type -> {
                AdvertisementTaxReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_advertisement_tax_receipt, parent, false))
            }
            Constant.TaxReceipt.SHOW.Type -> {
                ShowPaymentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_show_payment_receipt, parent, false))
            }
            Constant.TaxReceipt.HOTEL.Type -> {
                HotelPaymentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_hotel_payment_receipt, parent, false))
            }
            Constant.TaxReceipt.INITIAL_OUTSTANDING_PENALTY_WAIVE_OFF.Type -> {
                InitialOutstandingPenaltyWaiveOfReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_initial_outstanding_penalty_waive_off_receipt, parent, false))
            }
            Constant.TaxReceipt.BOOKING_REQUEST_RECEIPT.Type -> {
                BookingRequestReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_booking_request_receipt, parent, false))
            }
            Constant.TaxReceipt.BOOKING_ADVANCE_RECEIPT.Type -> {
                BookingAdvanceReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_booking_advance_receipt, parent, false))
            }
            Constant.TaxReceipt.SERVICE_BOOKING_ADVANCE.Type -> {
                ServiceAdvanceReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_service_booking_advance_receipt, parent, false))
            }
            Constant.TaxReceipt.SERVICE_REQUEST_BOOKING_DETAIL.Type -> {
                ServiceABookingRequestReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_service_booking_request_receipt, parent, false))
            }
            Constant.TaxReceipt.ASSET_ASSIGNMENT.Type -> {
                AssetAssignmentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_asset_assignment_receipt, parent, false))
            }
            Constant.TaxReceipt.ASSET_RETURN.Type -> {
                AssetReturnReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_asset_return_receipt, parent, false))
            }
            Constant.TaxNotice.TICKET_ISSUE.Type -> {
                TicketIssueViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_ticket_issue_receipt, parent, false))
            }
            Constant.TaxNotice.IMPOUNDMENT.Type -> {
                ImpoundmentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_impoundment_receipt, parent, false))
            }
            Constant.TaxNotice.IMPOUNDMENT_RETURN.Type -> {
                ImpoundmentReturnReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_impoundment_return_receipt, parent, false))
            }
            Constant.TaxReceipt.TICKET_PAYMENT.Type -> {
                TicketPaymentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_traffic_ticket_payment_receipt, parent, false), Constant.TaxReceipt.TICKET_PAYMENT.Type,stopPrintAPI)
            }
            Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type -> {
                TicketPaymentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_traffic_ticket_payment_receipt, parent, false), Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type,stopPrintAPI)
            }
            Constant.TaxReceipt.PARKING_TICKET_PAYMENT.Type -> {
                ParkingTicketPaymentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_parking_ticket_payment_receipt, parent, false))
            }
            Constant.TaxReceipt.PROPERTY_COM_PAYMENT_TRANSACTION.Type -> {
                PropertyTaxPaymentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_property_payment_receipt, parent, false), screenType)
            }
            Constant.TaxReceipt.PROPERTY_RES_PAYMENT_TRANSACTION.Type -> {
                PropertyTaxPaymentReceiptViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_property_payment_receipt, parent, false), screenType)
            }
            else -> {
                CMETaxViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_cme_tax_notice, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (mList[position]) {
            is CMETaxNoticeResponse -> Constant.TaxNotice.CME.Type
            is CPTaxNoticeResponse -> Constant.TaxNotice.CP.Type
            is ROPTaxNoticeResponse -> Constant.TaxNotice.ROP.Type
            is PDOTaxNoticeResponse -> Constant.TaxNotice.PDO.Type
            is RoadTaxNoticeResponse -> Constant.TaxNotice.ROAD_TAX.Type
            is SalesTaxNoticeResponse -> Constant.TaxNotice.SALES_TAX.Type
            is AdvertisementTaxNoticeResponse -> Constant.TaxNotice.ADVERTISEMENT_TAX.Type
            is CartTaxNoticeResponse -> Constant.TaxNotice.CART_TAX.Type
            is WeaponTaxNoticeResponse -> Constant.TaxNotice.WEAPON_TAX.Type
            is GamingMachineTaxNoticeResponse -> Constant.TaxNotice.GAME_TAX.Type
            is HotelTaxNoticeResponse -> Constant.TaxNotice.HOTEL.Type
            is ShowTaxNoticeResponse -> Constant.TaxNotice.SHOW.Type
            is LicenseRenewakNoticeResponse -> Constant.TaxNotice.LICENSE.Type
            is ServiceTaxNoticeResponse -> Constant.TaxNotice.SERVICE_NOTICE.Type
            is TaxReceiptsResponse -> {
                when ((mList[position] as TaxReceiptsResponse).taxRuleBookCode?.toUpperCase()) {
                    Constant.TaxRuleBook.CME.Code -> Constant.TaxReceipt.CME.Type
                    Constant.TaxRuleBook.CP.Code -> Constant.TaxReceipt.CP.Type
                    Constant.TaxRuleBook.ROP.Code -> Constant.TaxReceipt.ROP.Type
                    Constant.TaxRuleBook.PDO.Code -> Constant.TaxReceipt.PDO.Type
                    Constant.TaxRuleBook.DEFAULT.Code -> Constant.TaxReceipt.ROAD_TAX.Type
                    Constant.TaxRuleBook.ADVERTISEMENT.Code -> Constant.TaxReceipt.ADVERTISEMENT.Type
                    Constant.TaxRuleBook.SHOW.Code -> Constant.TaxReceipt.SHOW.Type
                    Constant.TaxRuleBook.HOTEL.Code -> Constant.TaxReceipt.HOTEL.Type
                    Constant.TaxRuleBook.LICENSE.Code -> Constant.TaxReceipt.LICENSE.Type
                    else -> 0
                }
            }
            is LicenseRenewalReceiptResponse -> Constant.TaxReceipt.LICENSE_RENEWAL.Type
            is AgentRechargeReceiptResponse -> Constant.TaxReceipt.RECHARGE.Type
            is PenaltyWaiveOffReceiptResponse -> Constant.TaxReceipt.PENALTY_WAIVE_OFF.Type
            is InitialOutstandingWaiveOffReceiptResponse -> Constant.TaxReceipt.INITIAL_OUTSTANDING_PENALTY_WAIVE_OFF.Type
            is BookingRequestReceiptResponse -> Constant.TaxReceipt.BOOKING_REQUEST_RECEIPT.Type
            is BookingAdvanceReceiptResponse -> Constant.TaxReceipt.BOOKING_ADVANCE_RECEIPT.Type
            is ServiceBookingAdvanceReceiptResponse -> Constant.TaxReceipt.SERVICE_BOOKING_ADVANCE.Type
            is ServiceRequestBookingReceiptResponse -> Constant.TaxReceipt.SERVICE_REQUEST_BOOKING_DETAIL.Type
            is CartTaxReceiptResponse -> {
                when ((mList[position] as CartTaxReceiptResponse).taxRuleBookCode?.toUpperCase()) {
                    Constant.TaxRuleBook.CART.Code -> Constant.TaxReceipt.CART_RECEIPT.Type
                    Constant.TaxRuleBook.GAME.Code -> Constant.TaxReceipt.GAME_RECEIPT.Type
                    Constant.TaxRuleBook.WEAPON.Code -> Constant.TaxReceipt.WEAPON_RECEIPT.Type

                    else -> 0
                }

            }
            is AssetRentAndReturnReceiptResponse -> {
                when ((mList[position] as AssetRentAndReturnReceiptResponse).taxRuleBookCode?.toUpperCase()) {
                    Constant.TaxRuleBook.ASSET_ASSIGNMENT.Code -> Constant.TaxReceipt.ASSET_ASSIGNMENT.Type
                    Constant.TaxRuleBook.ASSET_RETURN.Code -> Constant.TaxReceipt.ASSET_RETURN.Type

                    else -> 0
                }
            }
            is TicketIssueReceiptResponse -> Constant.TaxNotice.TICKET_ISSUE.Type
            is ImpoundmentReceiptResponse -> Constant.TaxNotice.IMPOUNDMENT.Type
            is ImpoundmentReturnReceiptResponse -> Constant.TaxNotice.IMPOUNDMENT_RETURN.Type
            is PropertyLandTaxNoticeResponse -> Constant.TaxNotice.COM_PROP.Type

            is TicketPaymentReceiptResponse -> {
                when (screenType) {
                    Constant.TaxRuleBook.TICKET_PAYMENT.Code -> Constant.TaxReceipt.TICKET_PAYMENT.Type
                    Constant.TaxRuleBook.TICKET_PAYMENT_TRANSACTION.Code -> Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type
                    else -> 0
                }
            }
            is TicketPaymentReceiptResponse -> Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type
            is ParkingTicketPaymentReceiptResponse -> Constant.TaxReceipt.PARKING_TICKET_PAYMENT.Type
            is PropertyTaxReceiptResponse -> Constant.TaxReceipt.PROPERTY_COM_PAYMENT_TRANSACTION.Type
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Constant.TaxNotice.CME.Type -> {
                val response = mList[position] as CMETaxNoticeResponse
                (holder as CMETaxViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.CP.Type -> {
                val response = mList[position] as CPTaxNoticeResponse
                (holder as CPTaxViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.ROP.Type -> {
                val response = mList[position] as ROPTaxNoticeResponse
                (holder as ROPTaxViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.PDO.Type -> {
                val response = mList[position] as PDOTaxNoticeResponse
                (holder as PDOTaxViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.ROAD_TAX.Type -> {
                val response = mList[position] as RoadTaxNoticeResponse
                (holder as RoadTaxViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.COM_PROP.Type -> {
                val response = mList[position] as PropertyLandTaxNoticeResponse
                (holder as PropertyLandTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.SALES_TAX.Type -> {
                val response = mList[position] as SalesTaxNoticeResponse
                (holder as SalesTaxViewHolder).bind(response, iClickListener, fromScreen)
            }
            Constant.TaxNotice.ADVERTISEMENT_TAX.Type -> {
                val response = mList[position] as AdvertisementTaxNoticeResponse
                (holder as AdvertisementTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.CART_TAX.Type -> {
                val response = mList[position] as CartTaxNoticeResponse
                (holder as CartTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.WEAPON_TAX.Type -> {
                val response = mList[position] as WeaponTaxNoticeResponse
                (holder as WeaponTaxNoticetViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.GAME_TAX.Type -> {
                val response = mList[position] as GamingMachineTaxNoticeResponse
                (holder as GamingMachineTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.HOTEL.Type -> {
                val response = mList[position] as HotelTaxNoticeResponse
                (holder as HotelTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.SHOW.Type -> {
                val response = mList[position] as ShowTaxNoticeResponse
                (holder as ShowTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.LICENSE.Type -> {
                val response = mList[position] as LicenseRenewakNoticeResponse
                (holder as LicenseRenewalTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.SERVICE_NOTICE.Type -> {
                val response = mList[position] as ServiceTaxNoticeResponse
                (holder as ServiceTaxNoticeViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.CART_RECEIPT.Type -> {
                val response1 = mList[position] as CartTaxReceiptResponse
                (holder as CartTaxReceiptViewHolder).bind(response1, iClickListener)
            }
            Constant.TaxReceipt.GAME_RECEIPT.Type -> {
                val response = mList[position] as CartTaxReceiptResponse
                (holder as GamingTaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.WEAPON_RECEIPT.Type -> {
                val response = mList[position] as CartTaxReceiptResponse
                (holder as WeaponTaxReceiptViewHolder).bind(response, iClickListener)
            }

            Constant.TaxReceipt.CME.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as CMETaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.CP.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as CPTaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.ROP.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as ROPTaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.PDO.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as PDOTaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.LICENSE_RENEWAL.Type -> {
                val response = mList[position] as LicenseRenewalReceiptResponse
                (holder as PayPointLicenseRenewalViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.RECHARGE.Type -> {
                val response: AgentRechargeReceiptResponse = mList[position] as AgentRechargeReceiptResponse
                (holder as AgentRechargeReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.ROAD_TAX.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as RoadTaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.PENALTY_WAIVE_OFF.Type -> {
                val response: PenaltyWaiveOffReceiptResponse = mList[position] as PenaltyWaiveOffReceiptResponse
                (holder as PenaltyWaiveOffReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.ADVERTISEMENT.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as AdvertisementTaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.SHOW.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as ShowPaymentReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.HOTEL.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as HotelPaymentReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.LICENSE.Type -> {
                val response = mList[position] as TaxReceiptsResponse
                (holder as LicenseTaxReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.INITIAL_OUTSTANDING_PENALTY_WAIVE_OFF.Type -> {
                val response = mList[position] as InitialOutstandingWaiveOffReceiptResponse
                (holder as InitialOutstandingPenaltyWaiveOfReceiptViewHolder).bind(response, iClickListener, fromScreen)
            }
            Constant.TaxReceipt.BOOKING_REQUEST_RECEIPT.Type -> {
                val response = mList[position] as BookingRequestReceiptResponse
                (holder as BookingRequestReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.BOOKING_ADVANCE_RECEIPT.Type -> {
                val response = mList[position] as BookingAdvanceReceiptResponse
                (holder as BookingAdvanceReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.SERVICE_BOOKING_ADVANCE.Type -> {
                val response = mList[position] as ServiceBookingAdvanceReceiptResponse
                (holder as ServiceAdvanceReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.SERVICE_REQUEST_BOOKING_DETAIL.Type -> {
                val response = mList[position] as ServiceRequestBookingReceiptResponse
                (holder as ServiceABookingRequestReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.ASSET_ASSIGNMENT.Type -> {
                val response = mList[position] as AssetRentAndReturnReceiptResponse
                (holder as AssetAssignmentReceiptViewHolder).bind(response, iClickListener, isMovable)
            }
            Constant.TaxReceipt.ASSET_RETURN.Type -> {
                val response = mList[position] as AssetRentAndReturnReceiptResponse
                (holder as AssetReturnReceiptViewHolder).bind(response, iClickListener, isMovable)
            }
            Constant.TaxNotice.TICKET_ISSUE.Type -> {
                val response = mList[position] as TicketIssueReceiptResponse
                (holder as TicketIssueViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.IMPOUNDMENT.Type -> {
                val response = mList[position] as ImpoundmentReceiptResponse
                (holder as ImpoundmentReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxNotice.IMPOUNDMENT_RETURN.Type -> {
                val response = mList[position] as ImpoundmentReturnReceiptResponse
                (holder as ImpoundmentReturnReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.TICKET_PAYMENT.Type -> {
                val response = mList[position] as TicketPaymentReceiptResponse
                (holder as TicketPaymentReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.TICKET_PAYMENT_TRANSACTION.Type -> {
                val response = mList[position] as TicketPaymentReceiptResponse
                (holder as TicketPaymentReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.PARKING_TICKET_PAYMENT.Type -> {
                val response = mList[position] as ParkingTicketPaymentReceiptResponse
                (holder as ParkingTicketPaymentReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.PROPERTY_COM_PAYMENT_TRANSACTION.Type -> {
                val response = mList[position] as PropertyTaxReceiptResponse
                (holder as PropertyTaxPaymentReceiptViewHolder).bind(response, iClickListener)
            }
            Constant.TaxReceipt.PROPERTY_RES_PAYMENT_TRANSACTION.Type -> {
                val response = mList[position] as PropertyTaxReceiptResponse
                (holder as PropertyTaxPaymentReceiptViewHolder).bind(response, iClickListener)
            }
            else -> {

            }
        }
    }

}