package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.IncidentAndComplaintsCountsResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentQuickMenuBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.QuickMenuItem
import com.sgs.citytax.ui.*
import com.sgs.citytax.ui.adapter.DashboardAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import com.sgs.citytax.util.Constant.REQUEST_IMAGE_CAPTURE

class QuickMenuFragment : BaseFragment(), IClickListener {

    private var binding: FragmentQuickMenuBinding? = null
    private var listener: Listener? = null
    private lateinit var mAdapter: DashboardAdapter
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE

    companion object {
        @JvmStatic
        fun newInstance() = QuickMenuFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quick_menu, container, false)
        initComponents()
        return binding?.root
    }

    override fun initComponents() {
        setViews()
        bindData()
    }

    private fun setViews() {
        mAdapter = DashboardAdapter(getDashboardMenu(requireContext(), 0, 0), this)
    }

    private fun bindData() {
        binding?.recyclerView?.adapter = mAdapter
        APICall.getIncidentAndComplaintsCounts(object : ConnectionCallBack<IncidentAndComplaintsCountsResponse> {
            override fun onSuccess(response: IncidentAndComplaintsCountsResponse) {
                response.complaintCount?.let { response.incidentCount?.let { it1 -> callDashboardAdapter(it, it1) } }
            }

            override fun onFailure(message: String) {
                callDashboardAdapter(0, 0)
            }
        })
    }

    private fun callDashboardAdapter(complaintCount: Int = 0, incidentCount: Int = 0) {
        binding?.recyclerView?.adapter = DashboardAdapter(getDashboardMenu(requireContext(), complaintCount, incidentCount), this)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        //region Clear ObjectHolder before using new feature
        ObjectHolder.clearAll()
        //endregion
        val menuItem = obj as QuickMenuItem
        mCode = menuItem.code
        when (mCode) {
            Constant.QuickMenu.QUICK_MENU_REGISTER,
            Constant.QuickMenu.QUICK_MENU_UPDATE,
            Constant.QuickMenu.QUICK_MENU_TAX_NOTICE,
            Constant.QuickMenu.QUICK_MENU_TAX_COLLECTION -> {
                fragmentManager?.let {
                    DashboardDialogFragment.newInstance(mCode).show(fragmentManager as FragmentManager, DashboardDialogFragment::class.java.simpleName)
                }
            }
            Constant.QuickMenu.QUICK_MENU_INCIDENTS -> {
                startActivity(Intent(context, IncidentActivity::class.java))
            }
            Constant.QuickMenu.QUICK_MENU_TASKS -> {
                startActivity(Intent(context, TaskActivity::class.java))
            }
            Constant.QuickMenu.QUICK_MENU_SALES_TAX -> {
                 val intent = Intent(context, SellableTaxActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU,mCode)
                startActivity(intent)
            }
            Constant.QuickMenu.QUICK_MENU_SECURITY_TAX -> {
                val intent = Intent(context, SellableTaxActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, mCode)
                startActivity(intent)
            }

            Constant.QuickMenu.QUICK_MENU_WALLET -> {
                fragmentManager?.let {
                    DashboardDialogFragment.newInstance(mCode).show(fragmentManager as FragmentManager, DashboardDialogFragment::class.java.simpleName)
                }
            }
            Constant.QuickMenu.QUICK_MENU_IMPONDMENT -> {
                openCamera()
            }
            Constant.QuickMenu.QUICK_MENU_MY_AGENTS -> {
                val myAgents = Intent(context, MyAgentsActivity::class.java)
                startActivity(myAgents)
            }
            Constant.QuickMenu.QUICK_MENU_OTHER_TAXES -> {
                fragmentManager?.let {
                    DashboardDialogFragment.newInstance(mCode).show(fragmentManager as FragmentManager, DashboardDialogFragment::class.java.simpleName)
                }
            }
            Constant.QuickMenu.QUICK_MENU_SERVICE -> {
                val intent = Intent(context, ServiceActivity::class.java)
                intent.putExtra(KEY_QUICK_MENU, mCode)
                startActivity(intent)
            }
            Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE -> {
               openCamera()
            }
            Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                startActivity(intent)
            }
            Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                startActivity(intent)
            }
            Constant.QuickMenu.QUICK_MENU_RETURN_IMPONDMENT -> {
                val intent = Intent(context, ReturnImpoundmentActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                startActivity(intent)
            }
            Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
                startActivity(intent)
            }
            Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN -> {
                if (MyApplication.getPrefHelper().allowParking == "Y") {
                    if (MyApplication.getPrefHelper().parkingPlaceID > 0) {
                        val intent = Intent(context, ScanActivity::class.java)
                        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_IN)
                        startActivity(intent)
                    } else {
                        context?.resources?.getString(R.string.please_select_parking_place)?.let { listener?.showToast(it) }
                    }

                }
            }
            Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT -> {
                if (MyApplication.getPrefHelper().allowParking == "Y") {
                    if (MyApplication.getPrefHelper().parkingPlaceID > 0) {
                        val intent = Intent(context, ScanActivity::class.java)
                        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OUT)
                        startActivity(intent)
                    } else {
                        context?.resources?.getString(R.string.please_select_parking_place)?.let { listener?.showToast(it) }
                    }
                }
            }
            Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT -> {
                if (MyApplication.getPrefHelper().allowParking == "Y") {
                    if (MyApplication.getPrefHelper().parkingPlaceID > 0) {
                        val intent = Intent(context, ScanActivity::class.java)
                        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PARKING_COLLECT)
                        startActivity(intent)
                    } else {
                        context?.resources?.getString(R.string.please_select_parking_place)?.let { listener?.showToast(it) }
                    }
                }
            }
            Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES -> {
                val intent = Intent(context, ScanActivity::class.java)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_NOTICES)
                startActivity(intent)
            }
            Constant.QuickMenu.QUICK_MENU_STOCK_MANAGEMENT -> {
                val intent = Intent(context, AdjustmentsActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    interface Listener {
        fun addFragmentWithOutAnimation(fragment: Fragment, addToBackStack: Boolean)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showToast(message: String)
    }

    override fun onResume() {
        super.onResume()
        bindData()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                openCamera()
            else
                listener?.showToast(getString(R.string.msg_permission_storage_camera))
        }
    }
    fun openCamera(){
        if (!hasPermission(requireActivity(), Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            data?.let {
                when (mCode) {
                    Constant.QuickMenu.QUICK_MENU_IMPONDMENT -> {
                        ObjectHolder.documents = arrayListOf()
                        val intent = Intent(requireContext(), ImpoundmentActivity::class.java)
                        intent.putExtra(Constant.KEY_DOCUMENT, prepareDocument(it))
                        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                        startActivity(intent)
                    }
                    Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE -> {
                        ObjectHolder.documents = arrayListOf()
                        ObjectHolder.violations = arrayListOf()
                        val intent = Intent(context, VehicleTicketIssueActivity::class.java)
                        intent.putExtra(Constant.KEY_DOCUMENT, prepareDocument(it))
                        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                        startActivity(intent)
                    }
                    else -> {
                    }
                }
            }
        }
    }
    private fun prepareDocument(intent: Intent): COMDocumentReference {
        val photo = intent.extras?.get("data") as Bitmap
        val document = COMDocumentReference()
        document.documentName = System.currentTimeMillis().toString()
        document.extension = when (mCode) {
            Constant.QuickMenu.QUICK_MENU_IMPONDMENT -> "Impoundment.jpeg"
            Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE -> "VehiclePicture.jpeg"
            else -> ""
        }
        document.data = ImageHelper.getBase64String(photo)
        document.documentProofType = ""
        return document
    }
}