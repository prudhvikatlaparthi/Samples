package com.sgs.citytax.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.AccountPhone
import com.sgs.citytax.api.payload.BusinessTaxDueYearSummary
import com.sgs.citytax.api.payload.StoreCustomerB2B
import com.sgs.citytax.api.response.BusinessDueSummary
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemBusinessSummaryApprovalChildBinding
import com.sgs.citytax.databinding.ItemBusinessSummaryApprovalGroupBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.fragment_business_entry.view.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

class BusinessSummaryApprovalAdapter(private val listner: IClickListener) : BaseExpandableListAdapter() {

    private var group: ArrayList<String> = arrayListOf()
    private var child: HashMap<String, List<Any>> = HashMap()

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        if (group.isEmpty() || child.isEmpty() || group[groupPosition].isEmpty() || child[group[groupPosition]] == null || child[group[groupPosition]]!!.isEmpty())
            return -1
        return child[group[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        if (group.isEmpty() || child.isEmpty() || group[groupPosition].isEmpty() || child[group[groupPosition]] == null || child[group[groupPosition]]!!.isEmpty())
            return -1
        return child[group[groupPosition]]!!.size
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View {
        val binding: ItemBusinessSummaryApprovalChildBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.item_business_summary_approval_child, parent, false)

        binding.llOne.visibility = GONE
        binding.llTwo.visibility = GONE
        binding.llThree.visibility = GONE
        binding.llFour.visibility = GONE
        binding.llFive.visibility = GONE
        binding.llSix.visibility = GONE
        binding.llSeven.visibility = GONE
        binding.llEight.visibility = GONE
        binding.llNine.visibility = GONE
        binding.llTen.visibility = GONE
        binding.llEleven.visibility = GONE

        when (getChild(groupPosition, childPosition)) {
            is VUCRMCustomerProductInterestLines -> {

                val childData = getChild(groupPosition, childPosition) as VUCRMCustomerProductInterestLines

                // region ProductCode
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.product_code)
                binding.txtValueOne.text = childData.productCode
                binding.llOne.visibility = VISIBLE
                // endregion

                // region Status
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.status)
                binding.txtValueTwo.text = if (childData.active.equals("Y")) parent?.context?.resources?.getString(R.string.active) else parent?.context?.resources?.getString(R.string.inactive)
                binding.llTwo.visibility = VISIBLE
                // endregion

                // region Tax Type
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.tax_type)
                binding.txtValueThree.text = childData.product
                binding.llThree.visibility = VISIBLE
                // endregion

                when (childData.taxRuleBookCode) {
                    Constant.TaxRuleBook.HOTEL.Code -> {
                      // binding.lltwelve.visibility=View.VISIBLE
                        binding.txtKeyTwelve.text = parent?.context?.resources?.getString(R.string.star)
                        binding.llCategoryOne.visibility=View.VISIBLE
                        binding.txtLCategory.text= parent?.context?.resources?.getString(R.string.star)
                        binding.txtLicenceCValue.text = childData.attributeName
                    }
                    Constant.TaxRuleBook.SHOW.Code -> {
                       // binding.lltwelve.visibility=View.VISIBLE
                        binding.txtKeyTwelve.text = parent?.context?.resources?.getString(R.string.operator_type)
                        binding.llCategoryOne.visibility=View.VISIBLE
                        binding.txtLCategory.text= parent?.context?.resources?.getString(R.string.operator_type)
                        binding.txtLicenceCValue.text = childData.attributeName
                        binding.llTaxableMatter.visibility= VISIBLE
                        binding.txtDataTaxableMatter.text=childData.taxableMatterName
                    }

                    Constant.TaxRuleBook.LICENSE.Code -> {
                        binding.llCategoryOne.visibility=View.VISIBLE
                        binding.txtLicenceCValue.text = childData.attributeName

                    }
                    Constant.TaxRuleBook.ROP.Code -> {
                        if (childData.market == null || TextUtils.isEmpty(childData.market)) {
                            binding.txtValueEleven.visibility = GONE
                        }
                        else {
                            binding.llEleven.visibility = View.VISIBLE
                            binding.txtKeyEleven.text = parent?.context?.resources?.getString(R.string.title_market)
                            binding.txtValueEleven.text = childData.market
                        }
                    }


                }

                // region Occupancy Type
                if (childData.occupancyName.isNullOrEmpty()) {
                    binding.llFour.visibility = GONE
                } else {
                    if (childData.entityName == "CRM_Advertisements")
                        binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.advertisement_type)
                    else binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.occupancy_type)
                    binding.txtValueFour.text = childData.occupancyName
                    binding.llFour.visibility = VISIBLE
                }
                // endregion

                // region Taxable Matter
                if (childData.taxableMatter == null || childData.taxableMatter == 0.0) {
                    binding.llFive.visibility = GONE
                } else {
                    binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.taxable_matter)
                    childData.taxRuleBookCode?.toUpperCase(Locale.getDefault())?.let {
                        if (it == Constant.TaxRuleBook.CP.Code || it == Constant.TaxRuleBook.CME.Code) {
                            binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.turn_over_amount)
                            binding.txtValueFive.text = formatWithPrecision(childData.taxableMatter) //Added by Aparna
                        } else {
                            binding.txtValueFive.text = childData.taxableMatter.toString() //Added by Aparna
                        }
                    }

                    binding.llFive.visibility = VISIBLE
                }
                // endregion

                // region Taxable Element
                //Hiding Taxable Element field based on UnitCode
                if (childData.taxableElement == null || TextUtils.isEmpty(childData.taxableElement) ||
                    childData.unitcode == Constant.UnitCode.EA.name) {
                    binding.llSix.visibility = GONE
                } else {
                    binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.taxable_element)
                    binding.txtValueSix.text = childData.taxableElement.toString()
                    binding.llSix.visibility = VISIBLE
                }
                // endregion

                // region Tax Start Date
                if (childData.taxStartDate == null || TextUtils.isEmpty(childData.taxStartDate)) {
                    binding.llSeven.visibility = GONE
                } else {
                    binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.tax_start_date)
                    binding.txtValueSeven.text = displayFormatDate(childData.taxStartDate)
                    binding.llSeven.visibility = VISIBLE
                }
                // endregion

                // region Billing Cycle
                if (childData.billingCycleName == null || TextUtils.isEmpty(childData.billingCycleName)) {
                    binding.llEight.visibility = GONE
                } else {
                    binding.txtKeyEight.text = parent?.context?.resources?.getString(R.string.billing_cycle)
                    binding.txtValueEight.text = childData.billingCycleName.toString()
                    binding.llEight.visibility = VISIBLE
                }
                // endregion

                // region Estimated Tax
                if (childData.taxAmount == null || childData.taxAmount == BigDecimal.ZERO) {
                    binding.llNine.visibility = GONE
                } else {
                    binding.txtKeyNine.text = parent?.context?.resources?.getString(R.string.business_summary_estimated_tax)
                    binding.txtValueNine.text = formatWithPrecision(childData.taxAmount)
                    binding.llNine.visibility = VISIBLE
                }
                // endregion

                // region Currency
                binding.txtKeyTen.text = parent?.context?.resources?.getString(R.string.currency)
                binding.txtValueTen.text = MyApplication.getPrefHelper().currencySymbol
                binding.llTen.visibility = VISIBLE
                // endregion
            }

            is AccountPhone -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.phone_number)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.phone_type)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.status)
                binding.txtKeyFour.text = getString(R.string.verified)
                val childData = getChild(groupPosition, childPosition) as AccountPhone
                binding.txtValueOne.text = childData.number
                binding.txtValueTwo.text = childData.phoneType
                binding.txtValueThree.text = if (childData.default.equals("Y", true)) parent?.context?.resources?.getString(R.string.lbl_default) else parent?.context?.resources?.getString(R.string.lbl_non_default)
                binding.txtValueFour.text = if (childData.verified == "Y") parent?.context?.resources?.getString(R.string.yes) else parent?.context?.resources?.getString(R.string.no)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                binding.llFour.visibility = VISIBLE
            }

            is CRMAccountEmails -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.email)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.email_type)
                binding.txtKeyThree.text = getString(R.string.verified)
                val childData = getChild(groupPosition, childPosition) as CRMAccountEmails
                binding.txtValueOne.text = childData.email
                binding.txtValueTwo.text = childData.EmailType
                binding.txtValueThree.text = if (childData.verified == "Y") parent?.context?.resources?.getString(R.string.yes) else parent?.context?.resources?.getString(R.string.no)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
            }

            is GeoAddress -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.zone)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.sector)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.street)
                val childData = getChild(groupPosition, childPosition) as GeoAddress
                binding.txtValueOne.text = childData.zone
                binding.txtValueTwo.text = childData.sector
                binding.txtValueThree.text = childData.street

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
            }

            is CRMCorporateTurnover -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.from_date)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.to_date)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.amount)
                val childData = getChild(groupPosition, childPosition) as CRMCorporateTurnover
                binding.txtValueOne.text = getDate(childData.financialStartDate
                        ?: "", DateTimeTimeZoneMillisecondFormat, displayDateFormat)
                binding.txtValueTwo.text = getDate(childData.financialEndDate
                        ?: "", DateTimeTimeZoneMillisecondFormat, displayDateFormat)
                binding.txtValueThree.text = "${childData.amount}"

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
            }

            is VUCRMPropertyOwnership -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.property_name)
                val childData = getChild(groupPosition, childPosition) as VUCRMPropertyOwnership
                binding.txtValueOne.text = childData.propertyName

                binding.llOne.visibility = VISIBLE
            }

            is VUADMVehicleOwnership -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.vehicle_no)
                val childData = getChild(groupPosition, childPosition) as VUADMVehicleOwnership
                binding.txtValueOne.text = childData.vehicleNo

                val startDate = childData.fromDate ?: ""
                val endDate = childData.toDate ?: ""
                if (!TextUtils.isEmpty(startDate)) {
                    binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.start_date)
                    binding.txtValueTwo.text = displayFormatDate(startDate)
                    binding.llTwo.visibility = VISIBLE
                }
                if (!TextUtils.isEmpty(endDate)) {
                    binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.end_date)
                    binding.txtValueThree.text = displayFormatDate(endDate)
                    binding.llThree.visibility = VISIBLE
                }

                binding.llOne.visibility = VISIBLE
            }

            is ROPListItem -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.occupancy)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.taxable_matter)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.title_market)
                val childData = getChild(groupPosition, childPosition) as ROPListItem
                binding.txtValueOne.text = childData.occupancyName.toString()
                binding.txtValueTwo.text = childData.taxableMatter.toString()
                binding.txtValueThree.text = childData.market.toString()

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                if (TextUtils.isEmpty(childData.market) || childData.market == null)
                {
                    binding.llThree.visibility = GONE
                }
            }

            is CRMPropertyRent -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.rent_type)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.agreement_no)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.rent_amount)
                val childData = getChild(groupPosition, childPosition) as CRMPropertyRent
                binding.txtValueOne.text = childData.rentType
                binding.txtValueTwo.text = childData.agreementNo
                binding.txtValueThree.text = formatWithPrecision(childData.rentAmount)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
            }

            is VUCRMAdvertisements -> {
                binding.txtKeyOne.text = getString(R.string.advertisement_type)
                binding.txtKeyTwo.text = getString(R.string.quantity)
                binding.txtKeyThree.text = getString(R.string.active)
                val childData = getChild(groupPosition, childPosition) as VUCRMAdvertisements
                binding.txtValueOne.text = childData.advertisementTypeName
                binding.txtValueTwo.text = childData.quantity.toString()
                binding.txtValueThree.text = childData.active

                binding.txtValueThree.text = if (childData.active.equals("Y")) binding.txtValueThree?.context?.resources?.getString(R.string.yes) else binding.txtValueThree?.context?.resources?.getString(R.string.no)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE

                childData.documentList?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = AdvertisementImageAdapter(it, listner)
                }
            }

            is ShowsDetailsTable -> {
                binding.txtKeyOne.text = getString(R.string.show_name)
                binding.txtKeyTwo.text = getString(R.string.operator_type)
                val childData = getChild(groupPosition, childPosition) as ShowsDetailsTable
                binding.txtValueOne.text = childData.showName
                binding.txtValueTwo.text = childData.operatorType

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE

                childData.documents?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listner)
                }
            }

            is HotelDetails -> {
                binding.txtKeyOne.text = getString(R.string.hotel_name)
                binding.txtKeyTwo.text = getString(R.string.star)
                val childData = getChild(groupPosition, childPosition) as HotelDetails
                binding.txtValueOne.text = childData.hotelName
                binding.txtValueTwo.text = childData.star

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE

                childData.documents?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listner)
                }
            }

            is BusinessOwnership -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.first_name)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.citizen_syco_tax_id)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.citizen_id_number)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.profession)
                val childData = getChild(groupPosition, childPosition) as BusinessOwnership
                binding.txtValueOne.text = childData.firstName

                binding.llTwo.visibility = GONE
                binding.llThree.visibility = GONE
                binding.llFour.visibility = GONE


                childData.citizenSycoTaxID?.let {
                    binding.txtValueTwo.text = it
                    binding.llTwo.visibility = VISIBLE
                }
                childData.citizenCardNo?.let {
                    binding.txtValueThree.text = it
                    binding.llThree.visibility = VISIBLE
                }

                childData.profession?.let {
                    binding.txtValueFour.text = it
                    binding.llFour.visibility = VISIBLE
                }

                binding.llOne.visibility = VISIBLE
                childData.documents?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listner)

                }

            }

            is BusinessDueSummary -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.business_summary_initial_outstanding_current_year_due)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.business_summary_current_year_due)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.business_summary_current_year_penalty_due)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.business_summary_previous_year_due)
                binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.business_summary_previous_year_penalty_due)
                binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.business_summary_anterior_year_due)
                binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.business_summary_anterior_year_penalty_due)

                val childData = getChild(groupPosition, childPosition) as BusinessDueSummary

                binding.txtValueOne.text = formatWithPrecision(childData.initialOutstandingCurrentYearDue)
                binding.txtValueTwo.text = formatWithPrecision(childData.currentYearDue)
                binding.txtValueThree.text = formatWithPrecision(childData.currentYearPenaltyDue)
                binding.txtValueFour.text = formatWithPrecision(childData.previousYearDue)
                binding.txtValueFive.text = formatWithPrecision(childData.previousYearPenaltyDue)
                binding.txtValueSix.text = formatWithPrecision(childData.anteriorYearDue)
                binding.txtValueSeven.text = formatWithPrecision(childData.anteriorYearPenaltyDue)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                binding.llFour.visibility = VISIBLE
                binding.llFive.visibility = VISIBLE
                binding.llSix.visibility = VISIBLE
                binding.llSeven.visibility = VISIBLE
            }

            is BusinessTaxDueYearSummary -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.product)
                binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.invoice_amount)
                binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.invoice_due)
                binding.txtKeyEight.text = parent?.context?.resources?.getString(R.string.penalty_amount)
                binding.txtKeyNine.text = parent?.context?.resources?.getString(R.string.penalty_due)

                val childData = getChild(groupPosition, childPosition) as BusinessTaxDueYearSummary
                when (childData.taxRuleBookCode) {
                    Constant.TaxRuleBook.HOTEL.Code -> {
                        binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.star)

                    }
                    Constant.TaxRuleBook.SHOW.Code -> {
                        binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.operator_type)
                    }
                }

                binding.txtValueOne.text = if (childData.taxSubType == null || childData.taxSubType.isNullOrEmpty()) childData.product
                        ?: "" else childData.taxSubType
                binding.txtValueSix.text = formatWithPrecision(childData.invoiceAmount)
                binding.txtValueSeven.text = formatWithPrecision(childData.invoiceDue)
                binding.txtValueEight.text = formatWithPrecision(childData.penaltyAmount)
                binding.txtValueNine.text = formatWithPrecision(childData.penaltyDue)

                binding.llOne.visibility = VISIBLE
                binding.llSix.visibility = VISIBLE
                binding.llSeven.visibility = VISIBLE
                binding.llEight.visibility = VISIBLE
                binding.llNine.visibility = VISIBLE
            }

            is StoreCustomerB2B -> {
                val childData = getChild(groupPosition, childPosition) as StoreCustomerB2B

                childData.attachment?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it as ArrayList<COMDocumentReference>, listner)
                }
            }
            is WeaponTaxSummary -> {
                val mWeaponTax = getChild(groupPosition, childPosition) as WeaponTaxSummary

                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.syco_tax_id)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.registration_date)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.serial_no)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.make)
                binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.model)
                binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.purpose_of_possession)
                binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.description)
                binding.txtKeyEight.text = parent?.context?.resources?.getString(R.string.estimated_tax_amount)
                binding.txtKeyNine.text = parent?.context?.resources?.getString(R.string.status)
                binding.txtKeyTen.text = parent?.context?.resources?.getString(R.string.owner_name)
                binding.txtKeyEleven.text = parent?.context?.resources?.getString(R.string.phone_number)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                binding.llFour.visibility = VISIBLE
                binding.llFive.visibility = VISIBLE
                binding.llSix.visibility = VISIBLE
                binding.llSeven.visibility = VISIBLE
                binding.llEight.visibility = VISIBLE
                binding.llNine.visibility = VISIBLE
                binding.llTen.visibility = VISIBLE
                binding.llEleven.visibility = VISIBLE

                mWeaponTax?.let { weaponTax ->
                    weaponTax.weaponSycotaxID?.let {
                        binding.txtValueOne.text = it
                    }
                    weaponTax.registrationDate?.let {
                        binding.txtValueTwo.text = displayFormatDate(it)
                    }
                    weaponTax.serialNo?.let {
                        binding.txtValueThree.text = it
                    }
                    weaponTax.make?.let {
                        binding.txtValueFour.text = it
                    }
                    weaponTax.model?.let {
                        binding.txtValueFive.text = it
                    }
                    weaponTax.purposeOfPossession?.let {
                        binding.txtValueSix.text = it
                    }
                    weaponTax.description?.let {
                        binding.txtValueSeven.text = it
                    }
                    weaponTax.estimatedTax?.let {
                        binding.txtValueEight.text = formatWithPrecision(it)
                    }
                    weaponTax.active?.let {
                        binding.txtValueNine.text = if (it == "Y") parent?.context?.resources?.getString(R.string.active) else parent?.context?.resources?.getString(R.string.inactive)
                    }
                    weaponTax.accountName?.let {
                        binding.txtValueTen.text = it
                    }
                    weaponTax.accountPhone?.let {
                        binding.txtValueEleven.text = it
                    }
                }
                mWeaponTax.documentDetails?.comDocumentReferences?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it as ArrayList<COMDocumentReference>, listner)
                }

            }

            is CartTaxSummary -> {
                val mCartTax = getChild(groupPosition, childPosition) as CartTaxSummary

                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.syco_tax_id)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.registration_date)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.cart_no)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.estimated_tax_amount)
                binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.status)
                binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.owner_name)
                binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.phone_number)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                binding.llFour.visibility = VISIBLE
                binding.llFive.visibility = VISIBLE
                binding.llSix.visibility = VISIBLE
                binding.llSeven.visibility = VISIBLE


                mCartTax?.let { cartTax ->
                    cartTax.cartSycoTaxID?.let {
                        binding.txtValueOne.text = it
                    }
                    cartTax.registrationDate?.let {
                        binding.txtValueTwo.text = displayFormatDate(it)
                    }
                    cartTax.cartNo?.let {
                        binding.txtValueThree.text = it
                    }
                    cartTax.estimatedTax?.let {
                        binding.txtValueFour.text = formatWithPrecision(it)
                    }
                    cartTax.active?.let {
                        binding.txtValueFive.text = if (it == "Y") parent?.context?.resources?.getString(R.string.active) else parent?.context?.resources?.getString(R.string.inactive)
                    }
                    cartTax.accountName?.let {
                        binding.txtValueSix.text = it
                    }
                    cartTax.accountPhone?.let {
                        binding.txtValueSeven.text = it
                    }
                }
                mCartTax.documentDetails?.comDocumentReferences?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it as ArrayList<COMDocumentReference>, listner)
                }
            }

            is GamingMachineTaxSummary -> {
                val mGamingMachineTax = getChild(groupPosition, childPosition) as GamingMachineTaxSummary

                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.syco_tax_id)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.registration_date)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.serial_no)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.estimated_tax_amount)
                binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.status)
                binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.owner_name)
                binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.phone_number)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                binding.llFour.visibility = VISIBLE
                binding.llFive.visibility = VISIBLE
                binding.llSix.visibility = VISIBLE
                binding.llSeven.visibility = VISIBLE


                mGamingMachineTax?.let { gamingMachineTax ->
                    gamingMachineTax.gamingMachineSycotaxID?.let {
                        binding.txtValueOne.text = it
                    }
                    gamingMachineTax.registrationDate?.let {
                        binding.txtValueTwo.text = displayFormatDate(it)
                    }
                    gamingMachineTax.serialNo?.let {
                        binding.txtValueThree.text = it
                    }
                    gamingMachineTax.estimatedTax?.let {
                        binding.txtValueFour.text = formatWithPrecision(it)
                    }
                    gamingMachineTax.active?.let {
                        binding.txtValueFive.text = if (it == "Y") parent?.context?.resources?.getString(R.string.active) else parent?.context?.resources?.getString(R.string.inactive)
                    }
                    gamingMachineTax.accountName?.let {
                        binding.txtValueSix.text = it
                    }
                    gamingMachineTax.accountPhone?.let {
                        binding.txtValueSeven.text = it
                    }

                }

                mGamingMachineTax.documentDetails?.comDocumentReferences?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it as ArrayList<COMDocumentReference>, listner)
                }
            }

            is PropertyTax4Business -> {

                val childData = getChild(groupPosition, childPosition) as PropertyTax4Business
//                if (childData.taxRuleBookCode == "RES_PROP" || childData.taxRuleBookCode == "COM_PROP")
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.registration_date)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.property_name)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.property_id_sycotax)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.property_type)
                binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.status)


                binding.txtValueOne.text = formatDate(childData.registrationDate)
                binding.txtValueTwo.text = childData.propertyName
                binding.txtValueThree.text = childData.propertySycoTaxID
                binding.txtValueFour.text = childData.propertyType
                binding.txtValueFive.text = childData.status
                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                binding.llFour.visibility = VISIBLE
                binding.llFive.visibility = VISIBLE

                childData.documentDetails?.comDocumentReferences?.let {
                    binding.rcvDocuments.visibility = VISIBLE
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it as ArrayList<COMDocumentReference>, listner)
                }
            }
        }

        return binding.root
    }

    override fun getGroup(groupPosition: Int): Any {
        return group[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return group.size
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View {
        val binding: ItemBusinessSummaryApprovalGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.item_business_summary_approval_group, parent, false)

        binding.header.text = getGroup(groupPosition) as String

        return binding.root
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun update(groupName: String, childData: List<Any>, expandableView: ExpandableListView) {
        group.add(groupName)
        child[groupName] = childData
        expandableView.expandGroup(group.size - 1, true)
        notifyDataSetChanged()
    }

}