package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CartTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemCartTaxNoticeBinding
import com.sgs.citytax.model.CartTaxNoticeDetails
import com.sgs.citytax.util.*
import java.util.*

class CartTaxNoticeViewHolder(val mBinding: ItemCartTaxNoticeBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(taxDetails: CartTaxNoticeResponse, iClickListener: IClickListener?) {

        bindTaxDetails(taxDetails.cartTaxNoticeDetails[0],taxDetails)

        if (iClickListener != null) {
           mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetails)
                }
            })
        }

    }

    private fun bindTaxDetails(
        taxNoticeDetails: CartTaxNoticeDetails?,
        taxDetails: CartTaxNoticeResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxDetails.orgData
        )
        mBinding.txtCartSycoTaxIDLabel.text = String.format("%s%s", getString(R.string.cart_syco_tax_id), getString(R.string.colon))
        mBinding.txtNoticeNumberLabel.text = String.format("%s%s", getString(R.string.notice_number), getString(R.string.colon))
        mBinding.txtTaxationYearLabel.text = String.format("%s%s", getString(R.string.taxation_year), getString(R.string.colon))
        mBinding.txtBusinessOwnerLabel.text = String.format("%s%s", getString(R.string.title_cart_owner), getString(R.string.colon))
        mBinding.txtProfessionLabel.text = String.format("%s%s", getString(R.string.profession), getString(R.string.colon))
//        mBinding.txtSycoTaxIDLabel.text = String.format("%s%s", getString(R.string.cart_syco_tax_id), getString(R.string.colon))
        mBinding.txtStateLabel.text = String.format("%s%s", getString(R.string.state), getString(R.string.colon))
        mBinding.txtCityLabel.text = String.format("%s%s", getString(R.string.city), getString(R.string.colon))
        mBinding.txtAddressLabel.text = String.format("%s%s", getString(R.string.title_address), getString(R.string.colon))
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))


        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport
        if (taxNoticeDetails != null) {

            // region Date Of Print
            mBinding.txtDatePrint.text = formatDisplayDateTimeInMillisecond(Date())

            taxNoticeDetails.taxInvoiceID?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), taxNoticeDetails.cartSycotaxID ?: ""))
            }


            taxNoticeDetails.registrationDate?.let {
                mBinding.txtStartDate.text = displayFormatDate(it)
            }

            taxNoticeDetails.noticeReferenceNo?.let {
                mBinding.txtNoticeNumber.text = it
            }

            taxNoticeDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }

            taxNoticeDetails.taxInvoiceDate?.let {
                mBinding.txtTaxationDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            taxNoticeDetails.businessOwner?.let {
                mBinding.txtCartOwner.text = it
            }
            taxNoticeDetails.profession?.let {
                mBinding.txtProfession.text = it
            }

            taxNoticeDetails.cartSycotaxID?.let {
                mBinding.txtCartSycoTaxID.text = it
            }
            taxNoticeDetails.citizenSycoTaxId?.let {
                    mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                    mBinding.txtCitizenSycoTaxID.text = it
            }
            taxNoticeDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
            taxNoticeDetails.sycotaxID?.let {
                mBinding.llSycoTaxID.visibility = View.VISIBLE
                mBinding.txtSycoTaxID.text = it
            }
            taxNoticeDetails.phoneNumber?.let {
                mBinding.txtPhone.text = it
            }

            var address: String? = ""

            taxNoticeDetails.state?.let {
                mBinding.txtState.text = it
            }
            taxNoticeDetails.city?.let {
                mBinding.txtCity.text = it
                address += it
                address += ","
            }

            taxNoticeDetails.zone?.let {
                mBinding.txtArdt.text = it
                address += it
                address += ","
            }

            taxNoticeDetails.sector?.let {
                mBinding.txtSector.text = it
                address += it
                address += ","
            }
            taxNoticeDetails.plot?.let {
                mBinding.txtSection.text = it
                address += it
                address += ","
            }
            taxNoticeDetails.block?.let {
                mBinding.txtLot.text = it
                address += it
                address += ","
            }

            taxNoticeDetails.doorNo?.let {
                mBinding.txtParcel.text = it
                address += it
            }

            if (!address.isNullOrEmpty()) {
                mBinding.txtAddress.text = address
            } else
                mBinding.txtAddress.text = ""

            taxNoticeDetails.product?.let {
                mBinding.txtTaxType.text = it
            }
            taxNoticeDetails.cartType?.let {
                mBinding.txtCartType.text = it
            }

            taxNoticeDetails.rate?.let {
                mBinding.txtTariff.text = getTariffWithCurrency(it)

            }
            taxNoticeDetails.billingCycle?.let {
                mBinding.txtBillingCycle.text = it
            }

            var totalDueAmount: Double? = 0.0


            mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(0.0)
            taxNoticeDetails.amountDueForCurrentYear?.let {

                    mBinding.txtTaxAmountOfCurrentYear.text = formatWithPrecision(it)

                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(0.0)
            taxNoticeDetails.amountDueAnteriorYear?.let {
                if (it > 0.0)
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                else
                    mBinding.txtRAROfAnteriorYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtRAROfPreviousYear.text = formatWithPrecision(0.0)
            taxNoticeDetails.amountDuePreviousYear?.let {
                    mBinding.txtRAROfPreviousYear.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            mBinding.txtAmountOfPenalties.text = formatWithPrecision(0.0)
            taxNoticeDetails.penaltyDue?.let {
                    mBinding.txtAmountOfPenalties.text = formatWithPrecision(it)
                totalDueAmount = totalDueAmount?.plus(it)
            }

            totalDueAmount?.let {
                mBinding.txtTotalDueAmount.text = formatWithPrecision(it.toString())
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }

            taxNoticeDetails.generatedBy?.let {
                mBinding.txtGeneratedBy.text = it.toString()

            }
            taxNoticeDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = HtmlCompat.fromHtml(it, 0)
            }

            taxNoticeDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //                   mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }

            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(taxNoticeDetails.taxInvoiceID!!, mBinding.btnPrint)
            }
        }
    }
}