package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.DocumentAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class DocumentsMasterFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()

    companion object {
        fun getTableName(screen: Constant.QuickMenu) =
            if (screen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                "CRM_Organizations"
            else if (screen == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER || screen == Constant.QuickMenu.QUICK_MENU_SALES_TAX || screen == Constant.QuickMenu.QUICK_MENU_CREATE_ASSET_BOOKING || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING || screen == Constant.QuickMenu.QUICK_MENU_CREATE_CITIZEN || screen == Constant.QuickMenu.QUICK_MENU_SERVICE)
                "CRM_Contacts"
            else if (screen == Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_AGENT || screen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT || screen == Constant.QuickMenu.QUICK_MENU_VIEW_AGENT)
                "CRM_Agents"
            else if (screen == Constant.QuickMenu.QUICK_MENU_RENTAL_TAX)
                "CRM_PropertyRents"
            else if (screen == Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET)
                "AST_Assets"
            else if (screen == Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
                "CRM_Weapons"
            else if (screen == Constant.QuickMenu.QUICK_MENU_CREATE_CART_TAX || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX)
                "CRM_Carts"
            else if (screen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX)
                "CRM_GamingMachines"
            else if (screen == Constant.QuickMenu.QUICK_MENU_PUBLIC_DOMAIN_OCCUPANCY)
                "CRM_PublicDomainOccupancy"
            else if (screen == Constant.QuickMenu.QUICK_MENU_RIGHTS_OF_MARKET_PLACES)
                "CRM_RightOfPlaces"
            else if (screen == Constant.QuickMenu.QUICK_MENU_CORPORATE_TURN_OVER)
                "CRM_CorporateTurnover"
            else if (screen == Constant.QuickMenu.QUICK_MENU_ADVERTISEMENTS)
                "CRM_Advertisements"
            else if (screen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP || screen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE || screen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP)
                "ADM_Vehicles"
            else if (screen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_CITIZEN_DOCUMENT)
                "ADM_VehicleOwnerShip"
            else if (screen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT || screen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE)
                "LAW_ViolationTickets"
            else if (screen == Constant.QuickMenu.QUICK_MENU_IMPONDMENT)
                "LAW_Impoundments"
            else if (screen == Constant.QuickMenu.QUICK_MENU_RETURN_IMPONDMENT)
                "LAW_Impoundments"
            else if (screen == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY
                || screen == Constant.QuickMenu.QUICK_MENU_VERIFY_PROPERTY ||
                screen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND || screen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND ||
                screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY ||
                screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND || screen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND

            )
                "COM_PropertyMaster"
            else if (screen == Constant.QuickMenu.QUICK_MENU_SHOW_TAX)
                "CRM_Shows"
            else if (screen == Constant.QuickMenu.QUICK_MENU_HOTEL_TAX)
                "CRM_Hotels"
            else if (screen == Constant.QuickMenu.QUICK_MENU_LICENSE_TAX)
                "CRM_Licenses"
            else ""

        var primaryKey = 0
        var mPrimaryKey = ""
    }

    override fun initComponents() {
        primaryKey = 0
        mPrimaryKey = ""
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP
                || fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP
                || fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP
            ) {
                mPrimaryKey = arguments?.getString(Constant.KEY_PRIMARY_KEY) ?: ""
            } else {
                primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
            }
        }
        //endregion
        setViews()
        bindData()
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
        return mBinding.root
    }

    private fun setViews() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_VERIFICATION_AGENT)
            mBinding.fabAdd.visibility = View.GONE
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
        mBinding.recyclerView.adapter = DocumentAdapter(this, fromScreen, mListener?.screenMode)
    }

    private fun bindData() {
        if (primaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getDocumentDetails(
                primaryKey.toString(),
                getTableName(fromScreen),
                object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        mListener?.dismissDialog()
                        comDocumentReferences = response as ArrayList<COMDocumentReference>
                        val adapter = (mBinding.recyclerView.adapter as DocumentAdapter)
                        adapter.clear()
                        adapter.update(response)
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        (mBinding.recyclerView.adapter as DocumentAdapter).clear()
                        if (message.isNotEmpty()) {
                        }
                        mListener?.showAlertDialog(message)
                    }
                })
        } else if (!TextUtils.isEmpty(mPrimaryKey)) {
            mListener?.showProgressDialog()
            APICall.getVehicleDocumentDetails(
                mPrimaryKey,
                getTableName(fromScreen),
                object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        mListener?.dismissDialog()
                        comDocumentReferences = response as ArrayList<COMDocumentReference>
                        val adapter = (mBinding.recyclerView.adapter as DocumentAdapter)
                        adapter.clear()
                        adapter.update(response)
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        (mBinding.recyclerView.adapter as DocumentAdapter).clear()
                        if (message.isNotEmpty()) {
                        }
                        mListener?.showAlertDialog(message)
                    }
                })
        }
        /*else {
            mListener?.showAlertDialog("In complete flow")
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS || fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER || fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT) {
                if (!ObjectHolder.registerBusiness.documents.isNullOrEmpty())
                    (mBinding.recyclerView.adapter as DocumentAdapter).update(ObjectHolder.registerBusiness.documents)
            }
        }*/
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val fragment = DocumentEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(
                    this@DocumentsMasterFragment,
                    Constant.REQUEST_CODE_DOCUMENT_ENTRY
                )

                mListener?.showToolbarBackButton(R.string.documents)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {

                    val fragment = DocumentEntryFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putParcelable(Constant.KEY_DOCUMENT, obj as COMDocumentReference?)
                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENT_ENTRY)

                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)
                }

                R.id.img_document -> {
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    comDocumentReferences.remove(comDocumentReference)
                    comDocumentReferences.add(0, comDocumentReference)
                    //intent.putExtra(Constant.KEY_DOCUMENT_URL, comDocumentReference.awsfile)
                    intent.putExtra(Constant.KEY_DOCUMENT, comDocumentReferences)
                    startActivity(intent)
                }

                R.id.txtDelete -> {
                    deleteDocument(obj as COMDocumentReference)
                }
                else -> {

                }
            }
        }
    }

    private fun deleteDocument(comDocumentReference: COMDocumentReference?) {
        if (null != comDocumentReference?.documentID) {
            mListener?.showProgressDialog()
            APICall.deleteDocument(
                comDocumentReference.documentReferenceID!!.toInt(),
                object : ConnectionCallBack<Boolean> {
                    override fun onSuccess(response: Boolean) {
                        mListener?.dismissDialog()
                        bindData()
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(message)
                    }
                })
        }
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_DOCUMENT_ENTRY) {
            (mBinding.recyclerView.adapter as DocumentAdapter).clear()
            bindData()
        }
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)

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
}