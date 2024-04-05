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
import com.sgs.citytax.ui.CartTaxActivity
import com.sgs.citytax.ui.ScanActivity
import com.sgs.citytax.ui.adapter.CartAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.Pagination

class CartTaxMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    lateinit var pagination: Pagination
    private var mAdapter: CartAdapter? = null

    companion object {
        var primaryKey = 0
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
        }
        //endregion
        setViews()
        // bindData()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
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
        mBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )

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
        tableDetails.tableOrViewName = "VU_CRM_Carts"
        tableDetails.primaryKeyColumnName = "CartID"
        tableDetails.TableCondition = "AND"
        tableDetails.selectColoumns =
            "CartID,CartSycotaxID,CartNo,RegistrationDate,AccountID,CartType,Active"
        tableDetails.sendCount = false

        searchFilter.tableDetails = tableDetails

        APICall.getDynamicValuesBusinessChildList(
            searchFilter,
            object : ConnectionCallBack<GetSpecificationValueBusinessChildSet> {
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


        /*mBinding.recyclerView.adapter = mAdapter
        mListener?.showProgressDialog()
        APICall.getCartList(primaryKey, "VU_CRM_Carts", object : ConnectionCallBack<List<CartTax>> {
            override fun onSuccess(response: List<CartTax>) {
                mListener?.dismissDialog()
                val adapter = (mBinding.recyclerView.adapter as CartAdapter)
                adapter.clear()
                adapter.update(response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding.recyclerView.adapter = null

            }
        })*/

    }

    private fun setData(response: GetSpecificationValueBusinessChildSet) {
        pagination.setIsScrolled(false)
        if (response.results?.VU_CRM_Carts != null) {
            pagination.stopPagination(response.results?.VU_CRM_Carts!!.size)
        } else {
            pagination.stopPagination(0)
        }

        if (mAdapter == null) {
            mAdapter = CartAdapter(this, fromScreen, mListener?.screenMode)
            mBinding?.recyclerView?.adapter = mAdapter
        }

        /* var adapter = (mBinding?.recyclerView?.adapter as CartAdapter)
         adapter.clear()*/
        if (response.results != null) {
            val specificationValueSets = response.results?.VU_CRM_Carts
            mAdapter!!.update(specificationValueSets as List<CartTax>)
        }
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
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
                    var cartTax = obj as CartTax

                    val intent = Intent(context, CartTaxActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                    intent.putExtra(Constant.KEY_SCREEN_MODE, mListener?.screenMode)
                    cartTax?.let {
                        intent.putExtra(Constant.KEY_SYCO_TAX_ID, it.cartSycoTaxID)
                        intent.putExtra(Constant.KEY_CART_TAX, it)

                        intent.putExtra(Constant.KEY_PRIMARY_KEY, primaryKey)
                        intent.putExtra(Constant.KEY_ACCOUNT_ID, it.cartID)
                    }
                    startActivity(intent)

                }
                R.id.txtDelete -> {
                    var cartTax = obj as CartTax
                    cartTax.cartID?.let { it1 -> deleteCartTax(it1) }
                }

                else -> {

                }
            }
        }
    }

    private fun deleteCartTax(cardId: Int) {
        mListener?.showProgressDialog()
        APICall.deleteCart(cardId, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                if (mAdapter != null)
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
        if (mAdapter != null)
            mAdapter!!.clear()
        pagination.setDefaultValues()
    }

}