package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CPTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemCpCarrierDeterminedTaxBinding
import com.sgs.citytax.databinding.ItemCpCarrierVariableTaxBinding
import com.sgs.citytax.databinding.ItemCpTaxNoticeBinding
import com.sgs.citytax.model.CPCarrierDeterminedTax
import com.sgs.citytax.model.CPCarrierPrapotaionalTax
import com.sgs.citytax.model.CPCarrierVariableTax
import com.sgs.citytax.model.CPTaxNoticeDetails
import com.sgs.citytax.util.*
import java.util.*

class CPTaxViewHolder(val mBinding: ItemCpTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {

    private var mTotalFixedAmountOfCarrier: Double? = 0.00
    private var mTotalVariableAmountForCarriers: Double? = 0.00
    private var xValue: Double? = 0.00

    fun bind(taxDetails: CPTaxNoticeResponse, iClickListener: IClickListener?) {

        bindDeterminedTaxes(taxDetails.determinedTaxes)
        bindVariableTaxes(taxDetails.variableTaxes)
        bindPraptional(taxDetails.prapotionalTaxes[0])
        bindCP(taxDetails.cpTaxNoticeDetails[0],taxDetails)


        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }

    }

    private fun bindPraptional(cpCarrierPrapotaionalTax: CPCarrierPrapotaionalTax)
    {
        cpCarrierPrapotaionalTax.rentPerYear?.let {
            mBinding.txtRentalAmountPerYear.text = formatWithPrecision(it)
        }
        cpCarrierPrapotaionalTax.rentApplied?.let {
            if (it.equals("Y"))
            {
                cpCarrierPrapotaionalTax.rentTaxRate?.let {
                    if(it.contains("%")){
                        mBinding.txtRate.text = getTariffWithPercentage(it)
                    }
                    else {
                        mBinding.txtRate.text = formatWithPrecision(it)
                    }
                }
                cpCarrierPrapotaionalTax.rentTaxAmount?.let {
                    mBinding.txtProportionalLaw.text = formatWithPrecision(it)
                    xValue = xValue?.plus(it)
                }

            }
            else
            {
                if (cpCarrierPrapotaionalTax.fixedPriceApplied.equals("Y"))
                {
                    var price = cpCarrierPrapotaionalTax.priceFactor!! * cpCarrierPrapotaionalTax.fixedPriceTurnoverTax!!
                    mBinding.txtRate.text = formatWithPrecision(price)
                    cpCarrierPrapotaionalTax.fixedPriceTaxAmount?.let {
                        mBinding.txtProportionalLaw.text = formatWithPrecision(it)
                        xValue = xValue?.plus(it)
                    }
                }
                else
                {

                }
            }
        }

    }

    private fun bindDeterminedTaxes(determinedTaxes: List<CPCarrierDeterminedTax>?) {
        if (!determinedTaxes.isNullOrEmpty()) {
            mBinding.llCarrierDeterminedTax.removeAllViews()
            for (determinedTax in determinedTaxes) {
                val determinedBinding: ItemCpCarrierDeterminedTaxBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context)
                        , R.layout.item_cp_carrier_determined_tax, mBinding.llCarrierDeterminedTax, false)

                determinedTax.vehicleType?.let {
                    determinedBinding.txtCarrierType.text = it
                }

                determinedTax.vehicleCount?.let {
                    determinedBinding.txtNoOfCarrier.text = "$it"
                }

                determinedTax.fixedAmountForVehicleType?.let {
                    determinedBinding.txtFixedAmountForCarrierType.text = formatWithPrecision(it)
                    xValue = xValue?.plus(it)
                }

                determinedTax.totalFixedTaxAmount?.let {
                    determinedBinding.txtTotalFixedAmountOfCarrier.text = formatWithPrecision(it)
                    mTotalFixedAmountOfCarrier = mTotalFixedAmountOfCarrier?.plus(it)
                }

                mBinding.llCarrierDeterminedTax.addView(determinedBinding.root)
            }
            mTotalFixedAmountOfCarrier?.let {
                mBinding.llTotalFixedAmountOfCarrier.visibility = View.VISIBLE
                mBinding.txtTotalFixedAmountOfCarrier.text = formatWithPrecision(it)
            }
        }
    }

    private fun bindVariableTaxes(variableTaxes: List<CPCarrierVariableTax>?) {
        if (!variableTaxes.isNullOrEmpty()) {
            mBinding.llCarrierVariableTax.removeAllViews()
            for (variableTax in variableTaxes) {
                val variableBinding: ItemCpCarrierVariableTaxBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context)
                        , R.layout.item_cp_carrier_variable_tax, mBinding.llCarrierVariableTax, false)

                variableTax.vehicleType?.let {
                    variableBinding.txtTypeOfTransport.text = it
                }

                variableTax.tarifPerSeat?.let {
                    variableBinding.txtTarifPerSeat.text = formatWithPrecision(it)
                }

                variableTax.totalSeats?.let {
                    variableBinding.txtTotalSeats.text = "$it"
                }

                variableTax.tarifPerTon?.let {
                    variableBinding.txtTarifPerTon.text = formatWithPrecision(it)
                }

                variableTax.totalTonCapacity?.let {
                    variableBinding.txtTotalTonCapacity.text = getQuantity(it.toString())
                }

                variableTax.totalAmount?.let {
                    variableBinding.txtTotalTotalAmount.text = formatWithPrecision(it)
                }

                mTotalVariableAmountForCarriers = mTotalVariableAmountForCarriers?.plus(variableTax.totalAmount
                        ?: 0.0)


                mBinding.llCarrierVariableTax.addView(variableBinding.root)
            }
        }

    }

    private fun bindCP(taxDetails: CPTaxNoticeDetails?, cpTaxNoticeResponse: CPTaxNoticeResponse) {
        val sector = mBinding.txtSectorLabel.context.getString(R.string.sector)
        mBinding.txtSectorLabel.text = String.format("%s%s", sector, getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = cpTaxNoticeResponse.orgData
        )
        if (taxDetails != null) {

            taxDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
  //                  mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }

            //region QrCode
            taxDetails.taxInvoiceID?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, taxDetails.taxInvoiceID.toString(), taxDetails.sycoTaxID
                        ?: ""))
            }
            //endregion

            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            taxDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNo.text = it.toString()
            }

            //region Required On
            taxDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = "$it"
            }
            //endregion

            taxDetails.taxInvoiceDate?.let {
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(it)
            }

            taxDetails.startDate?.let{
                mBinding.txtStartDate.text = displayFormatDate(it)
            }

            //region Activity Code
