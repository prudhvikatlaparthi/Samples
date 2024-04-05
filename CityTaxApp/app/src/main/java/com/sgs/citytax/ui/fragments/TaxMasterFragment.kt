package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.ActivityDomains
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.ProductCategory
import com.sgs.citytax.databinding.FragmentTaxMasterBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.model.VuInvProducts
import com.sgs.citytax.model.VuTax
import com.sgs.citytax.ui.adapter.TaxMasterAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.ICheckListener
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class TaxMasterFragment : BaseFragment(), IClickListener, ICheckListener {

    private lateinit var mBinding: FragmentTaxMasterBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mPrimaryKey: Int = 0
    private var mListener: Listener? = null
    private var mProducts: List<VuInvProducts>? = arrayListOf()
    private var mProduct: VuInvProducts? = null
    private var mProductCategories: List<ProductCategory>? = arrayListOf()
    private var savedTaxes: MutableList<VuTax> = arrayListOf()

    //    private var isEdit: Boolean = false
    private var mShowProportionalDutyOnCP: Boolean = false

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
//            isEdit = arguments?.getBoolean(Constant.KEY_EDIT) ?: false

        }
        //endregion
        setView()
        fetchAllProductsLOVs()
        setListeners()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tax_master, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as Listener
        } catch (e: Exception) {
            throw ClassCastException(context.toString() + "must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun filterProducts(categoryID: Int? = 0): ArrayList<VuInvProducts> {
        val products = arrayListOf<VuInvProducts>()
        mProducts?.let {
            for (product in it) {
                categoryID?.let { categoryID ->
                    if (categoryID == product.categoryID)
                        products.add(product)
                }
            }
        }
        return products
    }

    private fun showProductSelectionPopup() {
        mProducts?.let {
            val dialog = Dialog(requireActivity(), R.style.Theme_MaterialComponents_Light_Dialog_Alert)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_product_selection)
            val btnOkay = dialog.findViewById<TextView>(R.id.btnOk)
            val btnCancel = dialog.findViewById<TextView>(R.id.btnCancel)
            val spnCategory = dialog.findViewById<Spinner>(R.id.spinnerProductCategory)
            val spnProduct = dialog.findViewById<Spinner>(R.id.spinnerProduct)

            spnCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var category: ProductCategory? = ProductCategory()
                    if (p0 != null && p0.selectedItem != null)
                        category = p0.selectedItem as ProductCategory

                    category?.categoryID?.let {
                        val products = filterProducts(it)
                        if (products.isNullOrEmpty())
                            spnProduct.adapter = null
                        else {
                            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, products)
                            spnProduct.adapter = adapter
                        }
                    }
                }
            }

            mProductCategories?.let {
                if (it.isNullOrEmpty())
                    spnCategory.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
                    spnCategory.adapter = adapter
                }
            }

            btnOkay.setOnClickListener {
                val selObj: VuInvProducts? = spnProduct.selectedItem as VuInvProducts?

                selObj?.let {
                    mProduct = selObj
                    var isExists = false
                    for (product in savedTaxes.iterator()) {
                        if (product.productCode == selObj.productCode) {
                            isExists = true
                            break
                        }
                    }

                    if (isExists)
                        mListener?.showAlertDialog(getString(R.string.product_already_added))
                    else {
                        val productInterests = VuTax()
                        productInterests.productCode = selObj.productCode
                        productInterests.product = selObj.product
                        getActivityTaskCodeList(productInterests)
                    }
                }

                dialog.dismiss()

            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }


    private fun fetchCount(filterColumns: List<FilterColumn>, tableOrViewName: String, primaryKeyColumnName: String) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = tableOrViewName
        tableDetails.primaryKeyColumnName = primaryKeyColumnName
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                bindCounts(tableOrViewName, 0)
            }

            override fun onSuccess(response: Int) {
                bindCounts(tableOrViewName, response)
            }
        })
    }

    private fun bindCounts(tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "VU_CRM_RightOfPlaces" -> {
                savedTaxes.forEach { it.noOfROP = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "OrganizationID"
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "VU_CRM_PublicDomainOccupancy", "PublicDomainOccupancyID")
            }
            "VU_CRM_PublicDomainOccupancy" -> {
                savedTaxes.forEach { it.noOfPDO = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "OrganizationID"
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "CRM_CorporateTurnover", "CorporateTurnoverID")
            }
            "CRM_CorporateTurnover" -> {
                savedTaxes.forEach { it.noOfCorporateTurnOver = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "VU_ADM_VehicleOwnership", "VehicleOwnershipID")
            }
            "VU_ADM_VehicleOwnership" -> {
                savedTaxes.forEach { it.noOfVehicleOwnership = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "OrganizationID"
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "VU_CRM_Advertisements", "AdvertisementID")

            }
            "VU_CRM_Advertisements" -> {
                savedTaxes.forEach { it.noOfAdvertisements = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "OrganizationID"
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "CRM_SHOWS", "ShowID")
            }
            "CRM_SHOWS" -> {
                savedTaxes.forEach { it.noOfShows = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "OrganizationID"
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "CRM_Hotels", "HotelID")
            }
            "CRM_Hotels" -> {
                savedTaxes.forEach { it.noOfHotel = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "OrganizationID"
                filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "CRM_Licenses", "LicenseID")
            }
            "CRM_Licenses" -> {
                savedTaxes.forEach { it.noOfLicenses = count }
                (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                (mBinding.recyclerView.adapter as TaxMasterAdapter).add(savedTaxes)
            }
        }
    }

    private fun setView() {
        // todo commented for onsite requirement
//        when (mListener?.screenMode) {
//            Constant.ScreenMode.EDIT -> mBinding.fabAddTax.visibility = View.VISIBLE
//            Constant.ScreenMode.VIEW -> mBinding.fabAddTax.visibility = View.GONE
//        }

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
        {
            mBinding.fabAddTax.visibility = View.GONE
        }
        mBinding.recyclerView.adapter = TaxMasterAdapter(this, this, mListener?.screenMode, fromScreen)
    }

    private fun fetchAllProductsLOVs() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_CustomerProductInterests", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mProducts = response.products
                mProductCategories = response.productCategories
                mListener?.dismissDialog()
                fetchSavedProducts()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                mProducts = arrayListOf()
            }
        })
    }

    private fun getActivityTaskCodeList(product: VuTax) {
        mListener?.showProgressDialog()
        APICall.getActivityDomains(product.productCode, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                ?: 0, object : ConnectionCallBack<ActivityDomains> {
            override fun onSuccess(response: ActivityDomains) {
                product.taskCodes = response.taskCodes
                saveTax(product)
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (message.isNotEmpty()) {
                    product.taskCodes = null
                    saveTax(product)
                } else mListener?.showAlertDialog(message)

            }
        })
    }

    private fun addTaxView(product: VuTax) {
        (mBinding.recyclerView.adapter as TaxMasterAdapter).add(listOf(product))
    }

    private fun saveTax(product: VuTax) {
        mListener?.showProgressDialog()
        val productInterests = CustomerProductInterests()
        productInterests.active = 'Y'
        productInterests.productCode = product.productCode!!
        productInterests.customerID = mPrimaryKey

        /*if (mProductInterests != null && mProductInterests!!.customerProductInterestID != 0)
            productInterests.customerProductInterestID = mProductInterests!!.customerProductInterestID!!*/

        APICall.saveCustomerProductInterests(productInterests, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                savedTaxes.add(product)
                //addTaxView(product)
                fetchSavedProducts()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun fetchSavedProducts() {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getTaxes("VU_CRM_CustomerProductInterests", mPrimaryKey,
                    0, object : ConnectionCallBack<List<VuTax>> {
                override fun onSuccess(response: List<VuTax>) {
                    mListener?.dismissDialog()
                    savedTaxes = response.toMutableList()
                    (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                    for (tax: VuTax in savedTaxes) {
                        //TODO "mShowProportionalDutyOnCP" is jsut making inverse of the real value
                        //mShowProportionalDutyOnCP = !mShowProportionalDutyOnCP && tax.taxRuleBookCode == "CP"
                            if(!mShowProportionalDutyOnCP){
                                mShowProportionalDutyOnCP = tax.taxRuleBookCode == "CP"
                            }
                        addTaxView(tax)
                    }
                    fetchChildEntriesCount()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    savedTaxes = arrayListOf()
                    (mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun fetchChildEntriesCount() {
        ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId?.let {
            val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
            val filterColumn = FilterColumn()
            filterColumn.columnName = "OrganizationID"
            filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            fetchCount(listFilterColumn, "VU_CRM_RightOfPlaces", "RightOfPlaceID")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fetchChildEntriesCount()
    }

    private fun setListeners() {
        mBinding.fabAddTax.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                showProductSelectionPopup()
            }
        })
    }

    private fun deleteCustomerProduct(view: View) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()

            APICall.deleteCustomerProductInterests(mPrimaryKey, view.tag as String,
                    object : ConnectionCallBack<Boolean> {
                        override fun onSuccess(response: Boolean) {
                            mListener?.dismissDialog()
                            if (response)
                                fetchSavedProducts()
                        }

                        override fun onFailure(message: String) {
                            mListener?.dismissDialog()
                            //(mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                            if (message != getString(R.string.msg_no_data))
                                mListener?.showAlertDialog(message)
                        }
                    })
        }
    }

    private fun updateStatusCustomerProductInterest(view: View, sts: String) {
        if (mPrimaryKey != 0) {
            mListener?.showProgressDialog()

            APICall.updateStatusCustomerProdIntrst(mPrimaryKey, view.tag as String, sts,
                    object : ConnectionCallBack<Boolean> {
                        override fun onSuccess(response: Boolean) {
                            mListener?.dismissDialog()
                            if (response)
                                fetchSavedProducts()
                        }

                        override fun onFailure(message: String) {
                            mListener?.dismissDialog()
                            //(mBinding.recyclerView.adapter as TaxMasterAdapter).clear()
                            if (message != getString(R.string.msg_no_data))
                                mListener?.showAlertDialog(message)
                        }
                    })
        }
    }

    override fun onCheckedChange(view: View, position: Int, obj: Any, sts: String) {
        when (view.id) {
            R.id.checkbox -> {
                Log.e("checkbox val", ">>>>>>>$sts")
                updateStatusCustomerProductInterest(view, sts)
            }
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        when (view.id) {

            R.id.btnDelete -> {
                deleteCustomerProduct(view)
            }
            R.id.llCorporateTurnOverChildTab -> {
                val fragment = CorporateTurnOverMasterFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putParcelable(Constant.KEY_PRODUCT_DETAILS, mProduct)
                bundle.putBoolean(Constant.KEY_SHOW_PROPORTIONAL_DUTY, mShowProportionalDutyOnCP)
                bundle.putString(Constant.KEY_PRODUCT_BILLING_CYCLE, savedTaxes?.get(position).billingCycle)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)

                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_CORPORATE_TURNOVER)
                mListener?.showToolbarBackButton(R.string.corporate_turnover)
                mListener?.addFragment(fragment, true)
            }
            R.id.llPropertyOwnerShipChildTab -> {
                val fragment = PropertyOwnershipMasterFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNERSHIP_LIST)
                mListener?.showToolbarBackButton(R.string.property_ownership)
                mListener?.addFragment(fragment, true)
            }
            R.id.llVehicleOwnerShipChildTab -> {
                val fragment = VehicleOwnershipMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_OWNERSHIP_LIST)

                mListener?.showToolbarBackButton(R.string.tax_master_vehicle_ownership)
                mListener?.addFragment(fragment, true)
            }
            R.id.llPDOChildTab -> {
                val fragment = ROPPDOMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY, true)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_PUBLIC_DOMAIN_OCCUPANCY_LIST)

                mListener?.showToolbarBackButton(R.string.public_domain_occupancy)
                mListener?.addFragment(fragment, true)
            }
            R.id.llROPChildTab -> {
                val fragment = ROPPDOMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY, false)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_PUBLIC_DOMAIN_OCCUPANCY_LIST)

                mListener?.showToolbarBackButton(R.string.rights_of_places_in_markets)
                mListener?.addFragment(fragment, true)
            }
            R.id.llRentalDetailsChildTab -> {
                val fragment = RentalMasterFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_RENTAL_DETAILS)
                mListener?.showToolbarBackButton(R.string.title_rental_details)
                mListener?.addFragment(fragment, true)
            }
            R.id.llAdvertisementChildTab -> {
                val fragment = AdvertisementMasterFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ADVERTISEMENTS)
                mListener?.showToolbarBackButton(R.string.title_advertisements)
                mListener?.addFragment(fragment, true)
            }
            R.id.llShowTaxChildTab -> {
                val fragment = ShowTaxMasterFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_SHOWS)
                mListener?.showToolbarBackButton(R.string.title_shows)
                mListener?.addFragment(fragment, true)
            }
            R.id.llHotelTaxChildTab -> {
                val fragment = HotelListFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_HOTELS)
                mListener?.showToolbarBackButton(R.string.title_hotels)
                mListener?.addFragment(fragment, true)
            }
            R.id.llLicenseTaxChildTab -> {
                val fragment = LicenseMasterFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constant.KEY_TASK_CODE, view.tag as TaskCode)
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, (obj as VuTax).taxRuleBookCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_LICENSES)
                mListener?.showToolbarBackButton(R.string.title_licenses)
                mListener?.addFragment(fragment, true)
            }
            else -> {

            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showToolbarBackButton(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }
}