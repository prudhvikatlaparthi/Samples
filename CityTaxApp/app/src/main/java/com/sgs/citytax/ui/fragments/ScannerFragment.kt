package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentScannerBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.*
import com.sgs.citytax.ui.custom.DialogAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_QR_CODE_DATA
import com.sgs.citytax.util.Constant.REQUEST_CODE_QR_CODE
import java.math.BigDecimal

class ScannerFragment : BaseFragment(), SearchView.OnQueryTextListener, View.OnClickListener {
    private var prefHelper: PrefHelper = MyApplication.getPrefHelper()
    private lateinit var binding: FragmentScannerBinding
    private var mListener: Listener? = null
    private lateinit var mContext: Context
    private var fromScreen: Any? = null
    private var isValidParking: Boolean = true

    var pageIndex: Int = 1
    val pageSize: Int = 10

    private var mCOMComboStaticValues: ArrayList<ComComboStaticValues> = arrayListOf()
    private var mVehicleSycotaxList: ArrayList<UnusedVehicleSycotaxID> = arrayListOf()

    var mAdapter: DialogAdapter? = null
    lateinit var pagination: Pagination
    private val kPAGESIZE: Int = 100
    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None

    companion object {
        @JvmStatic
        fun newInstance(fromScreen: Any?, businessMode: Constant.BusinessMode) = ScannerFragment().apply {
            fromScreen?.let {
                this.fromScreen = fromScreen
            }
            this.businessMode = businessMode
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mListener = try {
            context as Listener
        } catch (e: Exception) {
            throw ClassCastException(context.toString() + "must implement Listener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scanner, container, false)
        initComponents()
        return binding.root
    }

    override fun initComponents() {
        setViewControls()
        setViewEvents()
        addScannerFragment()
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_RETURN_IMPONDMENT
                || fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAW_PENALTY_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY) {
            getImpoundmentsComboStatics()
            binding.searchView.visibility = View.INVISIBLE
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP
                || fromScreen == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE) {
            getUnusedParkingSycoTaxId()
            binding.searchView.visibility = View.INVISIBLE
        }
    }

    private fun getUnusedParkingSycoTaxId() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getRandomVehicleSycotaxIDList(object : ConnectionCallBack<VehicleSycotaxListResponse> {
            override fun onSuccess(response: VehicleSycotaxListResponse) {
                mListener?.dismissDialog()
                if(response.vehicleSycotaxList != null){
                mVehicleSycotaxList.addAll(response.vehicleSycotaxList!!)

                }
                displayScannerListSelection()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener!!.showAlertDialog(message)
            }
        })
    }

    private fun displayScannerListSelection() {
        mListener?.showAlertDialog(R.string.parking_vehicle_scan_list,
                R.string.yes, View.OnClickListener {
            val dialog = (it as Button).tag as androidx.appcompat.app.AlertDialog
            dialog.dismiss()
        }, R.string.no, View.OnClickListener {
            val dialog = (it as Button).tag as androidx.appcompat.app.AlertDialog
            dialog.dismiss()
            displaySycoTaxList(mVehicleSycotaxList)
        })
    }

    private fun displaySycoTaxList(response: List<UnusedVehicleSycotaxID>) {
        val vehicleSycotaxIDList: ArrayList<String> = arrayListOf()
        for (value in response) {
            vehicleSycotaxIDList.add(value.vehicleSycotaxID.toString())
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_select_sycotax_id)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, vehicleSycotaxIDList)
        builder.setAdapter(adapter) { dialog, which ->
            dialog.dismiss()
            response[which].vehicleSycotaxID?.let {
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP) {
                    setResultToBussiness(it)
                } else {
                    getVehicleOwnership(it, fromScreen)
                }
            }

        }
        val dialog = builder.create()
        dialog.show()

    }

    private fun setResultToBussiness(sycoTaxId: String) {
        if (Event.instance != null) {
            val intent = Intent()
            intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxId)
            Event.instance.hold(intent)
            activity?.setResult(Activity.RESULT_OK)
            mListener?.finish()
        }
    }


    private fun getImpoundmentsComboStatics() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT || fromScreen == Constant.QuickMenu.QUICK_MENU_LAW_PENALTY_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY) {
            APICall.getCorporateOfficeLOVValues("LAW_ImpoundmentsComboStaticsPayment", object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mListener?.dismissDialog()

                    var mComComboStaticValues = ComComboStaticValues()
                    mComComboStaticValues.comboCode = "-1"
                    mComComboStaticValues.comboValue = getString(R.string.select)
                    mComComboStaticValues.code = ""
                    mComComboStaticValues.desc = ""
                    mCOMComboStaticValues.add(0, mComComboStaticValues)

                    mCOMComboStaticValues.addAll(response.comboStaticValues as ArrayList<ComComboStaticValues>)

                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                }

            })
        } else {
            APICall.getCorporateOfficeLOVValues("LAW_ImpoundmentsComboStatics", object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mListener?.dismissDialog()

                    var mComComboStaticValues = ComComboStaticValues()
                    mComComboStaticValues.comboCode = "-1"
                    mComComboStaticValues.comboValue = getString(R.string.select)
                    mComComboStaticValues.code = ""
                    mComComboStaticValues.desc = ""
                    mCOMComboStaticValues.add(0, mComComboStaticValues)

                    for (i in response.comboStaticValues) {
                        if (i.isVisible == "Y")
                            mCOMComboStaticValues.add(i)

                    }
//                mCOMComboStaticValues.addAll(response.comboStaticValues  as ArrayList<ComComboStaticValues>)

                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                }

            })
        }
    }

    private fun setViewControls() {

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS || fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER)
            binding.searchView.queryHint = getString(R.string.hint_search_sycotax_id)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET)
            binding.searchView.queryHint = getString(R.string.hint_asset_sycotaxid_assetname)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING)
            binding.searchView.queryHint = getString(R.string.hint_asset_sycotaxid)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING
                || fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT) {
            binding.searchView.queryHint = getString(R.string.hint_booking_request_search)
            binding.searchView.inputType = InputType.TYPE_CLASS_NUMBER
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSIGN_ASSET || fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_RETURN)
            binding.searchView.queryHint = getString(R.string.hint_enter_asset_no)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_CART_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_WEAPON_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE
                || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE
                || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION
                || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_SUMMARY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX
                || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_SUMMARY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY
        )
            binding.searchView.queryHint = getString(R.string.hint_search_sycotax_id)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP) {
            binding.searchView.queryHint = getString(R.string.hint_search_sycotax_id_vehicle_ownership)
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP_SEARCH)
            binding.searchView.queryHint = getString(R.string.hint_search_sycotax_id_vehicle_registration)
        /* else if (fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY)
             binding.searchView.queryHint = getString(R.string.hint_notice_reference_no)*/
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_PENALTY_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_TRANSACTION_HISTORY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT
                || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT)
            binding.searchView.queryHint = getString(R.string.hint_search_sycotax_id_vehicle_parking)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_TICKET_HISTORY)
            binding.searchView.queryHint = getString(R.string.hint_notice_reference_no)
        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT) {
            binding.searchView.queryHint = getString(R.string.hint_violation_ticket_id)
            binding.searchView.inputType = InputType.TYPE_CLASS_NUMBER
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL) {
            binding.searchView.queryHint = getString(R.string.hint_license_number)
        }
