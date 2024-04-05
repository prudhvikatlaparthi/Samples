package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GamingMachineTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemGamingTaxNoticeBinding
import com.sgs.citytax.model.GamingMachineTaxNoticeDetails
import com.sgs.citytax.util.*
import java.util.*

class GamingMachineTaxNoticeViewHolder(val mBinding: ItemGamingTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(taxDetails: GamingMachineTaxNoticeResponse, iClickListener: IClickListener?) {

        bindTaxDetails(taxDetails.gamingMachineTaxNoticeDetails[0],taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }
    }

    private fun bindTaxDetails(
        receiptDetails: GamingMachineTaxNoticeDetails?,
        taxDetails: GamingMachineTaxNoticeResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxDetails.orgData
        )
        mBinding.txtNoticeNumberLabel.text = String.format("%s%s", getString(R.string.notice_number), getString(R.string.colon))
        mBinding.txtProfessionLabel.text = String.format("%s%s", getString(R.string.profession), getString(R.string.colon))
        mBinding.txtStateLabel.text = String.format("%s%s", getString(R.string.state), getString(R.string.colon))
        mBinding.txtCityLabel.text = String.format("%s%s", getString(R.string.city), getString(R.string.colon))
        mBinding.txtAddressLabel.text = String.format("%s%s", getString(R.string.title_address), getString(R.string.colon))
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))


        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
        if (receiptDetails != null) {

            // region Date Of Print
            mBinding.txtDatePrint.text = formatDisplayDateTimeInMillisecond(Date())

            receiptDetails.taxInvoiceID?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), receiptDetails.gamingMachineSycotaxID))//
            }

            receiptDetails.registrationDate?.let {
                mBinding.txtStartDate.text = displayFormatDate(it)
            }
            receiptDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNo.text = it
            }
            receiptDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }

            receiptDetails.taxInvoiceDate?.let {
                mBinding.txtTaxationDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.businessOwner?.let {
                mBinding.txtGamingMachineOwner.text = it
            }

            receiptDetails.profession?.let {
                mBinding.txtProfession.text = it
            }

            receiptDetails.gamingMachineSycotaxID?.let {
                mBinding.txtGameSycoTaxID.text = it
            }

            receiptDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            receiptDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
            receiptDetails.sycotaxID?.let {
                mBinding.llSycoTaxID.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }

            receiptDetails.serialNo?.let {
                mBinding.txtSerialNumber.text = it
            }

            receiptDetails.phoneNumber?.let {
                mBinding.txtPhone.text = it
            }

            var address: String? = ""

            receiptDetails.state?.let {
                mBinding.txtState.text = it
                address += it
                address += ","
            }

            receiptDetails.city?.let {
                mBinding.txtCity.text = it
                address += it
                address += ","
            }
//            receiptDetails.plot?.let {
//                mBinding.txtDistrict.text = it
//            }

            receiptDetails.zone?.let {
                mBinding.txtArdt.text = it
                address += it
                address += ","
            }

            receiptDetails.sector?.let {
                mBinding.txtSector.text = it
                address += it
                address += ","
            }

            receiptDetails.plot?.let {
                mBinding.txtSection.text = it
                address += it
                address += ","
            }

            receiptDetails.block?.let {
                mBinding.txtLot.text = it
                address += it
                address += ","
            }


            receiptDetails.doorNo?.let {
                mBinding.txtParcel.text = it
                address += it
            }


            if (!address.isNullOrEmpty()) {
                mBinding.txtAddress.text = address
            } else
                mBinding.txtAddress.text = ""


            receiptDetails.product?.let {
                mBinding.txtTaxType.text = it
            }

            receiptDetails.gamingMachineType?.let {
                mBinding.txtGamingMachineType.text = it
            }
            receiptDetails.rate?.let {
                mBinding.txtTariff.text = getTariffWithCurrency(it)
            }
            receiptDetails.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }

//            receiptDetails.invoiceAmounnt?.let {
//                mBinding.txtAmountOfTaxInvoice.text = formatWithPrecision(it.toString())
//            }

            var totalDueAmount: Double? = 0.0

            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            receiptDetails.amountDueForCurrentYear?.let {
                if (it > 0.0)
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)
                else
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)

                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            receiptDetails.amountDueAnteriorYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                else
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            receiptDetails.amountDuePreviousYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                else
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            receiptDetails.penaltyDue?.let {
                if (it > 0)
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it)
                else
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            totalDueAmount?.let {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(it.toString())
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }

            receiptDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it.toString()

            }


            receiptDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //                   mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(receiptDetails.taxInvoiceID!!, mBinding.btnPrint)
            }

        }
    }
}