//            taxDetails.product?.let {
//                mBinding.txtActivityCode.text = it
//            }
            //endregion

            //region Social Reason
            taxDetails.businessName?.let {
                mBinding.txtBusinessName.text = it
            }

            taxDetails.businessType?.let {
                mBinding.txtBusinessType.text = it
            }
            //endregion

            //region SycoTaxID
            taxDetails.sycoTaxID?.let {
                mBinding.txtSycoTaxID.text = it
            }
            //endregion

            //region TradeNo
            taxDetails.tradeNo?.let {
                mBinding.txtTradeRegisterNo.text = it
            }
            //endregion

            //region IFU
            taxDetails.ifu?.let {
                mBinding.txtIFUNo.text = it
            }
            //endregion

            //region BusinesssMobile
            taxDetails.businessMobile?.let {
                mBinding.txtPhoneNumber.text = it
            }
            //endregion

            //region ARDT
            taxDetails.zone?.let {
                mBinding.txtArdt.text = it
            }
            //endregion

            //region Sector
            taxDetails.sector?.let {
                mBinding.txtSector.text = it
            }
            //endregion

            //region Lot
            taxDetails.block?.let {
                mBinding.txtLot.text = it
            }
            //endregion

            //region Section
            taxDetails.plot?.let {
                mBinding.txtSection.text = it
            }
            //endregion

            taxDetails.doorNo?.let{
                mBinding.txtParcel.text = it
            }

            //region Board
            taxDetails.product?.let {
                mBinding.txtBoard.text = it
            }
            //endregion

            //region Sales
            taxDetails.turnOverAmount?.let {
                mBinding.txtSales.text = formatWithPrecision(it)
            }
            //endregion

            //region Amount
            taxDetails.turnOverTaxAmount?.let {
                mBinding.txtAmount.text = formatWithPrecision(it)
                xValue = xValue?.plus(it)
            }
            //endregion

            //region Total Variable Amount For Carriers
            mTotalVariableAmountForCarriers?.let {
                mBinding.txtTotalVariableAmountForCarriers.text = formatWithPrecision(mTotalVariableAmountForCarriers)
                xValue = xValue?.plus(it)
            }
            //endregion

            //region Total Fixed Duty and Variable Tax Of Carriers
            val totalFixedDutyAndVariableTaxOfCarriers = mTotalVariableAmountForCarriers?.plus(mTotalFixedAmountOfCarrier
                    ?: 0.0)
            mBinding.txtTotalFixedDutyAndVariableTaxOfCarriers.text = formatWithPrecision(totalFixedDutyAndVariableTaxOfCarriers
                    ?: 0.0)
            //endregion

            //region Base
            //endregion

            //region Rate
            //endregion

            //region Proportional Law
            //endregion

            //region Patent To Pay
            //endregion
            var totalDueAmount: Double? = 0.0

            //region Amount of tax for the current year
            mBinding.txtAmountOfTaxForTheCurrentYear.text = formatWithPrecision(0.0)
            taxDetails.amountDueCurrentYear?.let {
                    mBinding.txtAmountOfTaxForTheCurrentYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }
            //endregion

            //region RAR from anterior year
            mBinding.txtRARFromCurrentYear.text = formatWithPrecision(0.0)
            taxDetails.amountDueAnteriorYear?.let {
                    mBinding.txtRARFromCurrentYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }
            //endregion

            //region RAR of the previous year
            mBinding.txtRARFromPreviousYear.text = formatWithPrecision(0.0)
            taxDetails.amountDuePreviousYear?.let {
                    mBinding.txtRARFromPreviousYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }
            //endregion

            //region Amount of penalties
            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            taxDetails.penaltyDue?.let {
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }
            //endregion

            xValue?.let {
                mBinding.txtPatentToPay.text = formatWithPrecision(it)
//                totalDueAmount = totalDueAmount?.plus(it)
            }

            //region HAS ADOPTED THIS NOTICE TO THE SUM
            totalDueAmount?.let {
                mBinding.txtDueTotalAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtHasAdoptedThisNoticeToTheSum)
            }
            //endregion

            taxDetails.note?.let{
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it,0)
            }

            //region Generated by
            taxDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it
            }

            taxDetails.taxRefundDemandAmount?.let {
                if (it > 0.0 || it == 0.0)
                {
                    mBinding.llTaxAmountRefund.visibility = View.VISIBLE
                    mBinding.txtTaxAmountRefund.text = formatWithPrecision(it)
                }
                else
                {
                    mBinding.llTaxAmountDemand.visibility = View.VISIBLE
                    mBinding.txtTaxAmountDemand.text = formatWithPrecision(it)
                }
            }

            //endregion
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(taxDetails.taxInvoiceID!!, mBinding.btnPrint)
            }
        }
    }

}