//        else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN
//                || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT)
//            binding.searchView.queryHint = getString(R.string.hint_search_sycotax_id_vehicle_parking)
        else binding.searchView.queryHint = getString(R.string.hint_customer_search)

        binding.imgBtnSearch.visibility = if (prefHelper.isSearchEnabled) View.VISIBLE else View.GONE
        binding.searchView.visibility = if (prefHelper.isSearchEnabled && fromScreen != Constant.QuickMenu.QUICK_MENU_SALES_TAX)
            View.VISIBLE
        else View.GONE
    }


    private fun setViewEvents() {
        binding.searchView.setOnQueryTextListener(this)
        binding.imgBtnSearch.setOnClickListener(this)
    }

    private fun addScannerFragment() {
        val fragment = QRScannerFragment.newInstance()
        fragment.setTargetFragment(this, REQUEST_CODE_QR_CODE)
        mListener?.addFragmentWithFrameLayoutID(fragment, false, R.id.frame_scanner)
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun navigateToViolationTicketEntry(detail: ViolationDetail) {
        val intent = Intent(context, VehicleTicketIssueActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_VIOLATION_DETAIL, detail)
        startActivity(intent)
        activity?.finish()
    }

    private fun fetchViolationTicketDetails(ticketID: String) {
        try {
            val getViolationTicketsByViolationTicketID = GetViolationTicketsByViolationTicketID()
            getViolationTicketsByViolationTicketID.violationTicketID = ticketID.toInt()
            mListener?.showProgressDialog(R.string.msg_please_wait)
            APICall.getViolationTicketsByViolationTicketID(getViolationTicketsByViolationTicketID, object : ConnectionCallBack<ViolationDetail> {
                override fun onSuccess(response: ViolationDetail) {
                    mListener?.dismissDialog()
                    navigateToViolationTicketEntry(response)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
//                    mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { _, _ ->
                        activity?.finish()
                    })
                }
            })
        } catch (e: Exception) {
            mListener?.showAlertDialog(getString(R.string.hint_invalid_violation_ticket_id), DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
                activity?.finish()
            })
        }
    }

    private fun isPropertySycoTaxIDAvailable(sycoTaxID: String) {
        val isPropertySycoTaxAvailable = IsPropertySycoTaxAvailable()
        isPropertySycoTaxAvailable.sycoTaxID = sycoTaxID
        mListener?.showProgressDialog()
        APICall.isPropertySycoTaxAvailable(isPropertySycoTaxAvailable, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                searchPropertyDetailsBySycoTax(sycoTaxID)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun searchPropertyDetailsBySycoTax(sycoTaxID: String) {
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

    private fun navigateToPropertyTax(sycoTaxID: String, propertyDetailsBySycoTax: PropertyDetailsBySycoTax) {
        val intent = Intent(context, PropertyTaxActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_PROPERTY_DETAILS, propertyDetailsBySycoTax)
        if (propertyDetailsBySycoTax.address.size > 0) {
            intent.putParcelableArrayListExtra(Constant.KEY_ADDRESS, propertyDetailsBySycoTax.address)
        }
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToLandTax(sycoTaxID: String, propertyDetailsBySycoTax: PropertyDetailsBySycoTax) {
        val intent = Intent(context, LandTaxActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_PROPERTY_DETAILS, propertyDetailsBySycoTax)
        if (propertyDetailsBySycoTax.address.size > 0) {
            intent.putParcelableArrayListExtra(Constant.KEY_ADDRESS, propertyDetailsBySycoTax.address)
        }
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun search(@Suppress("UNUSED_PARAMETER") view: View?) {
        val query: String = binding.searchView.query.toString().trim()
        if (!TextUtils.isEmpty(query)) {
            if (query.length < 3
                    && (fromScreen != Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT
                            && fromScreen != Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING
                            && fromScreen != Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT)
            ) {
                mListener?.showAlertDialog(getString(R.string.msg_min_data))
                return
            }

            if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
                fetchTaxPayerDetails(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING)
                fetchAvailableAssetSycotaxIdList(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING)
                fetchBookings(query, true)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT)
                fetchBookings(query, false)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET)
                fetchUpdateAssetList(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT)
                fetchViolationTicketDetails(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSIGN_ASSET)
                getAssetDetailsBySearchForAssignmentAndReturn(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_RETURN)
                getAssetDetailsBySearchForAssignmentAndReturn(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_CART_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX)
                isCartSycoTaxAvailable(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX)
                isGamingSycoTaxAvailable(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_WEAPON_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
                checkIsWeaponSycotaxAvailable(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION)
//                  isNoticeGen4IndividualTax(query, fromScreen)
                showIndividualTaxesList(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_SUMMARY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF)
                showIndividualTaxesList(query)
//            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY
//                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF
//                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF)
//                getOrSearchIndividualTaxDetails(query, fromScreen)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP) {
                getVehicleOwnershipBySycoTaxId(query)
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT)
                getVehicleOwnership(query, fromScreen)
            /* else if (fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY)
                 fetchTrackOnTaxNoticeHistory(query)*/
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP_SEARCH)
                getUnassignedVehiclesDetailsSearch(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_TICKET_HISTORY)
                fetchParkingTicketHistory(query)
//            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE)
//                isNoticeGen4IndividualTax(query, fromScreen)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT)
                getVehicleDetailsBySearch(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY)
                searchVehicleSummary(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE)
                showIndividualTaxesList(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND)
                isPropertySycoTaxIDAvailable(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_SUMMARY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY)
                showPropertyTaxesList(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_TRANSACTION_HISTORY)
                getVehicleOwnership(query, fromScreen)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL)
                scanSearchPendingLicenses(query)
            else if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER)
                isCitizenSycoTaxAvailable(query)
            //else searchTaxPayerDetails(query)
            else searchTaxPayerList(query)
        }
    }

    private fun getUnassignedVehiclesDetailsSearch(query: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getUnassignedVehiclesDetailsAPI(query, object : ConnectionCallBack<VehicleDetailsSearchOwnerResponse> {
            override fun onSuccess(response: VehicleDetailsSearchOwnerResponse) {
                if (response.vehicleDetails != null) {
                    if (response.vehicleDetails.isNotEmpty() || response.vehicleDetails.size > 0 || response.vehicleDetails != null) {

                        /*  var vehicleDetails: ArrayList<VUADMVehicleOwnership> = arrayListOf()
                          vehicleDetails = response.vehicleDetails as ArrayList<VUADMVehicleOwnership>
                          val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, vehicleDetails)
                          showListAlertDialog("vehicle", adapter)*/

                        val list = response?.vehicleDetails
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(R.string.title_select_vehicle)
                        val adapter = list?.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                        builder.setAdapter(adapter) { dialog, which ->
                            dialog.dismiss()
                            //navigateToVehicleSummary(sycoTaxID, response.results?.vehicleDetails?.get(which))
                            vehicleOwnershipEntryFragment(response.vehicleDetails[which], response.vehicleDetails[which].vehicleSycotaxID)

                        }
                        val dialog = builder.create()
                        dialog.show()

                        //  vehicleOwnershipEntryFragment(vuadmVehicleOwnership, vuadmVehicleOwnership.vehicleSycotaxID)
                    } else {
                        mListener?.showAlertDialogFailure("", R.string.no_records_found, DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            mListener?.finish()
                        })
                    }
                } else {
                    mListener?.showAlertDialogFailure("", R.string.no_records_found, DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        mListener?.finish()
                    })
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    mListener?.finish()
                })
            }
        })
    }


    private fun vehicleOwnershipEntryFragment(vuAdmVehicleOwnership: VUADMVehicleOwnership?, sycoTaxID: String?) {
        if (Event.instance != null) {
            val intent = Intent()
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
            intent.putExtra(Constant.KEY_VEHICLE_OWNERSHIP, vuAdmVehicleOwnership)
            Event.instance.hold(intent)
            activity?.setResult(Activity.RESULT_OK)
            mListener?.finish()
        }
    }

    /*  private fun showIndividualTaxesList(query: String) {
          mListener?.showProgressDialog(R.string.msg_please_wait)
          APICall.getTableOrViewData(getIndividualTaxesList(query), object : ConnectionCallBack<GenericServiceResponse> {
              override fun onSuccess(response: GenericServiceResponse) {
                  mListener?.dismissDialog()
                  if (response.result != null && response.result.individualTax.size > 0) {
                      val list = response.result.individualTax
                      val adapter = list.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                      showListAlertDialog(getString(R.string.title_select_sycotax_id), adapter)
                  } else {
                      mListener?.showAlertDialog(getString(R.string.no_records_found))
                  }
              }

              override fun onFailure(message: String) {
                  mListener?.dismissDialog()
                  mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
              }
          })
      }
    */
    //New Changes for Recycler view listing- 22-02-2021
    private fun showIndividualTaxesList(query: String) {
        hideKeyboard()
        showIndividualCustomListDialog(getString(R.string.title_select_sycotax_id), query)
    }

    private fun showIndividualCustomListDialog(mTitle: String, query: String) {
        val dialog = requireContext().prepareCustomListDialog(R.layout.alert_dialog_list, true)
        val lView = dialog.findViewById<RecyclerView>(R.id.listView)
        lView.setHasFixedSize(true)
        lView.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))
        dialog.findViewById<TextView>(R.id.listTextView).text = mTitle
        mAdapter = setAdapter(dialog)
        lView.adapter = mAdapter
        pagination = Pagination(1, kPAGESIZE, lView) { pageNumber, PageSize ->
            callIndividualApi(query, pageNumber, PageSize, dialog)
        }
        pagination.setDefaultValues()
    }

    private fun callIndividualApi(query: String, pageNumber: Int, pageSize: Int, alertDialog: Dialog) {
        if (pageNumber == 1) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
        } else {
            alertDialog.showhidePaginationProgress(true)
        }
        APICall.getTableOrViewData(getIndividualTaxesList(query, pageNumber, pageSize), object : ConnectionCallBack<GenericServiceResponse> {
            override fun onSuccess(response: GenericServiceResponse) {
                mListener?.dismissDialog()
                if (response.result?.individualTax?.size ?: 0 > 0) {
                    val list = response.result!!.individualTax!!
                    if (pageNumber == 1) {
                        pagination.totalRecords = response.totalSearchedRecords
                        alertDialog.show()
                    }
                    setTaxIdListData(list)
                } else {
                    if (pageNumber == 1) {
                        mListener?.showAlertDialog(getString(R.string.no_records_found))
                        activity?.finish()
                    }
                }
                alertDialog.showhidePaginationProgress(false)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageNumber == 1) {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener {dialog, which ->
                        dialog.dismiss()
                        activity?.finish()
                    })
                }
                alertDialog.showhidePaginationProgress(false)
            }
        })
    }


    private fun setTaxIdListData(list: List<VuCrmIndividualTaxes>) {
        pagination.setIsScrolled(false)
        if (list.isNotEmpty()) {
            pagination.stopPagination(list.size)
        } else {
            pagination.stopPagination(0)
        }
        mAdapter?.updateList(list)
    }

    private fun setAdapter(alertDialog: Dialog): DialogAdapter {
        val adapter = DialogAdapter()
        adapter.addItemClickListener { item, which ->
            alertDialog.dismiss()
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX) {
                (adapter.resultList.get(which) as VuCrmIndividualTaxes).SycotaxID.let { getCartTax(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
                (adapter.resultList.get(which) as VuCrmIndividualTaxes).SycotaxID.let { getWeaponTax(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE) {
                (adapter.resultList.get(which) as VuCrmIndividualTaxes).SycotaxID.let { getGamingTax(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE) {
                (adapter.resultList.get(which) as VuCrmIndividualTaxes).SycotaxID.let { isNoticeGen4IndividualTaxSelected(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION) {
                (adapter.resultList.get(which) as VuCrmIndividualTaxes).SycotaxID.let { isNoticeGen4IndividualTaxSelected(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_SUMMARY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF) {
                (adapter.resultList.get(which) as VuCrmIndividualTaxes).SycotaxID.let { getOrSearchIndividualTaxDetails(it, fromScreen) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND) {
                (adapter.resultList.get(which) as VuComProperties?)?.propertySycotaxID?.let { searchPropertyDetailsBySycoTax(it) }

            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE) {
                (adapter.resultList[which] as VuComProperties?)?.propertySycotaxID?.let {
                    navigateToPropertyTaxDetailsScreen(it, (adapter.resultList[which] as VuComProperties?)?.propertyID, fromScreen as Constant.QuickMenu)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_SUMMARY || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY) {
                (adapter.resultList[which] as VuComProperties?)?.let {
                    NavigateToPropertyLandSummaryActivity(it)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY) {
                (adapter.resultList[which] as VuComProperties?)?.propertySycotaxID?.let {
                    navigateToPropertyTaxNoticeHistory((adapter.resultList[which] as VuComProperties?), it)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF) {
                (adapter.resultList[which] as VuComProperties?)?.propertySycotaxID?.let {
                    navigateToPropertyPenalityWaiveOffScreen((adapter.resultList[which] as VuComProperties?))
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF) {
                (adapter.resultList[which] as VuComProperties?)?.propertySycotaxID?.let {
                    navigateToOutstandingWaiveOffScreen((adapter.resultList[which] as VuComProperties?))
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY) {
                (adapter.resultList[which] as VuComProperties?)?.propertySycotaxID?.let {
                    navigateToPropertyTransactionHistory((adapter.resultList[which] as VuComProperties?), it)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL) {
                (adapter.resultList[which] as PendingLicenses4Agent?)?.licenseNumber?.let {
                    navigateToTaxDetailsActivity((adapter.resultList[which] as PendingLicenses4Agent?))
                }
            }
        }

        return adapter
    }


    /*private fun showPropertyTaxesList(query: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getTableOrViewData(getPropertyTaxesList(query), object : ConnectionCallBack<GenericServiceResponse> {
            override fun onSuccess(response: GenericServiceResponse) {
                mListener?.dismissDialog()
                if (response.result.property.size > 0) {
                    val list = response.result.property
                    if (list.size == 1 && (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY)) {
                        navigateToPropertyTaxNoticeHistory(list.get(0), list.get(0).propertySycotaxID)
                    } else if (list.size == 1 && (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF
                                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF)) {
                        navigateToPropertyPenalityWaiveOffScreen(list.get(0))
                    } else if (list.size == 1 && fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF) {
                        navigateToOutstandingWaiveOffScreen(list.get(0))
                    } else {
                        val adapter = list.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                        showListAlertDialog(getString(R.string.title_select_sycotax_id), adapter)
                    }
                } else {
                    mListener?.showAlertDialog(getString(R.string.no_records_found))
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                *//*  mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                  mListener?.finish()*//*

                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }*/

    private fun showPropertyTaxesList(query: String) {
        hideKeyboard()
        showPropertyTaxesCustomListDialog(getString(R.string.title_select_sycotax_id), query)
    }

    private fun showPropertyTaxesCustomListDialog(mTitle: String, query: String) {
        val dialog = requireContext().prepareCustomListDialog(R.layout.alert_dialog_list, true)
        val lView = dialog.findViewById<RecyclerView>(R.id.listView)
        lView.setHasFixedSize(true)
        lView.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))
        dialog.findViewById<TextView>(R.id.listTextView).text = mTitle
        mAdapter = setAdapter(dialog)
        lView.adapter = mAdapter
        pagination = Pagination(1, kPAGESIZE, lView) { pageNumber, PageSize ->
            callPropertyTaxesApi(query, pageNumber, PageSize, dialog)
        }
        pagination.setDefaultValues()
    }

    private fun callPropertyTaxesApi(query: String, pageNumber: Int, pageSize: Int, alertDialog: Dialog) {
        if (pageNumber == 1) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
        } else {
            alertDialog.showhidePaginationProgress(true)
        }
        APICall.getTableOrViewData(getPropertyTaxesList(query, pageNumber, pageSize), object : ConnectionCallBack<GenericServiceResponse> {
            override fun onSuccess(response: GenericServiceResponse) {
                mListener?.dismissDialog()

                if (response.result?.property?.size ?: 0 > 0) {
                    val list = response.result!!.property!!
                    if (pageNumber == 1) {
                        pagination.totalRecords = response.totalSearchedRecords
                    }

                    if (list.size == 1 && (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY)) {
                        navigateToPropertyTaxNoticeHistory(list.get(0), list.get(0).propertySycotaxID)
                    } else if (list.size == 1 && (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF
                                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF)) {
                        navigateToPropertyPenalityWaiveOffScreen(list.get(0))
                    } else if (list.size == 1 && fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF) {
                        navigateToOutstandingWaiveOffScreen(list.get(0))
                    } else {
                        pagination.stopPagination(list.size)
                        mAdapter?.updateList(list)
                        pagination.setIsScrolled(false)
                        if (!alertDialog.isShowing) {
                            alertDialog.show()
                        }
                    }

                } else {
                    pagination.stopPagination(0)
                    if (pageNumber == 1) {
                        mListener?.showAlertDialog(getString(R.string.no_records_found), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            activity?.finish()
                        })
                    }
                }
                alertDialog.showhidePaginationProgress(false)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageNumber == 1) {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        activity?.finish()
                    })
                }
                alertDialog.showhidePaginationProgress(false)
            }
        })
    }

    private fun getPropertyTaxesList(query: String, pageNumber: Int, pageSize: Int): AdvanceSearchFilter {
        val searchFilter = AdvanceSearchFilter()
        //region Apply Filters
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "PropertySycotaxID"
        filterColumn.columnValue = query
        filterColumn.srchType = "like"

        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList
        //endregion

        //region Apply Table/View details
        val tableDetails = TableDetails()
        tableDetails.primaryKeyColumnName = "PropertyID"
        tableDetails.selectColoumns = "PropertyID,PropertyName,PropertySycotaxID,TaxRuleBookCode,ProductCode"
        tableDetails.TableCondition = "AND"
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY
                || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF
                || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY) {
            // if the property type is land
            tableDetails.initialTableCondition = "TaxRuleBookCode = 'LAND_PROP'"
            tableDetails.tableOrViewName = "VU_COM_LANDS"
        } else {
            tableDetails.tableOrViewName = "VU_COM_PROPERTIES"
        }

        searchFilter.tableDetails = tableDetails
        //endregion

        searchFilter.pageIndex = pageNumber
        searchFilter.pageSize = pageSize

        return searchFilter
    }

    private fun searchVehicleSummary(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val searchVehicleDetails = SearchVehicleDetails()
        searchVehicleDetails.filterString = sycoTaxID
        searchVehicleDetails.pageIndex = pageIndex
        searchVehicleDetails.pageSize = pageSize

        APICall.searchVehicleSummary(searchVehicleDetails, object : ConnectionCallBack<SearchVehicleResultResponse> {
            override fun onSuccess(response: SearchVehicleResultResponse) {
                mListener?.dismissDialog()

                if (response.results != null) {
                    val list = response.results?.vehicleDetails
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(R.string.title_select_vehicle)
                    val adapter = list?.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                    builder.setAdapter(adapter) { dialog, which ->
                        dialog.dismiss()
                        navigateToVehicleSummary(sycoTaxID, response.results?.vehicleDetails?.get(which))
                    }
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                })
            }

        })

    }

    private fun navigateToVehicleSummary(searchID: String, vehicleMaster: VehicleMaster?) {

        val intent = Intent(context, BusinessSummaryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, searchID)
        intent.putExtra(Constant.KEY_VEHICLE_DETAILS, vehicleMaster)
        startActivity(intent)
        activity?.finish()

    }

    /*private fun fetchTrackOnTaxNoticeHistory(referenceNo: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val getTaxInvoicesDetailsByNoticeReferenceNo = GetTaxInvoicesDetailsByNoticeReferenceNo()
        getTaxInvoicesDetailsByNoticeReferenceNo.noticeReferenceNo = referenceNo
        APICall.getTaxInvoicesDetailsByNoticeReferenceNo(getTaxInvoicesDetailsByNoticeReferenceNo, object : ConnectionCallBack<List<TaxNoticeDetail>> {
            override fun onSuccess(response: List<TaxNoticeDetail>) {
                mListener?.dismissDialog()
                navigateToTrackOnTaxNoticeHistory(response as ArrayList<TaxNoticeDetail>)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }

        })
    }*/

    private fun fetchParkingTicketHistory(filter: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val getTicketsForCancellation = GetTicketsForCancellation()
        getTicketsForCancellation.filter = filter
        APICall.getTicketsForCancellation(getTicketsForCancellation, object : ConnectionCallBack<ParkingTicket> {
            override fun onSuccess(response: ParkingTicket) {
                mListener?.dismissDialog()
                navigateToParkingTicketHistory(response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }

        })
    }

    private fun navigateToParkingTicketHistory(mParkingTicket: ParkingTicket) {
        val intent = Intent(context, ParkingTicketHistoryActivity::class.java)
        intent.putExtra(Constant.KEY_PARKING_TICKET_NOTICE_HISTORY, mParkingTicket)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToTrackOnTaxNoticeHistory(mTaxNoticeDetails: ArrayList<TaxNoticeDetail>) {
        val intent = Intent(context, TrackOnTaxNoticeHistoryActivity::class.java)
        intent.putParcelableArrayListExtra(Constant.KEY_TRACK_ON_TAX_NOTICE_HISTORY, mTaxNoticeDetails)
        startActivity(intent)
        activity?.finish()
    }

    private fun getVehicleOwnership(query: String, fromScreen: Any?) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val searchVehicleDetails = SearchVehicleDetails()
        searchVehicleDetails.filterString = query
        searchVehicleDetails.pageIndex = pageIndex
        searchVehicleDetails.pageSize = pageSize

        APICall.searchVehicleDetails(searchVehicleDetails, object : ConnectionCallBack<SearchVehicleResultResponse> {
            override fun onSuccess(response: SearchVehicleResultResponse) {
                mListener?.dismissDialog()

                if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE) {

                    if (response.results != null) {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(getString(R.string.title_select_vehicle))
                        val adapter = response.results?.vehicleDetails?.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                        builder.setAdapter(adapter) { dialog, which ->
                            dialog.dismiss()
                            navigateToVehicleDetails(response.results?.vehicleDetails?.get(which)?.vehicleSycotaxID.toString(), response.results?.vehicleDetails?.get(which))
                        }
                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        navigateToVehicleDetails(query, null)
                    }
                } else {

                    if (response.results != null) {
                        val list = response.results?.vehicleDetails
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(R.string.title_select_vehicle)
                        val adapter = list?.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                        builder.setAdapter(adapter) { dialog, which ->
                            dialog.dismiss()
                            navigateToParkingTicketPaymentCollect(query, response.results?.vehicleDetails?.get(which))
                        }
                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                        })
                    }

                }

                /* if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT) {
                     if (response.results != null) {
                         val list = response.results?.vehicleDetails
                         val builder = AlertDialog.Builder(context)
                         builder.setTitle(R.string.title_select_business)
                         val adapter = list?.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                         builder.setAdapter(adapter) { dialog, which ->
                             dialog.dismiss()
                             navigateToParkingTicketPaymentCollect(sycoTaxID, response.results?.vehicleDetails?.get(which))
                         }
                         val dialog = builder.create()
                         dialog.show()
                     } else {
                         mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                             dialogInterface.dismiss()
                             activity?.finish()
                         })
                     }
                 } else {
                     if (response.results != null) {
                         val list = response.results?.vehicleDetails
                         val builder = AlertDialog.Builder(context)
                         builder.setTitle(R.string.title_select_vehicle)
                         val adapter = list?.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                         builder.setAdapter(adapter) { dialog, which ->
                             dialog.dismiss()
                             navigateToVehicleDetails(sycoTaxID, response.results?.vehicleDetails?.get(which))
                         }
                         val dialog = builder.create()
                         dialog.show()
                     } else {
                         navigateToVehicleDetails(sycoTaxID, null)
                     }
                 }*/
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                })
            }

        })
    }

    private fun navigateToParkingTicketPaymentCollect(searchID: String, vehicleMaster: VehicleMaster?) {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT) {
            val intent = Intent(context, ParkingTicketPaymentActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            intent.putExtra(Constant.KEY_SYCO_TAX_ID, searchID)
            intent.putExtra(Constant.KEY_VEHICLE_NO, vehicleMaster!!.vehicleNo)
            intent.putExtra(Constant.KEY_PARKING_PLACE_ID, MyApplication.getPrefHelper().parkingPlaceID)
            startActivity(intent)
            activity?.finish()
        }
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_PENALTY_WAIVE_OFF) {
            CallParkingPenaltyApi(vehicleMaster?.vehicleNo)
        }
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_TRANSACTION_HISTORY) {
            val intent = Intent(context, ParkingTransactionHistoryActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            intent.putExtra(Constant.KEY_VEHICLE_NO, vehicleMaster?.vehicleNo)
            intent.putExtra(Constant.KEY_PARKING_PLACE_ID, MyApplication.getPrefHelper().parkingPlaceID)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun CallParkingPenaltyApi(vehicleNo: String?) {
        val getParkingPenaltyTransactionsList = GetParkingPenaltyTransactionsList()
        getParkingPenaltyTransactionsList.vehno = vehicleNo // "TS03EX0239"
        getParkingPenaltyTransactionsList.parkingplcid = MyApplication.getPrefHelper().parkingPlaceID //3

        mListener?.showProgressDialog(R.string.msg_please_wait)

        APICall.getParkingPenaltyTransactionsList(getParkingPenaltyTransactionsList, object : ConnectionCallBack<GetParkingPenaltyTransactionsResponse> {
            override fun onSuccess(response: GetParkingPenaltyTransactionsResponse) {
                mListener?.dismissDialog()

                if (response.penalties != null && response.penalties.isNotEmpty()) {
                    navigateToParkingPenaltyScreen(response.penalties)
                } else {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        mListener?.finish()
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    mListener?.finish()
                })

            }
        })

    }

    private fun navigateToVehicleDetails(query: String, vehicleMaster: VehicleMaster?) {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN
                || fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE) {
            if (vehicleMaster == null) {
                mListener?.showAlertDialog(R.string.parking_vehicle_not_register,
                        R.string.yes, View.OnClickListener {
                    val dialog = (it as Button).tag as androidx.appcompat.app.AlertDialog
                    dialog.dismiss()
                    val intent = Intent(context, ScanActivity::class.java)
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE) {
                        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE)
                    } else
                        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP)
                    startActivity(intent)

                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE)
                        activity?.finish()
                }, R.string.no, View.OnClickListener {
                    val dialog = (it as Button).tag as androidx.appcompat.app.AlertDialog
                    dialog.dismiss()
                    activity?.finish()
                })
            } else {
                vehicleMaster.vehicleNo?.let {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE) {
                        mListener?.showAlertDialog(getString(R.string.msg_sycotax_id_already_registered), DialogInterface.OnClickListener { dialogInterface, _ ->
                            activity?.finish()
                        })
                    } else {
                        getParkingTicketDetails(it, vehicleMaster)
                    }
                }
            }
        } else {
            startOnBoardingVehicleOwnership(query, vehicleMaster)
        }
    }

    private fun startOnBoardingVehicleOwnership(query: String, vehicleMaster: VehicleMaster?) {
        val intent = Intent(context, OnBoardingVehicleOwnershipActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, query)
        if (vehicleMaster != null) {
            intent.putExtra(Constant.KEY_EDIT, true)
            intent.putExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS, vehicleMaster)
        } else {
            intent.putExtra(Constant.KEY_EDIT, false)
        }
        startActivity(intent)
        activity?.finish()
    }

    private fun checkIsWeaponSycotaxAvailable(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)

        APICall.checkWeaponSycotaxAvailable(sycoTaxID, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()

                if (response != 0) {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_WEAPON_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX) {
                        mListener?.showAlertDialog(getString(R.string.msg_sycotax_id_already_registered), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity?.finish()
                        })
                    } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
                        getWeaponTax(sycoTaxID)
                    }
                } else
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX)
                        navigateToWeaponTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX)
                    else if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
                        navigateToWeaponTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
                    else
                        navigateToWeaponTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }

        })
    }

    private fun getWeaponTax(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val getIndividualTax = GetIndividualTax()
        getIndividualTax.columnName = "WeaponSycotaxID"
        getIndividualTax.sycoTaxID = sycoTaxID
        getIndividualTax.tableName = "VU_CRM_Weapons"
        APICall.getIndividualTaxForWeapon(getIndividualTax, object : ConnectionCallBack<Weapon> {
            override fun onSuccess(response: Weapon) {
                mListener?.dismissDialog()
                navigateToWeaponTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX, response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun navigateToWeaponTax(sycoTaxID: String, code: Constant.QuickMenu, weaponTax: Weapon? = null) {
        val intent = Intent(context, WeaponRegisterActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, code)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        weaponTax?.let {
            intent.putExtra(Constant.KEY_WEAPON_TAX, it)
        }
        startActivity(intent)
        activity?.finish()
    }


    private fun fetchAvailableAssetSycotaxIdList(id: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.searchAssetDetailsList(id, object : ConnectionCallBack<AssetSycoTaxIdBySearch> {
            override fun onSuccess(response: AssetSycoTaxIdBySearch) {
                mListener?.dismissDialog()
                if (response.results != null) {
                    response.results?.sycotaxList?.let {
                        if (response.results != null)
                            displayAssetSycotaxList(it)
                    }
                } else
                    mListener?.showAlertDialogFailure("", R.string.msg_no_data, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }


    private fun fetchAssetList(id: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.searchAssetList(id, object : ConnectionCallBack<AssetDetailsBySearch> {
            override fun onSuccess(response: AssetDetailsBySearch) {
                mListener?.dismissDialog()
                response.results?.assetList?.let {
                    displayAssetList(it)
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

    private fun fetchUpdateAssetList(id: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.searchUpdateAssetList(id, object : ConnectionCallBack<AssetDetailsBySearch> {
            override fun onSuccess(response: AssetDetailsBySearch) {
                mListener?.dismissDialog()
                response.results?.assetList?.let {
                    displayAssetList(it)
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

    private fun fetchAssetDetails(id: String) {

        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.searchAssetDetails(id, object : ConnectionCallBack<AssetDetailsBySycotax> {
            override fun onSuccess(response: AssetDetailsBySycotax) {
                mListener?.dismissDialog()
                Log.e("response", "" + response.assetDetails)

                if (response.isSycotaxAvailable == true && response.assetDetails == null)
                    navigateAssetOnBoarding(id)
                else if (response.isSycotaxAvailable == false && response.assetDetails == null)
                    mListener?.showAlertDialog(getString(R.string.invalid_syco_tax_id), DialogInterface.OnClickListener { _, _ ->
                        activity?.finish()
                    })
                else
                    mListener?.showAlertDialog(getString(R.string.msg_asset_already_registered), DialogInterface.OnClickListener { _, _ ->
                        activity?.finish()
                    })
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })

    }


    private fun displayAssetSycotaxList(response: List<AssetSycoTaxId>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_select_asset)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response)
        builder.setAdapter(adapter) { dialog, which ->
            dialog.dismiss()
            navigateAssetOnBoarding(response[which].assetSycotaxID.toString())
            activity?.finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun displayAssetList(response: List<AssetDetails>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_select_asset)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response)
        builder.setAdapter(adapter) { dialog, which ->
            dialog.dismiss()
            fetchAsset(response[which].assetID.toString())
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun navigateAssetOnBoarding(sycoTaxID: String) {
        val intent = Intent(context, AssetOnBoardingActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        startActivity(intent)
        activity?.finish()
    }


    private fun fetchBookings(id: String, flag: Boolean) {

        try {
            val result = id.toInt()
            if (result > 0) {
                fetchBookingsInfo(id, flag)
            } else {
                mListener?.showAlertDialog(getString(R.string.msg_invalid_asset_id), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }
        } catch (nfe: NumberFormatException) {
            mListener?.showAlertDialog(getString(R.string.msg_invalid_asset_id), DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
                activity?.finish()
            })
        }

    }


    private fun fetchBookingsInfo(id: String, flag: Boolean) {
        val getBookingsList = GetBookingsList()
        getBookingsList.bookingRequestID = id.toInt()
        getBookingsList.bookingRequestLineId = 0
        getBookingsList.isAssetBookingUpdate = flag
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getBookings(getBookingsList, object : ConnectionCallBack<List<AssetBooking>> {
            override fun onSuccess(response: List<AssetBooking>) {
                mListener?.dismissDialog()
                if (response.size > 1) {
                    displayBookingsList(response)
                } else {
                    navigateToAssetBooking(response[0])
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

    private fun isCartSycoTaxAvailable(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val cartSycoTax = CartSycoTax()
        cartSycoTax.sycoTaxID = sycoTaxID
        APICall.isCartSycoTaxAvailable(cartSycoTax, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                if (response != 0) {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_CART_TAX) {
                        mListener?.showAlertDialog(getString(R.string.msg_sycotax_id_already_registered), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity?.finish()
                        })
                    } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX) {
                        getCartTax(sycoTaxID)
                    }

                } else {
                    // navigateToCartTax(sycoTaxID, fromScreen as Constant.QuickMenu)
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX)
                        navigateToCartTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX)
                    else if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX)
                        navigateToCartTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX)
                    else
                        navigateToCartTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_CREATE_CART_TAX)
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

    private fun getCartTax(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val getIndividualTax = GetIndividualTax()
        getIndividualTax.columnName = "CartSycotaxID"
        getIndividualTax.sycoTaxID = sycoTaxID
        getIndividualTax.tableName = "VU_CRM_Carts"
        APICall.getIndividualTaxForCart(getIndividualTax, object : ConnectionCallBack<CartTax> {
            override fun onSuccess(response: CartTax) {
                mListener?.dismissDialog()
                navigateToCartTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX, response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun navigateToCartTax(sycoTaxID: String, code: Constant.QuickMenu, cartTax: CartTax? = null) {
        val intent = Intent(context, CartTaxActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, code)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        cartTax?.let {
            intent.putExtra(Constant.KEY_CART_TAX, it)
        }
        startActivity(intent)
        activity?.finish()
    }

    private fun isGamingSycoTaxAvailable(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val cartSycoTax = CartSycoTax()
        cartSycoTax.sycoTaxID = sycoTaxID
        APICall.isGamingSycotaxAvailable(cartSycoTax, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                if (response != 0) {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX) {
                        mListener?.showAlertDialog(getString(R.string.msg_sycotax_id_already_registered), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity?.finish()
                        })
                    } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE) {
                        getGamingTax(sycoTaxID)
                    }

                } else
                    navigateToGamingTax(sycoTaxID, fromScreen as Constant.QuickMenu)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun getGamingTax(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val getIndividualTax = GetIndividualTax()
        getIndividualTax.columnName = "GamingMachineSycotaxID"
        getIndividualTax.sycoTaxID = sycoTaxID
        getIndividualTax.tableName = "VU_CRM_GamingMachines"
        APICall.getIndividualGamingTax(getIndividualTax, object : ConnectionCallBack<GamingMachineTax> {
            override fun onSuccess(response: GamingMachineTax) {
                mListener?.dismissDialog()
                navigateToGamingTax(sycoTaxID, Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE, response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToGamingTax(sycoTaxID: String, fromScreen: Constant.QuickMenu, gamingMachineTax: GamingMachineTax? = null) {
        val intent = Intent(context, GamingRegisterActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        gamingMachineTax.let {
            intent.putExtra(Constant.KEY_GAMING_MACHINE, it)
        }
        startActivity(intent)
        activity?.finish()
    }

    private fun isNoticeGen4IndividualTax(sycoTaxID: String, fromScreen: Any?) {
        //TODO NEW FUNCTION
//        showListAlertDialog(getString(R.string.title_select_sycotax_id), fromScreen)
        isNoticeGen4IndividualTaxSelected(sycoTaxID)
    }

    private fun isNoticeGen4IndividualTaxSelected(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val cartSycoTax = CartSycoTax()
        cartSycoTax.sycoTaxID = sycoTaxID
        APICall.isNoticeGen4IndividualTax(cartSycoTax, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                fromScreen?.let {
                    navigateToIndividualTax(sycoTaxID, it)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun getOrSearchIndividualTaxDetails(sycoTaxID: String, fromScreen: Any?) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val cartSycoTax = CartSycoTax()
        cartSycoTax.sycoTaxID = sycoTaxID
        APICall.getOrSearchIndividualTaxDetails(cartSycoTax, object : ConnectionCallBack<GetSearchIndividualTaxDetails> {
            override fun onSuccess(response: GetSearchIndividualTaxDetails) {
                mListener?.dismissDialog()
                fromScreen?.let {
                    when (fromScreen) {
                        Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_SUMMARY -> navigateToIndividualTaxSummary(response)
                        Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY -> navigateToIndividualTaxNoticeHistory(response, response.sycotaxID.toString(), it)
                        Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF -> navigateToOutStandings(response, it)
                        Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF -> navigateToPenalties(response, it)
                        Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY -> navigateToIndividualBusinessTransactionHistory(response, response.sycotaxID.toString(), it)
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun navigateToIndividualTaxSummary(getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails) {
        val intent = Intent(context, IndividualTaxSummaryActivity::class.java)
        when (getSearchIndividualTaxDetails.taxRuleBookCode) {
            Constant.TaxRuleBook.CART.Code -> intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX)
            Constant.TaxRuleBook.WEAPON.Code -> intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX)
            Constant.TaxRuleBook.GAME.Code -> intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE)
        }
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, getSearchIndividualTaxDetails.sycotaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToOutStandings(getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails, fromScreen: Any) {
        val intent = Intent(context, OutstandingWaiveOffActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_INDIVIDUAL_TAX_DETAILS, getSearchIndividualTaxDetails)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToPenalties(getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails, fromScreen: Any) {
        val intent = Intent(context, PenaltyWaiveOffActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_INDIVIDUAL_TAX_DETAILS, getSearchIndividualTaxDetails)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToIndividualTax(sycoTaxID: String, fromScreen: Any?) {
        val intent = Intent(context, IndividualTaxNoticeActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToIndividualTaxNoticeHistory(getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails, sycoTaxID: String, fromScreen: Any?) {
        val intent = Intent(context, TaxNoticeHistoryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_TAX_NOTICE_HISTORY, getSearchIndividualTaxDetails)
        intent.putExtra(Constant.KEY_CUSTOMER_ID, sycoTaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToIndividualBusinessTransactionHistory(getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails, sycoTaxID: String, fromScreen: Any?) {
        val intent = Intent(context, BusinessTransactionHistoryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_BUSINESS_TRANSACTION_HISTORY, getSearchIndividualTaxDetails)
        intent.putExtra(Constant.KEY_CUSTOMER_ID, sycoTaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun fetchAsset(id: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        if (id.matches("[0-9]+".toRegex())) {
            APICall.updateAsset(id.toInt(), object : ConnectionCallBack<GetUpdateAsset> {
                override fun onSuccess(response: GetUpdateAsset) {
                    mListener?.dismissDialog()
                    navigateToUpdateAsset(getUpdateAssetDetails(response))
                    //    activity?.finish()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                        activity?.finish()
                    })
                }
            })
        } else {
            mListener?.dismissDialog()
            mListener?.showAlertDialog(getString(R.string.no_record))
        }
    }

    private fun validateAssetForReturn(assetNo: String) {
        APICall.validateAsset4Return(assetNo, object : ConnectionCallBack<ValidateAssetForAssignAndReturnResponse> {
            override fun onSuccess(response: ValidateAssetForAssignAndReturnResponse) {
                navigateToAssetReturnScreen(response)
                activity?.finish()

            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun navigateToAssetReturnScreen(responseAssignAndReturn: ValidateAssetForAssignAndReturnResponse) {
        val assetReturnIntent = Intent(requireContext(), AssetBookingActivity::class.java)
        assetReturnIntent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        assetReturnIntent.putExtra(Constant.KEY_ASSET_ID, responseAssignAndReturn.assetId)
        assetReturnIntent.putExtra(Constant.KEY_VALIDATE_ASSET, responseAssignAndReturn)
        startActivity(assetReturnIntent)
    }


    /**
     * If customer ID = 0 && IsSycoTaxID = true then it is a valid QR navigate to Registration
     * If customer ID = 0 && IsSycoTaxID = false then it is a Invalid search
     * If customer ID non 0 , take it to respective screen based on menu selection
     * */
    private fun fetchTaxPayerDetails(filterString: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getTaxPayerDetails(filterString, object : ConnectionCallBack<TaxPayerDetails> {
            override fun onSuccess(response: TaxPayerDetails) {
                onRecordFound(filterString, response)
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_QR_CODE
                && resultCode == Activity.RESULT_OK
                && data != null && data.hasExtra(KEY_QR_CODE_DATA)) {

            data.getStringExtra(KEY_QR_CODE_DATA)?.let {
                when {
                    fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                fetchUpdateAssetList(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            fetchAssetList(it)
                    }//fetchAsset(it)
                    fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING -> fetchAssetDetails(it)
                    fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val bookingId = uri.getQueryParameter("BookingRequestID")
                            if (bookingId != null && !TextUtils.isEmpty(bookingId)) {
                                fetchBookings(bookingId, true)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            fetchBookings(it, true)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val bookingId = uri.getQueryParameter("BookingRequestID")
                            if (bookingId != null && !TextUtils.isEmpty(bookingId)) {
                                fetchBookings(bookingId, true)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            fetchBookings(it, true)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val ticketId = uri.getQueryParameter("ViolationTicketId")
                            if (ticketId != null && !TextUtils.isEmpty(ticketId)) {
                                fetchViolationTicketDetails(ticketId)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            fetchViolationTicketDetails(it)
                    }

                    fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX -> navigateToSellableProducts(it)
                    fromScreen == Constant.QuickMenu.QUICK_MENU_ASSIGN_ASSET -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                getAssetDetailsByScanForAssignmentAndReturn(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            getAssetDetailsByScanForAssignmentAndReturn(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_RETURN -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                getAssetDetailsByScanForAssignmentAndReturn(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            getAssetDetailsByScanForAssignmentAndReturn(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_RETURN_IMPONDMENT -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("TaxNoticeNo")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                getImpondmentReturnList(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            getImpondmentReturnList(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("TaxNoticeNo")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                getLawTaxTransactionList(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            getLawTaxTransactionList(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_LAW_PENALTY_WAIVE_OFF -> getLawPenaltyTransactions(it)

                    fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_LAND -> isPropertySycoTaxIDAvailable(it)

                    fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_SUMMARY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND ->
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            var sycoTaxID = ""
                            if (uri.toString().contains("SycID")) {
                                sycoTaxID = uri.getQueryParameter("SycID").toString()
                            } else if (uri.toString().contains("PropertySycotaxID")) {
                                sycoTaxID = uri.getQueryParameter("PropertySycotaxID").toString()
                            }

                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                showPropertyTaxesList(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else showPropertyTaxesList(it)

                    fromScreen == Constant.QuickMenu.QUICK_MENU_CART_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("TaxSycotaxID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                isCartSycoTaxAvailable(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else isCartSycoTaxAvailable(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("TaxSycotaxID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                isGamingSycoTaxAvailable(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else isGamingSycoTaxAvailable(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_WEAPON_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("TaxSycotaxID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                checkIsWeaponSycotaxAvailable(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else checkIsWeaponSycotaxAvailable(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                isNoticeGen4IndividualTax(sycoTaxID, fromScreen)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else isNoticeGen4IndividualTax(it, fromScreen)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_SUMMARY -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                getOrSearchIndividualTaxDetails(sycoTaxID, fromScreen)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else getOrSearchIndividualTaxDetails(it, fromScreen)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_PENALTY_WAIVE_OFF
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_TRANSACTION_HISTORY
                            || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP -> {
//                        getVehicleOwnership(it, fromScreen)
                        getVehicleOwnershipBySycoTaxId(it)
                    }

                    fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP_SEARCH -> {
                        getUnassignedVehiclesDetailsSearch(it)
                    }

                    fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val taxNoticeNo = uri.getQueryParameter("TaxNoticeNo")
                            if (taxNoticeNo != null && !TextUtils.isEmpty(taxNoticeNo)) {
                                getLawTaxTransactionList(taxNoticeNo)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            getLawTaxTransactionList(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                searchVehicleDetailsBySycoTaxId(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            searchVehicleDetailsBySycoTaxId(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_TICKET_HISTORY -> {
                        fetchParkingTicketHistory(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY -> {

                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                searchVehicleSummaryBySycoTaxId(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            searchVehicleSummaryBySycoTaxId(it)

                        // searchVehicleSummaryBySycoTaxId(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL -> {
                        if (it.contains("https://")) {
                            val uri = Uri.parse(it)
                            val sycoTaxID = uri.getQueryParameter("SycID")
                            if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID)) {
                                scanSearchPendingLicenses(sycoTaxID)
                            } else {
                                navigateToReceiptPreview(it)
                                activity?.finish()
                            }
                        } else
                            scanSearchPendingLicenses(it)
                    }
                    fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER -> {
                        isCitizenSycoTaxAvailable(it)
                    }

                    it.contains("https://") -> {
                        val uri = Uri.parse(it)
                        val sycoTaxID = uri.getQueryParameter("SycID")
                        if (sycoTaxID != null && !TextUtils.isEmpty(sycoTaxID))
                            fetchTaxPayerDetails(sycoTaxID)
                        else {
                            navigateToReceiptPreview(it)
                            activity?.finish()
                        }
                    }
                    else -> fetchTaxPayerDetails(it)
                }
            }
        }
        if (requestCode == Constant.REQUEST_CODE_VEHICLE_DETAILS
                && resultCode == Activity.RESULT_OK) {
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }
    }


    private fun navigateToSellableProducts(productCode: String) {
        val intent = Intent()
        intent.putExtra(Constant.KEY_SCAN_PRODUCT_CODE, productCode)
        mListener?.setResult(Activity.RESULT_OK, intent)
        mListener?.finish()
    }

    private fun navigateToAssetAssignmentScreen(assetNo: String) {
        val intent = Intent()
        intent.putExtra(Constant.KEY_ASSET_ID, assetNo)
        mListener?.setResult(Activity.RESULT_OK, intent)
        mListener?.finish()
    }

    private fun isCitizenSycoTaxAvailable(sycoTaxID: String) {
        mListener?.showProgressDialog()
        APICall.isCitizenSycoTaxAvailable(sycoTaxID, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                navigateToBusinessOwnerScreen(sycoTaxID)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    private fun navigateToBusinessOwnerScreen(sycoTaxID: String) {
        val intent = Intent()
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
        mListener?.setResult(Activity.RESULT_OK, intent)
        mListener?.finish()
    }


    private fun navigateToReceiptPreview(filterString: String) {
        val intent = Intent(context, ReceiptPreviewActivity::class.java)
        intent.putExtra(Constant.KEY_RECEIPT_PREVIEW, filterString)
        startActivity(intent)
    }

    private fun navigateToRegistration(filterString: String) {
        val intent = Intent(context, RegisterBusinessActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, filterString)
        startActivity(intent)
    }

    private fun navigateToAssetBooking(booking: AssetBooking) {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT) {
            val intent = Intent(context, AssetBookingActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            intent.putExtra(Constant.KEY_ASSET_BOOKING, booking)
            startActivity(intent)
            activity?.finish()
        } else {
            booking.assetBookingRequestHeader?.receivedAmount?.let {
                if (BigDecimal.ZERO.compareTo(it) != 0)
                    mListener?.showAlertDialog(getString(R.string.message_can_not_update_asset_booking), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                else {
                    val intent = Intent(context, AssetBookingActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                    intent.putExtra(Constant.KEY_ASSET_BOOKING, booking)
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }
    }

    private fun navigateToUpdateAsset(updateAsset: GetUpdateAsset) {
        val intent = Intent(context, AssetOnBoardingActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_UPDATE_ASSET, updateAsset)
        startActivity(intent)
        activity?.finish()
    }

    interface Listener {
        fun showToast(msg: String)
        fun popBackStack()
        fun setResult(resultCode: Int, intent: Intent)
        fun finish()
        fun scanResult(response: TaxPayerDetails)
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun addFragmentWithFrameLayoutID(fragment: Fragment, addToBackStack: Boolean, frameLayoutID: Int)
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialogFailure(message: String, noRecordsFound: Int, onClickListener: DialogInterface.OnClickListener)
    }




    override fun onQueryTextSubmit(query: String): Boolean {
        if (!TextUtils.isEmpty(query)) {
            if (query.length < 3 &&  (fromScreen != Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT
                            && fromScreen != Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING
                            && fromScreen != Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT)) {
                mListener?.showAlertDialog(getString(R.string.msg_min_data))
                return false
            }//, QUICK_MENU_CART_TAX,QUICK_MENU_GAMING_MACHINE
            when (fromScreen) {
                Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS ->  fetchTaxPayerDetails(query)
                Constant.QuickMenu.QUICK_MENU_ASSET_ON_BOARDING -> fetchAvailableAssetSycotaxIdList(query)
                Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING -> fetchBookings(query, true)
                Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT -> fetchBookings(query, false)
                Constant.QuickMenu.QUICK_MENU_ASSIGN_ASSET -> getAssetDetailsBySearchForAssignmentAndReturn(query)
                Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET -> fetchUpdateAssetList(query)
                Constant.QuickMenu.QUICK_MENU_ASSET_RETURN -> getAssetDetailsBySearchForAssignmentAndReturn(query)
                Constant.QuickMenu.QUICK_MENU_PARKING_PENALTY_WAIVE_OFF -> getVehicleOwnership(query, fromScreen)
                Constant.QuickMenu.QUICK_MENU_PARKING_TRANSACTION_HISTORY -> getVehicleOwnership(query, fromScreen)
                Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP -> getVehicleOwnershipBySycoTaxId(query)
                Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP -> displayScannerListSelection()
                Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE -> displayScannerListSelection()
                Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN -> getVehicleOwnership(query, fromScreen)
                Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT -> getVehicleOwnership(query, fromScreen)
                Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE -> getVehicleOwnership(query, fromScreen)
                Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT -> getVehicleDetailsBySearch(query)
                Constant.QuickMenu.QUICK_MENU_TRACKON_BUSINESS_SUMMARY -> searchVehicleSummary(query)
                Constant.QuickMenu.QUICK_MENU_CART_TAX -> isCartSycoTaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX -> isCartSycoTaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX -> isCartSycoTaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE -> isGamingSycoTaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX -> isGamingSycoTaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX -> isGamingSycoTaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_WEAPON_TAX -> checkIsWeaponSycotaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX -> checkIsWeaponSycotaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX -> checkIsWeaponSycotaxAvailable(query)
                Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX,
                Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE,
                Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX,
                Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE -> showIndividualTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT -> fetchViolationTicketDetails(query)
                Constant.QuickMenu.QUICK_MENU_CREATE_PROPERTY -> isPropertySycoTaxIDAvailable(query)
                Constant.QuickMenu.QUICK_MENU_CREATE_LAND -> isPropertySycoTaxIDAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY -> isPropertySycoTaxIDAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND -> isPropertySycoTaxIDAvailable(query)
                Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND -> isPropertySycoTaxIDAvailable(query)
                Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY, Constant.QuickMenu.QUICK_MENU_UPDATE_LAND -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_SUMMARY -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY -> showPropertyTaxesList(query)
                Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL -> scanSearchPendingLicenses(query)
                Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP_SEARCH -> getUnassignedVehiclesDetailsSearch(query)
                Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER -> isCitizenSycoTaxAvailable(query)
                // else -> searchTaxPayerDetails(query)
                else -> searchTaxPayerList(query)
            }
        }
        return false
    }


    private fun searchTaxPayerDetails(filterString: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getSearchTaxPayerDetails(filterString, object : ConnectionCallBack<List<TaxPayerDetails>> {
            override fun onSuccess(response: List<TaxPayerDetails>) {
                if (response.isNotEmpty()) {
                    displayTaxPayerList(filterString, response)
                } else {
                    binding.searchView.setQuery("", false)
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }

    /*private fun searchTaxPayerList(filterString: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getSearchTaxPayerList(filterString, object : ConnectionCallBack<TaxPayerListResponse> {
            override fun onSuccess(response: TaxPayerListResponse) {
                if (response.searchResults.isNotEmpty()) {
                    displayPayerList(response.searchResults)
                } else {
                    binding.searchView.setQuery("", false)
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }*/

    private fun searchTaxPayerList(filterString: String) {
        hideKeyboard()
        showSearchTaxPayerCustomListDialog(getString(R.string.title_select_business), filterString)
    }

    private fun showSearchTaxPayerCustomListDialog(mTitle: String, query: String) {
        val dialog = requireContext().prepareCustomListDialog(R.layout.alert_dialog_list, true)
        val lView = dialog.findViewById<RecyclerView>(R.id.listView)
        lView.setHasFixedSize(true)
        lView.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))
        dialog.findViewById<TextView>(R.id.listTextView).text = mTitle
        mAdapter = DialogAdapter()
        lView.adapter = mAdapter
        mAdapter?.addItemClickListener { item, position ->
            if (item is TaxPayerList) {
                dialog.dismiss()
                onRecordList(item)
            }
        }
        pagination = Pagination(1, kPAGESIZE, lView) { pageNumber, PageSize ->
            callSearchTaxPayerApi(query, pageNumber, PageSize, dialog)
        }
        pagination.setDefaultValues()
    }

    private fun callSearchTaxPayerApi(query: String, pageNumber: Int, pageSize: Int, dialog: Dialog) {
        if (pageNumber == 1) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
        } else {
            dialog.showhidePaginationProgress(isShow = true)
        }
        val taxPayerDetails = GetTaxPayerDetails()
        taxPayerDetails.filterString = query
        taxPayerDetails.pageIndex = pageNumber
        taxPayerDetails.pageSize = pageSize
        APICall.getSearchTaxPayerList(taxPayerDetails, object : ConnectionCallBack<TaxPayerListResponse> {
            override fun onSuccess(response: TaxPayerListResponse) {
                if (response.searchResults?.isNotEmpty() == true) {
                    if (pageNumber == 1) {
                        response.totalRecordsFound?.let {
                            pagination.totalRecords = it
                        }
                        dialog.show()
                    }
                    displayPayerList(response.searchResults!!)
                } else {
                    pagination.stopPagination(0)
                    if (pageNumber == 1) {
                        binding.searchView.setQuery("", false)
                        mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity?.finish()
                        })
                    }

                }
                mListener?.dismissDialog()
                dialog.showhidePaginationProgress(isShow = false)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageNumber == 1) {
                    mListener?.showAlertDialog(message, DialogInterface.OnClickListener { _, _ ->
                        activity?.finish()
                    })
                }
                dialog.showhidePaginationProgress(isShow = false)
            }
        })
    }

    private fun Dialog.showhidePaginationProgress(isShow: Boolean) {
        findViewById<ProgressBar>(R.id.paginationProgress)?.isVisible = isShow
    }

    private fun displayPayerList(list: List<TaxPayerList>) {
        mAdapter?.updateList(list)
        pagination.setIsScrolled(false)
        pagination.stopPagination(list.size)
    }

    private fun onRecordList(response: TaxPayerList) {
        if (businessMode == Constant.BusinessMode.BusinessActivateVerifyScan) {
            response.sycoTaxID?.let {
                setResultToBussiness(it)
            } ?: response.customer?.let { setResultToBussiness(it) }
        } else {
            response.sycoTaxID?.let {
                searchTaxPayerDetails(it)
            } ?: response.customer?.let { searchTaxPayerDetails(it) }
        }
    }


    private fun displayTaxPayerList(filterString: String, response: List<TaxPayerDetails>) {
        if (response.size == 1) {
            onRecordFound(filterString, response[0])
        } else {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.title_select_business)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response)
            builder.setAdapter(adapter) { dialog, which ->
                dialog.dismiss()
                onRecordFound(filterString, response[which])
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun onRecordFound(filterString: String, response: TaxPayerDetails) {
        if (response.CustomerID == 0) {
            when {
                response.IsSycoTaxIDPresent -> {
                    when (fromScreen) {
                        //If scan is for registering business then navigate directly to registration screen
                        Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                            navigateToRegistration(filterString)
                            activity?.finish()
                        }
                        //If scan is not for registering business then show a popup asking Do you want to register it?
                        else -> {
                            mListener?.showAlertDialog(getString(R.string.register_business_message) + " ${filterString}?", DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                                dialog.dismiss()
                                navigateToRegistration(filterString)
                                activity?.finish()
                            }, DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                                dialog.dismiss()
                                activity?.finish()
                            })
                        }
                    }
                }
                else -> {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }
            }
        } else {
            binding.searchView.setQuery("", false)
            mListener?.scanResult(response)
        }
    }

    private fun getUpdateAssetDetails(response: GetUpdateAsset): GetUpdateAsset {
        response.assetSpecifications?.let {
            for ((index, _) in it.withIndex()) {
                it[index].mandatory = it[index].dynamicForm?.mandatory
                it[index].specification = it[index].dynamicForm?.specification
                it[index].dataType = it[index].dynamicForm?.dataType
            }
        }
        return response
    }


    private fun displayBookingsList(response: List<AssetBooking>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_select_booking)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response)
        builder.setAdapter(adapter) { dialog, which ->
            dialog.dismiss()
            navigateToAssetBooking(response[which])
            activity?.finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun displayAssetsForAssignmentAndReturn(assetsList: List<AssetDetails>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.select_asset))
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, assetsList)
        builder.setAdapter(adapter) { dialog, position ->
            dialog.dismiss()
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSIGN_ASSET) {
                navigateToAssetAssignmentScreen(assetsList[position].assetNumber ?: "")
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_RETURN) {
                validateAssetForReturn(assetsList[position].assetNumber ?: "")
            }
            //activity?.finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getAssetDetailsByScanForAssignmentAndReturn(sycoTaxID: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.searchAssetDetails(sycoTaxID, object : ConnectionCallBack<AssetDetailsBySycotax> {
            override fun onSuccess(response: AssetDetailsBySycotax) {
                mListener?.dismissDialog()
                if (response.assetDetails != null) {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSIGN_ASSET) {
                        navigateToAssetAssignmentScreen(response.assetDetails?.assetNumber
                                ?: "")
                    } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_ASSET_RETURN) {
                        validateAssetForReturn(response.assetDetails?.assetNumber ?: "")
                    }
                } else {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }
        })
    }


    private fun getImpondmentReturnList(data: String) {
        val getImpondmentReturn = GetImpondmentReturn()
        getImpondmentReturn.columnName = "TaxNoticeNo"
        getImpondmentReturn.columnValue = data
        getImpondmentReturn.pageIndex = pageIndex
        getImpondmentReturn.pageSize = pageSize

        mListener?.showProgressDialog()
        APICall.getImpondmentReturnList(getImpondmentReturn, object : ConnectionCallBack<GetImpondmentReturnResponse> {
            override fun onSuccess(response: GetImpondmentReturnResponse) {
                mListener?.dismissDialog()
                if (response.results != null && response.results!!.getImpondmentReturnList != null && response.results!!.getImpondmentReturnList.isNotEmpty()) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(R.string.title_return_impondment)
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response.results!!.getImpondmentReturnList)
                    builder.setAdapter(adapter) { dialog, which ->
                        dialog.dismiss()
                        navigateToNextScreen(response.results!!.getImpondmentReturnList[which], data)

                    }
                    val dialog = builder.create()
                    dialog.show()
                } else
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }
        })
    }


    private fun getLawTaxTransactionList(data: String) {
        val getImpondmentReturnHistory = GetImpondmentReturnHistory()
        getImpondmentReturnHistory.onlydue = "Y"
        getImpondmentReturnHistory.filterType = "TaxNoticeNo"
        getImpondmentReturnHistory.filterString = data //"09iop"

        //mBinding.tvTotalDue.text = formatWithPrecision(it.currentDue)

        mListener?.showProgressDialog()
        APICall.getImpondmentReturnHistory(getImpondmentReturnHistory, object : ConnectionCallBack<GetLAWTaxTransactionsList> {
            override fun onSuccess(response: GetLAWTaxTransactionsList) {
                mListener?.dismissDialog()
                if (response.results != null && response.results.isNotEmpty()) {
                    val mImpondmentReturnHistory = response.results as ArrayList<ImpondmentReturn>

                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY) {
                        val intent = Intent(context, TrackOnTaxNoticeHistoryActivity::class.java)
                        intent.putExtra(Constant.KEY_TRACK_ON_TAX_NOTICE_HISTORY, mImpondmentReturnHistory)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        val intent = Intent(mContext, TicketPaymentActivity::class.java)
                        intent.putParcelableArrayListExtra(Constant.KEY_VIOLATION_VALUE, mImpondmentReturnHistory)
                        intent.putExtra(Constant.KEY_SELECTED_COMBI_VALUE, data)
                        if(fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                            intent.putExtra(Constant.KEY_SELECTED_COMBI_CODE, "TaxNoticeNo")
                        else
                            intent.putExtra(Constant.KEY_SELECTED_COMBI_CODE, "VehicleSycotaxID")
                        mContext.startActivity(intent)
                        mListener?.finish()
                    }
                } else {
                    mListener?.showAlertDialogFailure(
                        getString(R.string.no_records_found),
                        R.string.no_records_found,
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                            mListener?.finish()
                            activity?.finish()
                        }
                    )
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    activity?.finish()
                })

            }
        })
    }


    private fun getLawPenaltyTransactions(data: String) {
        val getPenaltyTransactions = GetPenaltyTransactions()
        getPenaltyTransactions.filterType = "VehicleSycotaxID"
        getPenaltyTransactions.filterString = data

        mListener?.showProgressDialog()
        APICall.getLawPenaltyTransactions(getPenaltyTransactions, object : ConnectionCallBack<GetLawPenaltyTransactionsResponse> {
            override fun onSuccess(response: GetLawPenaltyTransactionsResponse) {
                mListener?.dismissDialog()

                if (response.Penalties != null && response.Penalties.isNotEmpty()) {

                    navigateToNextPenaltyScreen(response.Penalties)

                } else {
                    mListener?.showAlertDialog(mContext.getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        mListener?.finish()
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()

                mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }
        })

    }


    private fun getAssetDetailsBySearchForAssignmentAndReturn(query: String) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.searchAssetList(query, object : ConnectionCallBack<AssetDetailsBySearch> {
            override fun onSuccess(response: AssetDetailsBySearch) {
                mListener?.dismissDialog()
                response.results?.let {
                    if (it.assetList.isNotEmpty() && it.assetList.isNotEmpty()) {
                        displayAssetsForAssignmentAndReturn(it.assetList)
                    } else {
                        binding.searchView.setQuery("", false)
                        mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity?.finish()
                        })
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }
        })
    }


    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgBtnSearch -> {
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_RETURN_IMPONDMENT
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_LAW_PENALTY_WAIVE_OFF
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY)
                    searchDialog()
                else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE)
                    displayScannerListSelection()
                else
                    search(view)
            }
            else -> {

            }
        }
    }

    private fun searchDialog() {
        val dialogFragment: ImpoundmentsComboStaticsDialogFragment = ImpoundmentsComboStaticsDialogFragment.newInstance(mCOMComboStaticValues, fromScreen as Constant.QuickMenu)
        dialogFragment.show(childFragmentManager, ImpoundmentsComboStaticsDialogFragment::class.java.simpleName)

    }


    private fun navigateToNextPenaltyScreen(lawPenalties: ArrayList<LawPenalties>) {
        val intent = Intent(context, PenaltyWaiveOffActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_LAW_TAX_DETAILS, lawPenalties)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToParkingPenaltyScreen(parkingPenalties: ArrayList<ParkingPenalties>) {
        val intent = Intent(context, PenaltyWaiveOffActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_PARKING_TAX_DETAILS, parkingPenalties)
        startActivity(intent)
        activity?.finish()

    }


    private fun navigateToNextScreen(impondmentReturn: ImpondmentReturn, data: String) {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT) {
            val intent = Intent(mContext, TicketPaymentActivity::class.java)
            intent.putExtra(Constant.KEY_VIOLATION_VALUE, impondmentReturn)
            // here data is the scan id and "vehiclesycotaxid" is the scan code
            intent.putExtra(Constant.KEY_SELECTED_COMBI_VALUE, data)
            intent.putExtra(Constant.KEY_SELECTED_COMBI_CODE, "VehicleSycotaxID")
            mContext.startActivity(intent)
            activity?.finish()
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_TRACK_ON_TAX_NOTICE_HISTORY) {
            val intent = Intent(context, TrackOnTaxNoticeHistoryActivity::class.java)
            intent.putExtra(Constant.KEY_TRACK_ON_TAX_NOTICE_HISTORY, impondmentReturn)
            startActivity(intent)
            activity?.finish()
        } else {
            val intent = Intent(context, ImpoundmentReturnHistoryActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            intent.putExtra(Constant.KEY_IMPOUNDMENT_RETURN, impondmentReturn)
            startActivity(intent)
            activity?.finish()
        }


    }

    private fun searchVehicleSummaryBySycoTaxId(sycoTaxID: String) {
        val searchVehicleDetailsBySycotax = SearchVehicleDetailsBySycotax()
        searchVehicleDetailsBySycotax.sycoTaxId = sycoTaxID

        mListener?.showProgressDialog()
        APICall.getVehicleSummaryBySycotax(searchVehicleDetailsBySycotax, object : ConnectionCallBack<SearchVehicleResultResponse> {
            override fun onSuccess(response: SearchVehicleResultResponse) {
                mListener?.dismissDialog()

                if (response.vehiclesDetails?.size ?: 0 > 0)
                    navigateToVehicleSummary(sycoTaxID, response.vehiclesDetails!![0])
                else
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                        activity?.finish()
                    })
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getVehicleOwnershipBySycoTaxId(sycoTaxID: String) {
        mListener?.showProgressDialog()
        APICall.searchVehicleDetailsFromSycoTaxId(sycoTaxID, object : ConnectionCallBack<VehicleDetailsResponse> {
            override fun onSuccess(response: VehicleDetailsResponse) {
                mListener?.dismissDialog()
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP) {
                    if (response.vehicleMaster != null) {
                        if (response.isSycoTaxAvailable) {
                            navigateToVehicleDetails(sycoTaxID, response.vehicleMaster)
                        } else {
                            response.vehicleMaster!!.vehicleSycotaxID?.let { navigateToVehicleDetails(it, response.vehicleMaster) }
                        }
                    } else {
                        if (response.isSycoTaxAvailable) {
                            navigateToVehicleDetails(sycoTaxID, null)
                        } else {
                            mListener?.showAlertDialog(getString(R.string.invalid_syco_tax), DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                activity?.finish()
                            })
                        }
                    }
                } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP
                        || fromScreen == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE) {
                    if (response.vehicleMaster != null) {
                        if (response.isSycoTaxAvailable) {
                            navigateToVehicleDetails(sycoTaxID, response.vehicleMaster)
                        } else {
                            response.vehicleMaster!!.vehicleSycotaxID?.let { navigateToVehicleDetails(it, response.vehicleMaster) }
                        }
                    } else {
                        if (response.isSycoTaxAvailable) {
                            if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP ) {
                                if (response.vehicleMaster!=null) {
                                    setResultToBussiness(sycoTaxID)
                                }
                                else
                                {
                                    mListener?.showAlertDialog(R.string.parking_vehicle_not_register,
                                        R.string.yes, View.OnClickListener {
                                            val dialog =
                                                (it as Button).tag as androidx.appcompat.app.AlertDialog
                                            dialog.dismiss()
                                            startOnBoardingVehicleOwnership(sycoTaxID, null)
                                        }, R.string.no, View.OnClickListener {
                                            val dialog =
                                                (it as Button).tag as androidx.appcompat.app.AlertDialog
                                            dialog.dismiss()
                                            activity?.finish()
                                        })
                                }
                            } else {
                                if (fromScreen != Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE)

                                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE)
                                        fromScreen = Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE
                                    else
                                        fromScreen = Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP

                                mListener?.showAlertDialog(R.string.parking_vehicle_not_register,
                                    R.string.yes, View.OnClickListener {
                                        val dialog =
                                            (it as Button).tag as androidx.appcompat.app.AlertDialog
                                        dialog.dismiss()
                                        startOnBoardingVehicleOwnership(sycoTaxID, null)
                                    }, R.string.no, View.OnClickListener {
                                        val dialog =
                                            (it as Button).tag as androidx.appcompat.app.AlertDialog
                                        dialog.dismiss()
                                        activity?.finish()
                                    })

                            }
                        } else {
                            mListener?.showAlertDialog(getString(R.string.invalid_syco_tax), DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                activity?.finish()
                            })
                        }
                    }
                } else {
                    if (response.vehicleMaster != null) {
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP) {
                            mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialog, which ->
                                mListener?.finish()
                            })
                        } else {
                            navigateToParkingTicketPaymentCollect(sycoTaxID, response.vehicleMaster)
                        }
                    } else {
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSSINESS_VEHICLE_OWNERSHIP) {
//                            setResultToBussiness(sycoTaxID)
                            if (response.isSycoTaxAvailable) {
                                setResultToBussiness(sycoTaxID)
                            } else {
                                mListener?.showAlertDialog(
                                    getString(R.string.invalid_syco_tax),
                                    DialogInterface.OnClickListener { dialog, which ->
                                        dialog.dismiss()
                                        activity?.finish()
                                    })
                            }
                        } else {
                            mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialog, which ->
                                mListener?.finish()
                            })
                        }
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()

                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })

            }
        })
    }

    private fun searchVehicleDetailsBySycoTaxId(sycoTaxID: String) {
        mListener?.showProgressDialog()
        APICall.searchVehicleDetailsFromSycoTaxId(sycoTaxID, object : ConnectionCallBack<VehicleDetailsResponse> {
            override fun onSuccess(response: VehicleDetailsResponse) {
                mListener?.dismissDialog()
                if (!response.isSycoTaxAvailable && response.vehicleMaster != null) {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT)
                        navigateToParkingTicketPaymentCollect(sycoTaxID, response.vehicleMaster)
                    else
                        getParkingTicketDetails(response.vehicleMaster?.vehicleNo
                                ?: "", null)
                } else {
                    mListener?.showAlertDialog(getString(R.string.msg_vehicle_not_registered), DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getVehicleDetailsBySearch(filterString: String) {
        mListener?.showProgressDialog()
        val searchVehicleDetails = SearchVehicleDetails()
        searchVehicleDetails.filterString = filterString
        searchVehicleDetails.pageIndex = 1
        searchVehicleDetails.pageSize = 20
        APICall.searchVehicleDetails(searchVehicleDetails, object : ConnectionCallBack<SearchVehicleResultResponse> {
            override fun onSuccess(response: SearchVehicleResultResponse) {
                mListener?.dismissDialog()
                if (response.results == null) {
                    mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity?.finish()
                    })
                }
                response.results?.let {
                    if (it.vehicleDetails.isNotEmpty()) {
                        displayVehiclesForSelection(it.vehicleDetails)
                    } else {
                        binding.searchView.setQuery("", false)
                        mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity?.finish()
                        })
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })

            }
        })
    }

    private fun displayVehiclesForSelection(vehicleDetails: List<VehicleMaster>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.msg_select_vehicle))
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, vehicleDetails)
        builder.setAdapter(adapter) { dialog, position ->
            dialog.dismiss()
            getParkingTicketDetails(vehicleDetails[position].vehicleNo ?: "", null)
            //activity?.finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getParkingTicketDetails(vehicleNo: String, vehicleMaster: VehicleMaster?) {
        var inOut = ""
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN) {
            inOut = "IN"
        } else {
            inOut = "" //TODO Empty
        }
        APICall.getParkingTicketDetails(vehicleNo, prefHelper.parkingPlaceID, inOut, object : ConnectionCallBack<ParkingTicketDetailsResponse?> {
            override fun onSuccess(response: ParkingTicketDetailsResponse?) {
                if (response?.ticketDetails == null)
                    isValidParking = false
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN && !isValidParking) {
                    if (vehicleMaster != null && vehicleMaster.vehicleImpounded == Constant.ACTIVE_Y) {
                        mListener?.showAlertDialog(getString(R.string.vehicle_impounded), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            mListener?.finish()
                        })
                    } else {
                        vehicleMaster?.let { getVehicleOwnership(it, response?.ticketDetails) }
                    }
                } else {
                    response?.ticketDetails?.currentDue = response?.currentDue ?: 0.0
                    response?.ticketDetails?.netReceivable = response?.netReceivable
                            ?: BigDecimal.ZERO
                    response?.ticketDetails?.netReceivable = response?.netReceivable
                            ?: BigDecimal.ZERO
                    response?.ticketDetails?.isPass = response?.isPass ?: ""
                    response?.ticketDetails?.vehicleOwnerAccountId = response?.vehicleOwnerAccountId
                    response?.ticketDetails?.parkingTicketId = response?.parkingTicketId
                    navigateToVehicleOutScreen(response?.ticketDetails, vehicleNo)
                }
            }

            override fun onFailure(message: String) {
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    activity?.finish()
                })
            }
        })
    }

    private fun navigateToVehicleOutScreen(details: ParkingTicketDetails?, vehicleNo: String) {
        val intent = Intent(context, VehicleOutActivity::class.java)
        intent.putExtra(Constant.KEY_PARKING_TICKET_DETAILS, details)
        intent.putExtra(Constant.KEY_IS_VALID_PARKING, isValidParking)
        intent.putExtra(Constant.KEY_VEHICLE_NO, vehicleNo)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        startActivity(intent)
        activity?.finish()
    }


    private fun navigateToVehicleInScreen(vehicleDetails: VehicleDetails, details: ParkingTicketDetails?) {
        val intent = Intent(context, ParkingTicketEntryActivity::class.java)
        intent.putExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS, vehicleDetails)
        intent.putExtra(Constant.KEY_PARKING_TICKET_DETAILS, details)
        intent.putExtra(Constant.KEY_IS_VALID_PARKING, isValidParking)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        startActivity(intent)
        activity?.finish()
    }


    private fun getVehicleOwnership(vehicleMaster: VehicleMaster, details: ParkingTicketDetails?) {
        mListener?.showProgressDialog()
        APICall.getVehicleOwnershipDetails(vehicleMaster.vehicleNo, object : ConnectionCallBack<VehicleOwnershipDetailsResult> {
            override fun onSuccess(response: VehicleOwnershipDetailsResult) {
                mListener?.dismissDialog()
                val list = response.vehicleDetails
                if (list?.size ?: 0 == 0) {
                    mListener?.showAlertDialog("${getString(R.string.no_record)} with ${vehicleMaster.vehicleNo}")
                    return
                }
                list?.let {
                    for (vehicle: VehicleDetails in it) {
                        if (vehicle.toDate == null) {
                            navigateToVehicleInScreen(vehicle, details)
                            break
                        }
                    }
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (message.isNotEmpty())
                    mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getIndividualTaxesList(query: String): AdvanceSearchFilter {
        val searchFilter = AdvanceSearchFilter()
        //region Apply Filters
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "SycotaxID"
        filterColumn.columnValue = query
        filterColumn.srchType = "like"

        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList
        //endregion

        //region Apply Table/View details
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_IndividualTaxes"
        tableDetails.primaryKeyColumnName = "VoucherNo"
        tableDetails.selectColoumns = "SycotaxID,AccountID,Product,VoucherNo,TaxRuleBookCode"
        tableDetails.TableCondition = "AND"
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX) {
            tableDetails.initialTableCondition = "TaxRuleBookCode = 'CART'"
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
            tableDetails.initialTableCondition = "TaxRuleBookCode = 'WP'"
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE) {
            tableDetails.initialTableCondition = "TaxRuleBookCode = 'GM'"
        } else {
            tableDetails.initialTableCondition = "TaxRuleBookCode= 'CART' OR TaxRuleBookCode='WP' OR TaxRuleBookCode= 'GM'"
        }

        searchFilter.tableDetails = tableDetails
        //endregion

        searchFilter.pageIndex = 1
        searchFilter.pageSize = 100

        return searchFilter
    }

    private fun getIndividualTaxesList(query: String, pageIndex: Int, pageSize: Int): AdvanceSearchFilter {
        val searchFilter = AdvanceSearchFilter()
        //region Apply Filters
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "SycotaxID"
        filterColumn.columnValue = query
        filterColumn.srchType = "like"

        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList
        //endregion

        //region Apply Table/View details
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_IndividualTaxes"
        tableDetails.primaryKeyColumnName = "VoucherNo"
        tableDetails.selectColoumns = "SycotaxID,AccountID,Product,VoucherNo,TaxRuleBookCode"
        tableDetails.TableCondition = "AND"
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX) {
            tableDetails.initialTableCondition = "TaxRuleBookCode = 'CART'"
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
            tableDetails.initialTableCondition = "TaxRuleBookCode = 'WP'"
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE) {
            tableDetails.initialTableCondition = "TaxRuleBookCode = 'GM'"
        } else {
            tableDetails.initialTableCondition = "TaxRuleBookCode= 'CART' OR TaxRuleBookCode='WP' OR TaxRuleBookCode= 'GM'"
        }

        searchFilter.tableDetails = tableDetails
        //endregion

        searchFilter.pageIndex = pageIndex
        searchFilter.pageSize = pageSize

        return searchFilter
    }

    /* private fun scanSearchPendingLicenses(query: String) {
         mListener?.showProgressDialog(R.string.msg_please_wait)
         APICall.scanSearchPendingLicenses(query, object : ConnectionCallBack<SearchPendingLicensesResponse> {
             override fun onSuccess(response: SearchPendingLicensesResponse) {
                 mListener?.dismissDialog()
                 if (response.PendingLicenses4Agent.pendingLicenses != null && response.PendingLicenses4Agent.pendingLicenses.size > 0) {
                     val list = response.PendingLicenses4Agent.pendingLicenses
                     val adapter = list.let { ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it) }
                     showListAlertDialog(getString(R.string.title_pending_licenses), adapter)
                 } else {
                     mListener?.showAlertDialog(getString(R.string.no_records_found))
                 }
             }

             override fun onFailure(message: String) {
                 mListener?.dismissDialog()
                 mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
             }
         })
     }*/

    private fun scanSearchPendingLicenses(query: String) {
        hideKeyboard()
        showSearchPendingLicensesCustomListDialog(getString(R.string.title_pending_licenses), query)
    }

    private fun showSearchPendingLicensesCustomListDialog(mTitle: String, query: String) {
        val dialog = requireContext().prepareCustomListDialog(R.layout.alert_dialog_list, true)
        val lView = dialog.findViewById<RecyclerView>(R.id.listView)
        lView.setHasFixedSize(true)
        lView.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))
        dialog.findViewById<TextView>(R.id.listTextView).text = mTitle
        mAdapter = setAdapter(dialog)
        lView.adapter = mAdapter
        pagination = Pagination(1, kPAGESIZE, lView) { pageNumber, PageSize ->
            callPendingLicensesApi(query, pageNumber, PageSize, dialog)
        }
        pagination.setDefaultValues()
    }

    private fun callPendingLicensesApi(query: String, pageNumber: Int, pageSize: Int, alertDialog: Dialog) {
        if (pageNumber == 1) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
        } else {
            alertDialog.showhidePaginationProgress(true)
        }
        val getPendingLicenses = SearchPendingLicenses()
        getPendingLicenses.filterString = query
        getPendingLicenses.pageindex = pageNumber
        getPendingLicenses.pagesize = pageSize
        APICall.scanSearchPendingLicenses(getPendingLicenses, object : ConnectionCallBack<SearchPendingLicensesResponse> {
            override fun onSuccess(response: SearchPendingLicensesResponse) {
                mListener?.dismissDialog()
                if (response.PendingLicenses4Agent?.pendingLicenses?.size ?: 0 > 0) {
                    val list = response.PendingLicenses4Agent!!.pendingLicenses!!
                    if (pageNumber == 1) {
                        response.totalSearchedRecords?.let {
                            pagination.totalRecords = it
                        }
                        alertDialog.show()
                    }
                    mAdapter?.updateList(list)
                    pagination.setIsScrolled(false)
                    pagination.stopPagination(list.size)

                } else {
                    pagination.stopPagination(0)
                    if (pageNumber == 1) {
                        mListener?.showAlertDialog(getString(R.string.no_records_found))
                    }
                }
                alertDialog.showhidePaginationProgress(false)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageNumber == 1) {
                    mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                }
                alertDialog.showhidePaginationProgress(false)
            }
        })
    }


    /*fun showListAlertDialog(mTitle: String, adapter: Any?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(mTitle)
        builder.setAdapter(adapter as ArrayAdapter<*>) { dialog, which ->
            dialog.dismiss()
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX) {
                adapter as ArrayAdapter<VuCrmIndividualTaxes>
                adapter.getItem(which)?.SycotaxID?.let { getCartTax(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
                adapter as ArrayAdapter<VuCrmIndividualTaxes>
                adapter.getItem(which)?.SycotaxID?.let { getWeaponTax(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE) {
                adapter as ArrayAdapter<VuCrmIndividualTaxes>
                adapter.getItem(which)?.SycotaxID?.let { getGamingTax(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE) {
                adapter as ArrayAdapter<VuCrmIndividualTaxes>
                adapter.getItem(which)?.SycotaxID?.let { isNoticeGen4IndividualTaxSelected(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION) {
                adapter as ArrayAdapter<VuCrmIndividualTaxes>
                adapter.getItem(which)?.SycotaxID?.let { isNoticeGen4IndividualTaxSelected(it) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_PENALTY_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_SUMMARY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF) {
                adapter as ArrayAdapter<VuCrmIndividualTaxes>
                adapter.getItem(which)?.SycotaxID?.let { getOrSearchIndividualTaxDetails(it, fromScreen) }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND) {
                adapter as ArrayAdapter<VuComProperties>
                adapter.getItem(which)?.propertySycotaxID?.let { searchPropertyDetailsBySycoTax(it) }

            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE || fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_COLLECTION || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE) {
                adapter as ArrayAdapter<VuComProperties>
                adapter.getItem(which)?.propertySycotaxID?.let {
                    navigateToPropertyTaxDetailsScreen(it, adapter.getItem(which)?.propertyID, fromScreen as Constant.QuickMenu)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_SUMMARY || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_SUMMARY) {
                adapter as ArrayAdapter<VuComProperties>
                adapter.getItem(which)?.let {
                    NavigateToPropertyLandSummaryActivity(it)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE_HISTORY) {
                adapter as ArrayAdapter<VuComProperties>
                adapter.getItem(which)?.propertySycotaxID?.let {
                    navigateToPropertyTaxNoticeHistory(adapter.getItem(which), adapter.getItem(which)?.propertySycotaxID)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF) {
                adapter as ArrayAdapter<VuComProperties>
                adapter.getItem(which)?.propertySycotaxID?.let {
                    navigateToPropertyPenalityWaiveOffScreen(adapter.getItem(which))
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF) {
                adapter as ArrayAdapter<VuComProperties>
                adapter.getItem(which)?.propertySycotaxID?.let {
                    navigateToOutstandingWaiveOffScreen(adapter.getItem(which))
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY
                    || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY) {
                adapter as ArrayAdapter<VuComProperties>
                adapter.getItem(which)?.propertySycotaxID?.let {
                    navigateToPropertyTransactionHistory(adapter.getItem(which), it)
                }
            } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL) {
                adapter as ArrayAdapter<PendingLicenses4Agent>
                adapter.getItem(which)?.licenseNumber?.let {
                    navigateToTaxDetailsActivity(adapter.getItem(which))
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }*/

    private fun navigateToTaxDetailsActivity(item: PendingLicenses4Agent?) {
        val intent = Intent(context, TaxDetailsActivity::class.java)
        intent.putExtra(Constant.KEY_CUSTOMER_ID, item?.accountId)
        intent.putExtra(Constant.KEY_LICENSE_NUMBER, item?.licenseNumber)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToPropertyTaxDetailsScreen(propertySycotaxID: String?, propertyID: Int?, quickMenu: Constant.QuickMenu) {
        val intent = Intent(context, PropertyLandTaxDetailsActivity::class.java)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, propertySycotaxID)
        intent.putExtra(Constant.KEY_PRIMARY_KEY, propertyID)
        intent.putExtra(Constant.KEY_QUICK_MENU, quickMenu)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToPropertyTaxNoticeHistory(mVuComProperties: VuComProperties?, propertySycotaxID: String?) {
        val intent = Intent(context, TaxNoticeHistoryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_PROPERTY_TAX_NOTICE_HISTORY, mVuComProperties)
        intent.putExtra(Constant.KEY_CUSTOMER_ID, propertySycotaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToPropertyPenalityWaiveOffScreen(mVuComProperties: VuComProperties?) {
        val intent = Intent(context, PenaltyWaiveOffActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_PROPERTY_TAX_PENALTY_WAIVE_OFF, mVuComProperties)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToOutstandingWaiveOffScreen(mVuComProperties: VuComProperties?) {
        val intent = Intent(context, OutstandingWaiveOffActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF, mVuComProperties)
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToPropertyTransactionHistory(mVuComProperties: VuComProperties?, propertySycotaxID: String?) {
        val intent = Intent(context, PropertyTransactionHistoryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_PROPERTY_TRANSACTION_HISTORY, mVuComProperties)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, propertySycotaxID)
        startActivity(intent)
        activity?.finish()
    }

    private fun NavigateToPropertyLandSummaryActivity(item: VuComProperties) {
        val intent = Intent(context, PropertyTaxSummaryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_SYCO_TAX_ID, item.propertySycotaxID)
        intent.putExtra(Constant.KEY_PRIMARY_KEY, item.propertyID)
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, item.taxRuleBookCode)
        // taxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
        startActivity(intent)
        activity?.finish()
    }


}