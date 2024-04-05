package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
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
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.GetSpecificationValueBusinessChildSet
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.model.Weapon
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.WeaponRegisterActivity
import com.sgs.citytax.ui.adapter.CartAdapter
import com.sgs.citytax.ui.adapter.WeaponAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.Pagination

class WeaponTaxMasterFragment : BaseFragment(), IClickListener{

    private var mBinding: FragmentMasterListBinding? = null
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    lateinit var pagination: Pagination
    private var mAdapter: WeaponAdapter? = null

    companion object {
        var primaryKey = 0
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            //primaryKey is Business accountid
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
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
        pagination = Pagination(1, 10, mBinding?.recyclerView) { pageNumber, PageSize ->
            bindData(pageNumber, PageSize)
        }
        return mBinding?.root
    }

    private fun setViews() {

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> mBinding?.fabAdd?.visibility = View.VISIBLE
            Constant.ScreenMode.VIEW -> mBinding?.fabAdd?.visibility = View.GONE
        }
        mBinding?.recyclerView?.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = WeaponAdapter(this, fromScreen, mListener?.screenMode)

    }

    private fun bindData(pageNumber: Int, pageSize: Int) {

        mBinding?.recyclerView?.adapter = mAdapter
        mListener?.showProgressDialog()


        val searchFilter = AdvanceSearchFilter()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageNumber
        searchFilter.query = null
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        val filterColumn = FilterColumn()
        filterColumn.columnName = "AccountID"
        filterColumn.columnValue = primaryKey.toString()  //primaryKey is Business accountid
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)

        searchFilter.filterColumns = listFilterColumn

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_Weapons"
        tableDetails.primaryKeyColumnName = "WeaponID"
        tableDetails.TableCondition = "AND"
        tableDetails.selectColoumns = "WeaponID,WeaponSycotaxID,SerialNo,RegistrationDate,AccountID,WeaponType,Active"
        tableDetails.sendCount = false

        searchFilter.tableDetails = tableDetails

        APICall.getDynamicValuesBusinessChildList(searchFilter, object : ConnectionCallBack<GetSpecificationValueBusinessChildSet> {
            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding?.recyclerView?.adapter = null
            }

            override fun onSuccess(response: GetSpecificationValueBusinessChildSet) {
                mListener?.dismissDialog()

                pagination.totalRecords = response.totalSearchedRecords
                setData(response)
            }
        })

        /*APICall.getWeaponList(primaryKey, "VU_CRM_Weapons", object : ConnectionCallBack<List<Weapon>> {
            override fun onSuccess(response: List<Weapon>) {
                mListener?.dismissDialog()
                val adapter = (mBinding?.recyclerView?.adapter as WeaponAdapter)
                adapter.clear()
                adapter.update(response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding?.recyclerView?.adapter = null

            }
        })*/

    }

    private fun setData(response: GetSpecificationValueBusinessChildSet) {
        pagination.setIsScrolled(false)
        if (response.results?.VU_CRM_Weapons != null) {
            pagination.stopPagination(response.results?.VU_CRM_Weapons!!.size)
        } else {
            pagination.stopPagination(0)
        }
        if(mAdapter == null) {
            mAdapter = WeaponAdapter(this, fromScreen, mListener?.screenMode)
            mBinding?.recyclerView?.adapter = mAdapter
        }
        if (response.results != null) {
            val specificationValueSets = response.results?.VU_CRM_Weapons
            mAdapter!!.update(specificationValueSets as List<Weapon>)
        }
    }

    private fun setListeners() {
        mBinding?.fabAdd?.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                startActivity(intent)
            }

        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {

                    var weapon = obj as Weapon

                    val intent = Intent(context, WeaponRegisterActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                    intent.putExtra(Constant.KEY_SCREEN_MODE, mListener?.screenMode)
                    weapon.let {
                        intent.putExtra(Constant.KEY_SYCO_TAX_ID, weapon.weaponSycotaxID)
                        intent.putExtra(Constant.KEY_WEAPON_TAX, it)

                        intent.putExtra(Constant.KEY_PRIMARY_KEY, primaryKey)
                        intent.putExtra(Constant.KEY_ACCOUNT_ID, weapon.weaponID)
                    }
                    startActivity(intent)
                }
                R.id.txtDelete -> {
                    val weapon = obj as Weapon
                    weapon.weaponID?.let { it1 -> deleteWeaponTax(it1) }
                }

                else -> {

                }
            }
        }
    }

    private fun deleteWeaponTax(weaponId: Int) {
        mListener?.showProgressDialog()
        APICall.deleteWeapon(weaponId, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                if(mAdapter != null)
                    mAdapter!!.clear()
                pagination.setDefaultValues()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (message.isNotEmpty()) {
                    mListener?.showAlertDialog(message)
                }
            }
        })
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
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