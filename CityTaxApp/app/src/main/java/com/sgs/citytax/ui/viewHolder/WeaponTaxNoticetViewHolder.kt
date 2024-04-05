package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.WeaponTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemWeaponTaxNoticeBinding
import com.sgs.citytax.model.WeaponTaxNoticeDetails
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.util.*

class WeaponTaxNoticetViewHolder(val mBinding: ItemWeaponTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(taxDetails: WeaponTaxNoticeResponse, iClickListener: IClickListener?) {

        bindTaxDetails(taxDetails.weaponTaxNoticeDetails[0], taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }
    }

    private fun bindTaxDetails(
        taxDetails: WeaponTaxNoticeDetails?,
        weaponTaxNoticeResponse: WeaponTaxNoticeResponse
    ) {
        checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = weaponTaxNoticeResponse.orgData
        )
        mBinding.txtNoticeNumberLabel.text = String.format("%s%s", getString(R.string.notice_number), getString(R.string.colon))
        mBinding.txtProfssionLabel.text = String.format("%s%s", getString(R.string.profession), getString(R.string.colon))
        mBinding.txtStateLabel.text = String.format("%s%s", getString(R.string.state), getString(R.string.colon))
        mBinding.txtCityLabel.text = String.format("%s%s", getString(R.string.city), getString(R.string.colon))
        mBinding.txtAddressLabel.text = String.format("%s%s", getString(R.string.title_address), getString(R.string.colon))
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
        if (taxDetails != null) {

            // region Date Of Print
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())


            taxDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), taxDetails.weaponSycotaxID))//

            }

            taxDetails.registrationDate?.let {
                mBinding.txtStartDate.text = displayFormatDate(it)

            }
            taxDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNo.text = it

            }
            taxDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()

            }
            taxDetails.taxInvoiceDate?.let {
                mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(it)

            }
            taxDetails.businessowner?.let {
                mBinding.txtWeaponOwner.text = it

            }
            taxDetails.profession?.let {
                mBinding.txtProfssion.text = it

            }

//            if (receiptDetails.weaponExemptionReason.isNullOrEmpty()) {
//                mBinding.txtExemption.text = getString(R.string.no)
//                mBinding.txtExemptionCriteria.text = ""
//            } else {
//                mBinding.txtExemption.text = getString(R.string.yes)
//                mBinding.txtExemptionCriteria.text = receiptDetails.weaponExemptionReason
//            }

            if (taxDetails.exemption.equals(getString(R.string.no))) {
                mBinding.txtExemption.text = getString(R.string.no)
            } else {
                mBinding.txtExemption.text = getString(R.string.yes)
            }
            
            taxDetails.weaponExemptionReason?.let {
                mBinding.txtExemptionCriteria.text = it
            }

            taxDetails.weaponSycotaxID?.let {
                mBinding.txtWeaponSycoTaxId.text = it

            }

            taxDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            taxDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
            taxDetails.sycotaxID?.let {
                mBinding.llSycoTaxID.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }

            taxDetails.serialNo?.let {
                mBinding.txtWeaponSerialNumber.text = it
            }


            taxDetails.phonenumber?.let {
                mBinding.txtcontact.text = it
            }
            var address: String? = ""

            taxDetails.state?.let {
                mBinding.txtState.text = it
                address += it
                address += ","
            }

            taxDetails.city?.let {
                mBinding.txtCity.text = it
                address += it
                address += ","
            }

            taxDetails.zone?.let {
                mBinding.txtArdt.text = it
                address += it
                address += ","
            }


            taxDetails.sector?.let {
                mBinding.txtSector.text = it
                address += it
                address += ","
            }
            taxDetails.plot?.let {
                mBinding.txtSection.text = it
                address += it
                address += ","
            }

            taxDetails.block?.let {
                mBinding.txtLot.text = it
                address += it
                address += ","
            }


            taxDetails.doorNo?.let {
                mBinding.txtParcel.text = it
                address += it
            }

            if (!address.isNullOrEmpty()) {
                mBinding.txtAddress.text = address
            } else
                mBinding.txtAddress.text = ""

            taxDetails.product?.let {
                mBinding.txtTaxType.text = it
            }
            taxDetails.waponType?.let {
                mBinding.txtWeaponType.text = it
            }

            taxDetails.rate?.let {
                mBinding.txtTariff.text = getTariffWithCurrency(it)
            }


            taxDetails.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }


//            receiptDetails.invoiceAmounnt?.let {
//                mBinding.txtAmountOfTaxInvoice.text = formatWithPrecision(it.toString())
//            }


            var totalDueAmount: Double? = 0.0

            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            taxDetails.amountDueForCurrentYear?.let {
                if (it > 0.0)
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)
                else
                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)

                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            taxDetails.amountDueAnteriorYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                else
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            taxDetails.amountDuePreviousYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                else
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            taxDetails.penaltyDue?.let {
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

            taxDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it.toString()

            }

            taxDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }
            taxDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                    mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
//                    mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(taxDetails.taxInvoiceId!!, mBinding.btnPrint)
            }
        }
    }
}