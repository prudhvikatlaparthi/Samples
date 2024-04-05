package com.sgs.citytax.ui.adapter

import android.graphics.Bitmap
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.BusinessDueSummary
import com.sgs.citytax.databinding.ItemBusinessSummaryTaxesBinding
import com.sgs.citytax.model.VUCRMCustomerProductInterestLines
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*

class BusinessSummaryTaxAdapter(private val taxes: List<Any>) : RecyclerView.Adapter<BusinessSummaryTaxAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBusinessSummaryTaxesBinding) : RecyclerView.ViewHolder(binding.root) {

        private fun hideAllViews() {
            binding.llTaxType.visibility = GONE
            binding.llOccupancyType.visibility = GONE
            binding.llTaxFinancialYear.visibility = GONE
            binding.llStartDate.visibility = GONE
            binding.llTaxableElement.visibility = GONE
            binding.llTurnOver.visibility = GONE
            binding.llBillingCycle.visibility = GONE
            binding.llTaxableMatter.visibility = GONE
            binding.llEstimateTax.visibility = GONE
            binding.llPendingTaxAmount.visibility = GONE
        }

        private fun setTaxableMatter(taxableMatter: Double? = 0.0) {
            binding.llTaxableMatter.visibility = VISIBLE
            binding.txtTaxableMatter.text = "$taxableMatter"
        }

        private fun setTaxType(taxType: String) {
            binding.llTaxType.visibility = VISIBLE
            binding.txtTaxType.text = taxType
        }

        private fun setTaxStartDate(date: String) {
            binding.llStartDate.visibility = VISIBLE
            binding.txtStartDate.text = displayFormatDate(date)
        }

        private fun setBillingCycle(billingCycle: String) {
            binding.llBillingCycle.visibility = VISIBLE
            binding.txtBillingCycle.text = billingCycle
        }

        private fun setEstimatedTax(estimatedTax: BigDecimal? = BigDecimal.ZERO) {
            binding.llEstimateTax.visibility = VISIBLE
            binding.txtEstimatedTax.text = formatWithPrecision(estimatedTax)
        }

        private fun setAdvertisementType(occupancyName: String) {
            binding.llOccupancyType.visibility = VISIBLE
            binding.txtOccupancyTypeLabel.text = binding.txtOccupancyTypeLabel.context.resources.getString(R.string.advertisement_type)
            binding.txtOccupancyType.text = occupancyName
        }

        private fun setMarketData(marketName: String) {
            binding.llMarket.visibility = VISIBLE
            binding.txtMarketName.text = marketName
        }

        private fun setTaxableElement(taxElement: String) {
            binding.llTaxableElement.visibility = VISIBLE
            binding.txtTaxableElement.text = taxElement
        }

        private fun setTaxSubType(attributeName: String, taxRuleBookCode: String?) {
            binding.llTaxSubType.visibility = GONE
            when (taxRuleBookCode) {
                Constant.TaxRuleBook.HOTEL.Code -> {
                    binding.tvTaxSubTypeHeader.text = binding.tvTaxSubTypeHeader?.context?.resources?.getString(R.string.star)
                    binding.txtTaxSubType.text = attributeName
                    binding.llTaxSubType.visibility = VISIBLE
                }
                Constant.TaxRuleBook.SHOW.Code -> {
                    binding.tvTaxSubTypeHeader.text = binding.tvTaxSubTypeHeader?.context?.resources?.getString(R.string.operator_type)
                    binding.txtTaxSubType.text = attributeName
                    binding.llTaxSubType.visibility = VISIBLE
                }

                Constant.TaxRuleBook.LICENSE.Code -> {
                    binding.tvTaxSubTypeHeader.text = binding.tvTaxSubTypeHeader?.context?.resources?.getString(R.string.license_category)
                    binding.txtTaxSubType.text = attributeName
                    binding.llTaxSubType.visibility = VISIBLE
                }


            }
        }

