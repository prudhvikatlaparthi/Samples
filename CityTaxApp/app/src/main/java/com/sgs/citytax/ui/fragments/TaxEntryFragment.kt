package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentTaxEntryBinding
import com.sgs.citytax.model.CRMActivityDomain
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.model.VuInvProducts
import com.sgs.citytax.util.Constant

class TaxEntryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentTaxEntryBinding
    private var mListener: Listener? = null
    private var mProducts: MutableList<VuInvProducts>? = arrayListOf()
    private var mActivityDomains: ArrayList<CRMActivityDomain>? = arrayListOf()
    private var mTaskCodes: ArrayList<TaskCode>? = arrayListOf()
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER

    companion object {
        @JvmStatic
        fun newInstance(code: Constant.QuickMenu) = TaxEntryFragment().apply {
            mCode = code
        }
    }

    override fun initComponents() {
        fetchProducts()
        setViews()
        setListeners()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tax_entry, container, false)
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

    override fun onDestroy() {
        super.onDestroy()
        mListener = null
    }

    private fun setListeners() {
        mBinding.spnProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var product: VuInvProducts? = VuInvProducts()
                if (parent != null && parent.selectedItem != null)
                    product = parent.selectedItem as VuInvProducts
                if (product?.productCode != null)
                    fetchActivityDomains(product.productCode!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mBinding.llPDOChildTab.setOnClickListener(this)
        mBinding.llROPChildTab.setOnClickListener(this)
        mBinding.llRentalDetailsChildTab.setOnClickListener(this)
        mBinding.llAccountPhoneChildTab.setOnClickListener(this)
        mBinding.llAccountEmailsChildTab.setOnClickListener(this)
        mBinding.llAccountAddressChildTab.setOnClickListener(this)
        mBinding.llVehicleOwnerShipChildTab.setOnClickListener(this)
        mBinding.llPropertyOwnerShipChildTab.setOnClickListener(this)
        mBinding.llCorporateTurnOverChildTab.setOnClickListener(this)
    }

    private fun bindProducts(products: List<VuInvProducts>? = arrayListOf()) {
        mProducts?.clear()
        mProducts?.add(0, VuInvProducts("-1", getString(R.string.select), ""))
        if (!products.isNullOrEmpty())
            mProducts?.addAll(products)
        mBinding.spnProduct.adapter = ArrayAdapter<VuInvProducts>(requireContext(), android.R.layout.simple_spinner_dropdown_item, mProducts!!)
    }

    private fun bindActivityDomains(activityDomains: List<CRMActivityDomain>? = arrayListOf(), taskCodes: List<TaskCode>? = arrayListOf()) {
        mActivityDomains?.clear()
        mActivityDomains?.add(0, CRMActivityDomain(getString(R.string.select), -1))
        if (!activityDomains.isNullOrEmpty())
            mActivityDomains?.addAll(activityDomains)
        mBinding.spnActivityDomain.adapter = ArrayAdapter<CRMActivityDomain>(requireContext(), android.R.layout.simple_list_item_1, mActivityDomains!!)

        mTaskCodes?.clear()
        if (!taskCodes.isNullOrEmpty())
            mTaskCodes?.addAll(taskCodes)
        setViews()
    }

    private fun fetchProducts() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_CustomerProductInterests", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                bindProducts(response.products)
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                bindProducts()
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun fetchActivityDomains(productCode: String) {
        if (TextUtils.isEmpty(productCode) || productCode == "-1") {
            bindActivityDomains()
            return
        }
        /* mListener?.showProgressDialog()
         APICall.getActivityDomains(productCode, object : ConnectionCallBack<ActivityDomains> {
             override fun onSuccess(response: ActivityDomains) {
                 bindActivityDomains(response.activityDomains, response.taskCodes)
                 mListener?.dismissDialog()
             }

             override fun onFailure(message: String) {
                 bindActivityDomains()
                 mListener?.dismissDialog()
                 mListener?.showAlertDialog(message)
             }
         })*/
    }

    private fun setViews() {
        mBinding.llChildTabs.visibility = GONE
        mBinding.llAccountPhoneChildTab.visibility = GONE
        mBinding.llAccountEmailsChildTab.visibility = GONE
        mBinding.llAccountAddressChildTab.visibility = GONE
        mBinding.llCorporateTurnOverChildTab.visibility = GONE
        mBinding.llPropertyOwnerShipChildTab.visibility = GONE
        mBinding.llVehicleOwnerShipChildTab.visibility = GONE
        mBinding.llPDOChildTab.visibility = GONE
        mBinding.llROPChildTab.visibility = GONE
        mBinding.llRentalDetailsChildTab.visibility = GONE

        if (!mTaskCodes.isNullOrEmpty()) {
            mBinding.llChildTabs.visibility = VISIBLE

            for (taskCode: TaskCode in mTaskCodes!!) {
                when (taskCode.taskCode) {
                    "CRM_AccountPhoneEntry" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llAccountPhoneChildTab.visibility = VISIBLE
                    }
                    "CRM_AccountEmailEntry" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llAccountEmailsChildTab.visibility = VISIBLE
                    }
                    "COM_GeoAddressEntry" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llAccountAddressChildTab.visibility = VISIBLE
                    }
                    "CRM_CorporateTurnoverEntry" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llCorporateTurnOverChildTab.visibility = VISIBLE
                    }
                    "CRM_PropertyOwnership" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llPropertyOwnerShipChildTab.visibility = VISIBLE
                    }
                    "ADM_VehicleOwnership" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llVehicleOwnerShipChildTab.visibility = VISIBLE
                    }
                    "CRM_PublicDomainOccupancy" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llPDOChildTab.visibility = VISIBLE
                    }
                    "CRM_RightOfPlacesEntry" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llROPChildTab.visibility = VISIBLE
                    }
                    "VU_CRM_PropertyRents" -> {
                        if (taskCode.IsMultiple == 'Y')
                            mBinding.llRentalDetailsChildTab.visibility = VISIBLE
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llRentalDetailsChildTab -> {
                val fragment = RentalMasterFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_RENTAL_DETAILS)
                mListener?.showToolbarBackButton(R.string.title_rental_details)
                mListener?.addFragment(fragment, true)
            }
            R.id.llAccountPhoneChildTab -> {
                val fragment = PhoneMasterFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                        ?: 0)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_PHONE)
                mListener?.showToolbarBackButton(R.string.title_phones)
                mListener?.addFragment(fragment, true)
            }
            R.id.llAccountEmailsChildTab -> {
                val fragment = EmailMasterFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                        ?: 0)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_EMAIL)
                mListener?.showToolbarBackButton(R.string.title_emails)
                mListener?.addFragment(fragment, true)
            }
            R.id.llAccountAddressChildTab -> {
                val fragment = AddressMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                        ?: 0)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_ACCOUNT_ADDRESSES_LIST)

                mListener?.showToolbarBackButton(R.string.title_address)
                mListener?.addFragment(fragment, true)
            }
            R.id.llCorporateTurnOverChildTab -> {
                val fragment = CorporateTurnOverMasterFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_CORPORATE_TURNOVER)
                mListener?.showToolbarBackButton(R.string.corporate_turnover)
                mListener?.addFragment(fragment, true)
            }
            R.id.llPDOChildTab -> {
                val fragment = ROPPDOMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY, true)
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
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putBoolean(Constant.KEY_IS_PUBLIC_DOMAIN_OCCUPANCY, false)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_PUBLIC_DOMAIN_OCCUPANCY_LIST)

                mListener?.showToolbarBackButton(R.string.rights_of_places_in_markets)
                mListener?.addFragment(fragment, true)
            }
            R.id.llVehicleOwnerShipChildTab -> {
                val fragment = VehicleOwnershipMasterFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this, Constant.REQUEST_CODE_VEHICLE_OWNERSHIP_LIST)

                mListener?.showToolbarBackButton(R.string.vehicle_ownership)
                mListener?.addFragment(fragment, true)
            }
            R.id.llPropertyOwnerShipChildTab -> {
                val fragment = PropertyOwnershipMasterFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNERSHIP_LIST)
                mListener?.showToolbarBackButton(R.string.property_ownership)
                mListener?.addFragment(fragment, true)
            }
            else -> {

            }
        }
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
    }
}