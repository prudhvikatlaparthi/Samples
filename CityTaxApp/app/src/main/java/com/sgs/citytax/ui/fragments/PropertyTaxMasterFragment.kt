package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GenericGetDetailsBySycotax
import com.sgs.citytax.api.response.PropertyDetailsBySycoTax
import com.sgs.citytax.api.response.PropertyLandTaxDetailsList
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.PropertyTax4Business
import com.sgs.citytax.ui.LandTaxActivity
import com.sgs.citytax.ui.PropertyTaxActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.PropertyTaxMasterAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.Pagination

class PropertyTaxMasterFragment : BaseFragment(), IClickListener{

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER

    private var mAdapter: PropertyTaxMasterAdapter? = null
    lateinit var pagination: Pagination

    private var taxRuleBook:Constant.TaxRuleBook = Constant.TaxRuleBook.COM_PROP
    private var setViewForGeoSpatial: Boolean? = false  //todo New key to Hide views for geo spacial- Busianess Record - 15/3/2022, not used fromScreen, to not to disturb th flow

    companion object {
        var primaryKey = 0
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
            setViewForGeoSpatial = arguments?.getBoolean(Constant.KEY_GEO_SPATIAL_VIEW) ?: false
        }
        //endregion
        setViews()
        //bindData()
        setListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        pagination = Pagination(1, 10, mBinding.recyclerView) { pageNumber, PageSize ->
            bindData(pageNumber, PageSize)
        }
        return mBinding.root
    }

    private fun setViews() {

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> mBinding.fabAdd.visibility = View.VISIBLE
            Constant.ScreenMode.VIEW -> mBinding.fabAdd.visibility = View.GONE
        }
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = PropertyTaxMasterAdapter(this, fromScreen, mListener?.screenMode)

    }

    private fun bindData(pageNumber: Int, pageSize: Int) {

        mBinding.recyclerView.adapter = mAdapter
        mListener?.showProgressDialog()

        var isProperty = true
        var isLand = true
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
            taxRuleBook = Constant.TaxRuleBook.COM_PROP
            isProperty = true
            isLand = false
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND) {
            taxRuleBook = Constant.TaxRuleBook.LAND_PROP
            isProperty = false
            isLand = true
        }

        APICall.getPropertyTax4Business(primaryKey, pageNumber, pageSize, isProperty, isLand, object : ConnectionCallBack<PropertyLandTaxDetailsList> {
            override fun onSuccess(taxDetails: PropertyLandTaxDetailsList) {
                mListener?.dismissDialog()

                pagination.totalRecords = taxDetails.totalRecords
                setData(taxDetails)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()

            }
        })


    }

    private fun setData(taxDetails: PropertyLandTaxDetailsList) {
        pagination.setIsScrolled(false)
        if (taxDetails.results?.propertyTaxDetails != null) {
            pagination.stopPagination(taxDetails.results?.propertyTaxDetails!!.size)
        } else {
            pagination.stopPagination(0)
        }
        /*//filtering the list and seperating them based on land & property type
        var filteredPropertyTaxDetails: ArrayList<PropertyTax4Business> = arrayListOf()
        for (detail in taxDetails.results!!.propertyTaxDetails) {
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY) {
                // if (detail.taxRuleBookCode == "RES_PROP" || detail.taxRuleBookCode == "COM_PROP")
                filteredPropertyTaxDetails.add(detail)
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND) {
                // if (detail.taxRuleBookCode == "LAND_PROP")
                filteredPropertyTaxDetails.add(detail)
            }
        }*/

        if(mAdapter == null) {
            mAdapter = PropertyTaxMasterAdapter(this, fromScreen, mListener?.screenMode)
            mBinding?.recyclerView?.adapter = mAdapter
        }

        mAdapter!!.setTaxRuleBook(taxRuleBook)

        val specificationValueSets = taxDetails.results?.propertyTaxDetails
        mAdapter!!.update(specificationValueSets as List<PropertyTax4Business>)

        /* val adapter = (mBinding.recyclerView.adapter as PropertyTaxMasterAdapter)
         adapter.clear()
         //send the filtered arraylist here ...
         adapter.update(filteredPropertyTaxDetails)*/
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val intent = Intent(context, ScanActivity::class.java)
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY)
                else
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND)
                startActivity(intent)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    val taxDetails = obj as PropertyTax4Business

                    searchPropertyDetailsBySycoTax(taxDetails.propertySycoTaxID)

                }
//                R.id.txtDelete -> {
//
//                }

                else -> {

                }
            }
        }
    }


    private fun searchPropertyDetailsBySycoTax(sycoTaxID: String?) {
        val genericGetDetailsBySycotax = GenericGetDetailsBySycotax()
        genericGetDetailsBySycotax.sycoTaxId = sycoTaxID
        mListener?.showProgressDialog()
        APICall.searchPropertyDetailsBySycoTax(genericGetDetailsBySycotax, object : ConnectionCallBack<PropertyDetailsBySycoTax> {
            override fun onSuccess(response: PropertyDetailsBySycoTax) {
                mListener?.dismissDialog()
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND) {
                    navigateToLandTax(sycoTaxID, response)
                } else {
                    navigateToPropertyTax(sycoTaxID, response)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun navigateToPropertyTax(sycoTaxID: String?, propertyDetailsBySycoTax: PropertyDetailsBySycoTax) {
        val intent = Intent(context, PropertyTaxActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY)
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, taxRuleBook.Code)
        intent.putExtra(Constant.KEY_PROPERTY_DETAILS, propertyDetailsBySycoTax)
        intent.putExtra(Constant.KEY_ACCOUNT_ID, primaryKey)
        intent.putExtra(Constant.KEY_GEO_SPATIAL_VIEW, setViewForGeoSpatial)
        if (propertyDetailsBySycoTax.address.size > 0) {
            intent.putParcelableArrayListExtra(Constant.KEY_ADDRESS, propertyDetailsBySycoTax.address)
        }
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        startActivity(intent)
    }

    private fun navigateToLandTax(sycoTaxID: String?, propertyDetailsBySycoTax: PropertyDetailsBySycoTax) {
        val intent = Intent(context, LandTaxActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND)
        intent.putExtra(Constant.KEY_PROPERTY_DETAILS, propertyDetailsBySycoTax)
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, taxRuleBook.Code)
        intent.putExtra(Constant.KEY_ACCOUNT_ID, primaryKey)
        intent.putExtra(Constant.KEY_GEO_SPATIAL_VIEW, setViewForGeoSpatial)
        if (propertyDetailsBySycoTax.address.size > 0) {
            intent.putParcelableArrayListExtra(Constant.KEY_ADDRESS, propertyDetailsBySycoTax.address)
        }
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        startActivity(intent)
//        activity?.finish()
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {


    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode

    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    override fun onResume() {
        super.onResume()
        if(mAdapter != null)
            mAdapter!!.clear()
        pagination.setDefaultValues()
    }


}