        fun bind(item: Any) {

            binding.llTax.visibility = GONE
            binding.llFixedExpenses.visibility = GONE
            binding.imgQRCode.visibility = GONE
            binding.qrCodeWrapper.qrRootView.hide()

            when (item) {
                is VUCRMCustomerProductInterestLines -> {
                    // region Visibility
                    binding.llTax.visibility = VISIBLE
                    binding.llStartDate.visibility = GONE
                    item.taxRuleBookCode?.toUpperCase(Locale.getDefault())?.let {
                        binding.llTurnOver.visibility = GONE
                        binding.llTaxableElement.visibility = VISIBLE
                        binding.llTaxableMatter.visibility = VISIBLE
                        binding.llOccupancyType.visibility = VISIBLE
                        if (it == Constant.TaxRuleBook.CME.Code || it == Constant.TaxRuleBook.CP.Code ) {
                            binding.llTurnOver.visibility = VISIBLE
                            binding.llTaxableElement.visibility = GONE
                            binding.llTaxableMatter.visibility = GONE
                            binding.llOccupancyType.visibility = GONE
                        } else if (it == Constant.TaxRuleBook.DEFAULT.Code) {
                            binding.llOccupancyType.visibility = GONE
                            binding.llTaxableElement.visibility = GONE
                        }
                    }

                    if (item.occupancyName != null)
                        binding.llOccupancyType.visibility = VISIBLE
                    else
                        binding.llOccupancyType.visibility = GONE

                    if (item.taxableElement != null)
                        binding.llTaxableElement.visibility = VISIBLE
                    else
                        binding.llTaxableElement.visibility = GONE

                    binding.llPendingTaxAmount.visibility = GONE
                    binding.llTaxFinancialYear.visibility = GONE
                    // endregion
                    binding.txtTaxType.text = item.product
                    binding.txtOccupancyType.text = item.occupancyName
                    binding.txtFinancialYear.text = "TODO"
                    if (item.taxStartDate != null && !TextUtils.isEmpty(item.taxStartDate)) {
                        binding.txtStartDate.text = displayFormatDate(item.taxStartDate)
                        binding.llStartDate.visibility = VISIBLE
                    }
                    binding.txtTaxableElement.text = item.taxableElement
                    binding.txtBillingCycle.text = item.billingCycleName
                    binding.txtTaxableMatter.text = "${item.taxableMatter}"
                    binding.txtEstimatedTax.text = formatWithPrecision(item.taxAmount)
                    binding.txtPendingTaxAmount.text = "TODO"

                    item.turnOver?.let {
                        binding.txtTurnOver.text = formatWithPrecision(it)
                    }

                    item.attributeName?.let {
                        setTaxSubType(it, item.taxRuleBookCode ?: "")
                    }


                    item.entityName?.let { it ->
                        if (it == "CRM_Advertisements") {
                            hideAllViews()
                            item.product?.let {
                                setTaxType(it)
                            }
                            item.taxStartDate?.let {
                                setTaxStartDate(it)
                            }
                            item.billingCycleName?.let {
                                setBillingCycle(it)
                            }
                            item.taxableMatter?.let {
                                setTaxableMatter(it)
                            }
                            item.taxAmount?.let {
                                setEstimatedTax(it)
                            }
                            item.occupancyName?.let {
                                setAdvertisementType(it)
                            }
                            item.taxableElement?.let {
                                //Hiding Taxable Element field based on UnitCode
                                if(item.unitcode == Constant.UnitCode.EA.name){
                                    binding.llTaxableElement.visibility = View.GONE
                                }else
                                setTaxableElement(it)
                            }
                        }
                        if (it == "CRM_RightOfPlaces") {
                            item.market?.let {

                                if (!it.isEmpty()) {
                                    setMarketData(it)
                                }
                            }
                        } else {
                            binding.llMarket.visibility = GONE
                        }
                    }
                }

                is BusinessDueSummary -> {
                    binding.llFixedExpenses.visibility = VISIBLE

                    binding.dueSummaryHeader.visibility = VISIBLE

                    binding.txtKeyOne.text = getString(R.string.initial_outstanding_current_year_due)
                    binding.txtKeyTwo.text = getString(R.string.current_year_due)
                    binding.txtKeyThree.text = getString(R.string.current_year_penalty_due)
                    binding.txtKeyFour.text = getString(R.string.previous_year_due)
                    binding.txtKeyFive.text = getString(R.string.previous_year_penalty_due)
                    binding.txtKeySix.text = getString(R.string.anterior_year_due)
                    binding.txtKeySeven.text = getString(R.string.anterior_year_penalty_due)

                    binding.txtValueOne.text = formatWithPrecision(item.initialOutstandingCurrentYearDue)
                    binding.txtValueTwo.text = formatWithPrecision(item.currentYearDue)
                    binding.txtValueThree.text = formatWithPrecision(item.currentYearPenaltyDue)
                    binding.txtValueFour.text = formatWithPrecision(item.previousYearDue)
                    binding.txtValueFive.text = formatWithPrecision(item.previousYearPenaltyDue)
                    binding.txtValueSix.text = formatWithPrecision(item.anteriorYearDue)
                    binding.txtValueSeven.text = formatWithPrecision(item.anteriorYearPenaltyDue)

                    binding.llOne.visibility = VISIBLE
                    binding.llTwo.visibility = VISIBLE
                    binding.llThree.visibility = VISIBLE
                    binding.llFour.visibility = VISIBLE
                    binding.llFive.visibility = VISIBLE
                    binding.llSix.visibility = VISIBLE
                    binding.llSeven.visibility = VISIBLE
                }

                is Bitmap -> {
                    binding.imgQRCode.visibility = VISIBLE
                    binding.imgQRCode.setImageBitmap(item)
                    binding.qrCodeWrapper.qrRootView.show()
                    CommonLogicUtils.checkNUpdateQRCodeNotes(
                        qrCodeWrapper =binding.qrCodeWrapper
                    )
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_business_summary_taxes, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return taxes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taxes[position])
    }

}