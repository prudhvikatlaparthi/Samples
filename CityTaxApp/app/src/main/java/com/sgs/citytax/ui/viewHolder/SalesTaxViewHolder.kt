package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.SalesTaxNoticeResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemSalesTaxNoticeBinding
import com.sgs.citytax.databinding.SalesProductItemDetailsBinding
import com.sgs.citytax.model.SalesProductDetails
import com.sgs.citytax.model.SalesTaxNoticeDetails
import com.sgs.citytax.util.*
import com.sgs.citytax.util.CommonLogicUtils.checkNUpdateQRCodeNotes
import java.math.BigDecimal
import java.util.*


class SalesTaxViewHolder(val mBinding: ItemSalesTaxNoticeBinding) :
    RecyclerView.ViewHolder(mBinding.root) {
    fun bind(
        salesTaxNoticeResponse: SalesTaxNoticeResponse,
        iClickListener: IClickListener,
        fromScreen: Constant.QuickMenu
    ) {
        binsSalesTaxDetails(
            salesTaxNoticeResponse.salesTaxNoticeDetails[0],
            fromScreen,
            salesTaxNoticeResponse
        )
        bindProductDetails(
            salesTaxNoticeResponse.productDetails,
            salesTaxNoticeResponse.salesTaxNoticeDetails[0],
            fromScreen
        )
        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, salesTaxNoticeResponse)
                }
            })
        }
    }

    private fun binsSalesTaxDetails(
        salesTaxNoticeDetails: SalesTaxNoticeDetails,
        fromScreen: Constant.QuickMenu,
        salesTaxNoticeResponse: SalesTaxNoticeResponse
    ) {
        checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = salesTaxNoticeResponse.orgData
        )
        mBinding.invoiceHeader.text = retrieveHeaderText(fromScreen)
        mBinding.headerSalesOrderNo.text =
            String.format("%s%s", getString(R.string.sales_order_no), getString(R.string.colon))
        mBinding.titleProfession.text =
            String.format("%s%s", getString(R.string.profession), getString(R.string.colon))
        mBinding.titleAddressLabel.text =
            String.format("%s%s", getString(R.string.title_address), getString(R.string.colon))
        mBinding.titleStateLabel.text =
            String.format("%s%s", getString(R.string.state), getString(R.string.colon))
        mBinding.titleCityLabel.text =
            String.format("%s%s", getString(R.string.city), getString(R.string.colon))
        val collectBy = mBinding.txtCollectedByLabel.context.getString(R.string.collected_by)
        mBinding.txtCollectedByLabel.text =
            String.format("%s%s", collectBy, getString(R.string.colon))
        mBinding.salesPaymentDetailsWrapper.headerNetReceivable.text =
            String.format("%s%s", getString(R.string.net_receivable), getString(R.string.colon))
        mBinding.salesPaymentDetailsWrapper.headerProsecutionFees.text =
            String.format(
                "%s%s",
                getString(R.string.txt_prosecution_fees),
                getString(R.string.colon)
            )
        mBinding.salesChequeDetailsWrapper.txtChequeNumberLabel.text =
            String.format("%s%s", getString(R.string.cheque_number), getString(R.string.colon))
        mBinding.salesChequeDetailsWrapper.headerChequeAmnt.text =
            String.format("%s%s", getString(R.string.cheque_amount), getString(R.string.colon))
        mBinding.salesChequeDetailsWrapper.headerChequeStatus.text =
            String.format("%s%s", getString(R.string.cheque_status), getString(R.string.colon))

        mBinding.titleLableAdminOffice.text =
            String.format("%s%s", getString(R.string.admin_office), getString(R.string.colon))
        mBinding.txtStreetLabel.text =
            String.format("%s%s", getString(R.string.street), getString(R.string.colon))
        mBinding.txtSectorLabel.text =
            String.format("%s%s", getString(R.string.sector), getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport


        if (salesTaxNoticeDetails.printCounts != null && salesTaxNoticeDetails.printCounts > 0) {
            mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = taxNoticeDetails.printCounts.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
        } else {
            mBinding.llDuplicatePrints.visibility = View.GONE
//                mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
        }

        mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

        salesTaxNoticeDetails.sono?.let {
            mBinding.imgQRCode.setImageBitmap(
                bindQRCode(
                    Constant.ReceiptType.SALES,
                    it.toString()
                )
            )
        }

        salesTaxNoticeDetails.sono?.let {
            mBinding.txtSalesOrderNo.text = it.toString()
        }

        salesTaxNoticeDetails.taxationYear?.let {
            mBinding.txtTaxationYear.text = "$it"
        }

        salesTaxNoticeDetails.sodt?.let {
            mBinding.txtDateOfTaxation.text = formatDisplayDateTimeInMillisecond(it)
        }
        salesTaxNoticeDetails.administrativeOffice?.let {
            mBinding.txtAdminOffice.text = "$it"
        }
        mBinding.llCitizenSycoTax.isVisible = salesTaxNoticeDetails.citizenSycoTaxID?.let {
            mBinding.txtCitizenSycoTaxID.text = it
            true
        } ?: false

        salesTaxNoticeDetails.customer?.let {
            mBinding.txtCompanyName.text = it
        }

        salesTaxNoticeDetails.ph?.let {
            mBinding.txtContact.text = it
        }
        mBinding.txtPaymentMode.text = salesTaxNoticeDetails.pmtmode ?: getString(R.string.cheque)

        mBinding.llWalletTransactionNumber.isVisible =
            salesTaxNoticeDetails.walletTransactionNo?.let {
                mBinding.txtReferanceTransactionNumber.text = it
                true
            } ?: false

        if (salesTaxNoticeDetails.netrec != null && salesTaxNoticeDetails.netrec != BigDecimal.ZERO)
            getAmountInWordsWithCurrency(
                salesTaxNoticeDetails.netrec.toDouble(),
                mBinding.txtAmountInWords
            )

        salesTaxNoticeDetails.generatedBy?.let {
            mBinding.txtCollectedBy.text = it
        }

        //region Address
        var address: String? = ""

        salesTaxNoticeDetails.st?.let {
            mBinding.txtState.text = it
            address += it
            address += ","
        }
        salesTaxNoticeDetails.cty?.let {
            mBinding.txtCity.text = it
            address += it
            address += ","
        }

        //region Zone
        if (!salesTaxNoticeDetails.zn.isNullOrEmpty()) {
            mBinding.txtArdt.text = salesTaxNoticeDetails.zn
            address += salesTaxNoticeDetails.zn
            address += ","
        } else {
            mBinding.txtArdt.text = ""
            address += ""
        }
        //endregion

        //region Sector
        if (!salesTaxNoticeDetails.sec.isNullOrEmpty()) {
            mBinding.txtSector.text = salesTaxNoticeDetails.sec
            address += salesTaxNoticeDetails.sec
            address += ","
        } else {
            mBinding.txtSector.text = ""
            address += ""
        }
        //endregion
        //
        // region Sector
        if (!salesTaxNoticeDetails.street.isNullOrEmpty()) {
            mBinding.txtStreet.text = salesTaxNoticeDetails.street
            address += salesTaxNoticeDetails.street
            address += ","
        } else {
            mBinding.txtStreet.text = ""
            address += ""
        }
        //endregion

        //region plot
        if (!salesTaxNoticeDetails.plot.isNullOrEmpty()) {
            mBinding.txtSection.text = salesTaxNoticeDetails.plot
            address += salesTaxNoticeDetails.plot
            address += ","
        } else {
            mBinding.txtSection.text = ""
            address += ""
        }
        //endregion

        //region block
        if (!salesTaxNoticeDetails.block.isNullOrEmpty()) {
            address += salesTaxNoticeDetails.block
            mBinding.txtLot.text = salesTaxNoticeDetails.block
            address += ","
        } else {
            mBinding.txtLot.text = ""
            address += ""
        }
        //endregion

        //region door no
        if (!salesTaxNoticeDetails.doorno.isNullOrEmpty()) {
            mBinding.txtParcel.text = salesTaxNoticeDetails.doorno
            address += salesTaxNoticeDetails.doorno
        } else {
            mBinding.txtParcel.text = ""
            address += ""
        }
        //endregion
        if (!address.isNullOrEmpty()) {
            mBinding.txtAddress.text = address
        } else
            mBinding.txtAddress.text = ""

        mBinding.salesChequeDetailsWrapper.chequeRootWrapper.isVisible =
            salesTaxNoticeDetails.chqno?.isNotEmpty() == true
        mBinding.salesChequeDetailsWrapper.apply {
            llChequeNumber.isVisible = salesTaxNoticeDetails.chqno?.let {
                txtChequeNumber.text = it
                true
            } ?: false

            llChequeDate.isVisible = salesTaxNoticeDetails.chqdt?.let {
                txtChequeDate.text = displayFormatDate(it)
                true
            } ?: false

            llBankName.isVisible = salesTaxNoticeDetails.bnkname?.let {
                txtChequeBankName.text = it
                true
            } ?: false

            llChequeAmount.isVisible = salesTaxNoticeDetails.chequeAmount?.let {
                txtChequeAmount.text = formatWithPrecision(it)
                it > BigDecimal.ZERO
            } ?: false

            llChequeStatus.isVisible = salesTaxNoticeDetails.chequeStatus?.let {
                txtChequeStatus.text = it
                true
            } ?: false

            //Sales Payment
            mBinding.salesPaymentDetailsWrapper.paymentRootWrapper.isVisible = true
            mBinding.salesPaymentDetailsWrapper.apply {
                llPaymentRecptNo.isVisible = salesTaxNoticeDetails.paymentReceiptNo?.let {
                    txtPaymentReceiptNo.text = it
                    true
                } ?: false

                if (fromScreen == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX) {
                    llPaymentAmountWrapper.isVisible = false
                } else {
                    llPaymentAmountWrapper.isVisible = salesTaxNoticeDetails.netrec?.let {
                        txtPaymentAmount.text = formatWithPrecision(it)
                        it > BigDecimal.ZERO
                    } ?: false
                }

                llTotalSalesWrapper.isVisible = salesTaxNoticeDetails.salesAmount?.let {
                    txtTotalSalesAmount.text = formatWithPrecision(it)
                    it > BigDecimal.ZERO
                } ?: false

                llPenaltyPerWrapper.isVisible = salesTaxNoticeDetails.penaltyPercentage?.let {
                    txtPenaltyPercentage.text = it.stripTrailingZeros().toPlainString()
                    it > BigDecimal.ZERO
                } ?: false

                llPenaltyAmountWrapper.isVisible = salesTaxNoticeDetails.penaltyAmount?.let {
                    txtPenaltyAmount.text = formatWithPrecision(it)
                    it > BigDecimal.ZERO
                } ?: false

                llProsecutionFeeWrapper.isVisible = salesTaxNoticeDetails.prosecutionFees?.let {
                    txtProsecutionFees.text = formatWithPrecision(it)
                    it > BigDecimal.ZERO
                } ?: false

                llNetReceivableWrapper.isVisible = salesTaxNoticeDetails.netrec?.let {
                    txtNetReceivable.text = formatWithPrecision(it)
                    it > BigDecimal.ZERO
                } ?: false
            }
        }

        if (MyApplication.getPrefHelper().isFromHistory == false) {
//                getNoticePrintFlag(salesTaxNoticeDetails.taxInvoiceId!!, mBinding.btnPrint)
        }

    }

    private fun retrieveHeaderText(
        fromScreen: Constant.QuickMenu
    ): String {
        return if (fromScreen == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX) {
            getString(R.string.security_sales_invoice_receipt)
        } else {
            getString(R.string.sales_invoice_receipt)
        }
    }

    private fun bindProductDetails(
        productDetails: ArrayList<SalesProductDetails>,
        salesTaxNoticeDetails: SalesTaxNoticeDetails,
        fromScreen: Constant.QuickMenu
    ) {
        if (!productDetails.isNullOrEmpty()) {
            mBinding.llProductDetails.removeAllViews()
            for (product in productDetails) {
                val mProductBinding: SalesProductItemDetailsBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mBinding.root.context),
                    R.layout.sales_product_item_details,
                    mBinding.llProductDetails,
                    false
                )
                mProductBinding.tvTotal.text =
                    String.format("%s%s", getString(R.string.total), getString(R.string.colon))

                if (fromScreen == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX) {
                    mProductBinding.llTypeOfCoverCode.visibility = View.GONE
                    mProductBinding.llSalesItemMain.visibility = View.GONE
                    mProductBinding.llSecurityItemMain.visibility = View.VISIBLE

                    mProductBinding.tvTypeOfCover.text = String.format(
                        "%s%s",
                        getString(R.string.type_of_cover), getString(R.string.colon)
                    )

                    mProductBinding.tvTypeOfCoverCode.text = String.format(
                        "%s%s",
                        getString(R.string.type_of_cover_code), getString(R.string.colon)
                    )

                    mProductBinding.tvNoOfPersons.text = String.format(
                        "%s%s",
                        getString(R.string.no_of_persons), getString(R.string.colon)
                    )

                    mProductBinding.tvNoOfDays.text = String.format(
                        "%s%s",
                        getString(R.string.no_of_days), getString(R.string.colon)
                    )

                    product.noOfPerns.let {
                        mProductBinding.txtNoOfPersons.text = it.toString()
                    }
                    product.daysCnt.let {
                        mProductBinding.txtNoOfDays.text = it.toString()
                    }
                } else {
                    mProductBinding.llSalesItemMain.visibility = View.VISIBLE
                    mProductBinding.llSecurityItemMain.visibility = View.GONE

                    mProductBinding.tvProductCode.text = String.format(
                        "%s%s",
                        getString(R.string.itemCode), getString(R.string.colon)
                    )

                    mProductBinding.tvProductName.text =
                        String.format("%s%s", getString(R.string.item), getString(R.string.colon))

                    mProductBinding.tvQuantity.text = String.format(
                        "%s%s",
                        getString(R.string.quantity), getString(R.string.colon)
                    )
                }


                product.itemCode?.let {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX)
                        mProductBinding.txtTypeOfCoverCode.text = it
                    else
                        mProductBinding.txtProductCode.text = "$it"
                }
                product.item?.let {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX)
                        mProductBinding.txtTypeOfCover.text = it
                    else
                        mProductBinding.txtProductName.text = "$it"
                }

                product.quantity.let {
                    mProductBinding.txtQuantity.text = it.stripTrailingZeros().toPlainString()
                }

                mProductBinding.llValidityPeriod.visibility = View.GONE
                product.expryDarte?.let {
                    mProductBinding.txtValidityPeriod.text =
                        displayFormatDate(salesTaxNoticeDetails.sodt) + " - " + displayFormatDate(it)
                    mProductBinding.llValidityPeriod.visibility = View.VISIBLE
                }
                product.unitPrice.let {
                    mProductBinding.txtUnitPrice.text = formatWithPrecision(it)
                }

                product.total.let {
                    mProductBinding.txtTotal.text = formatWithPrecision(it)
                }
                mBinding.llProductDetails.addView(mProductBinding.root)
            }
        } else {
            mBinding.llProductsView.visibility = View.GONE
        }

    }
}