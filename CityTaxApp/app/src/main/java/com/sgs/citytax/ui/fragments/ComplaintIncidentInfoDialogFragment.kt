package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ComplaintIncidentDetailLocation
import com.sgs.citytax.api.response.IncidentDetailLocation
import com.sgs.citytax.api.response.VUCRMServiceRequest
import com.sgs.citytax.databinding.FragmentComplaintIncidentSearchInfoBinding
import com.sgs.citytax.util.*

class ComplaintIncidentInfoDialogFragment : DialogFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentComplaintIncidentSearchInfoBinding
    private var mListener: Listener? = null
    private var incidentLocation: IncidentDetailLocation? = null
    private var complaintsLocation: ComplaintIncidentDetailLocation? = null
    private var fromScreenCode: Any? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(incidentLocations: IncidentDetailLocation?, complaintLocations: ComplaintIncidentDetailLocation?, fromScreen: Any?) = ComplaintIncidentInfoDialogFragment().apply {
            this.incidentLocation = incidentLocations
            this.complaintsLocation = complaintLocations
            this.fromScreenCode = fromScreen
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_complaint_incident_search_info, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AlertDialogTheme)
    }

    fun initComponents() {
        bindData()
        setViewsVisibility()
        setListeners()
    }

    private fun setViewsVisibility() {
        if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_COMPLAINT) {
            mBinding.btnIncident.visibility = View.GONE
        } else {
            mBinding.btnIncident.visibility = View.VISIBLE
        }
    }

    private fun bindData() {
        if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_COMPLAINT) {
            if (complaintsLocation != null) {
                mBinding.txtComplaintNo.text = "${complaintsLocation?.complaintNo}"
                mBinding.txtComplaint.text = complaintsLocation?.complaint.toString()
                mBinding.txtComplaintDate.text = getDate(complaintsLocation?.complaintDate
                        ?: "", DateTimeTimeZoneMillisecondFormat, displayDateTimeTimeSecondFormat)
                complaintsLocation?.complaintSubtype?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llComplaintSubType.visibility = View.VISIBLE
                        mBinding.txtComplaintSubType.text = it
                    }
                }

                complaintsLocation?.priority?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llPriority.visibility = View.VISIBLE
                        mBinding.txtPriority.text = it
                    }
                }

                complaintsLocation?.status?.let {
                    if (it.isNotEmpty()) {
                        mBinding.txtStatus.text = it
                    }
                }

                complaintsLocation?.title?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llTitle.visibility = View.VISIBLE
                        mBinding.txtTitle.text = it
                    }
                }
                complaintsLocation?.issueDescription?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llIssueDescription.visibility = View.VISIBLE
                        mBinding.txtIssueDescription.text = it
                    }
                }
                complaintsLocation?.reportedBy?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llReportedBy.visibility = View.VISIBLE
                        mBinding.txtReportedBy.text = it
                    }
                }
                complaintsLocation?.zone?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llZone.visibility = View.VISIBLE
                        mBinding.txtZone.text = it
                    }
                }
                complaintsLocation?.sector?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llSector.visibility = View.VISIBLE
                        mBinding.txtSector.text = it
                    }
                }
            }
        } else if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_INCIDENT) {
            if (incidentLocation != null) {
                mBinding.txtLabelComplaintNo.text = context?.resources?.getString(R.string.label_incident_no)
                mBinding.txtLabelComplaint.text = context?.resources?.getString(R.string.label_incident_type)
                mBinding.txtLabelComplaintSubType.text = context?.resources?.getString(R.string.label_incident_sub_type)
                mBinding.txtLabelComplaintDate.text = context?.resources?.getString(R.string.label_incident_date)

                mBinding.txtComplaintNo.text = "${incidentLocation?.incidentNo}"
                mBinding.txtComplaint.text = incidentLocation?.incidentType.toString()
                incidentLocation?.incidentSubtype?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llComplaintSubType.visibility = View.VISIBLE
                        mBinding.txtComplaintSubType.text = it
                    }
                }
                mBinding.txtComplaintDate.text = getDate(incidentLocation?.incidentDate
                        ?: "", DateTimeTimeZoneMillisecondFormat, displayDateTimeTimeSecondFormat)
                incidentLocation?.title?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llTitle.visibility = View.VISIBLE
                        mBinding.txtTitle.text = it
                    }
                }

                incidentLocation?.status?.let {
                    if (it.isNotEmpty()) {
                        mBinding.txtStatus.text = it
                    }
                }

                incidentLocation?.priority?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llPriority.visibility = View.VISIBLE
                        mBinding.txtPriority.text = it
                    }
                }
                incidentLocation?.issueDescription?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llIssueDescription.visibility = View.VISIBLE
                        mBinding.txtIssueDescription.text = it
                    }
                }
                incidentLocation?.reportedBy?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llReportedBy.visibility = View.VISIBLE
                        mBinding.txtReportedBy.text = it
                    }
                }
                incidentLocation?.zone?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llZone.visibility = View.VISIBLE
                        mBinding.txtZone.text = it
                    }
                }
                incidentLocation?.sector?.let {
                    if (it.isNotEmpty()) {
                        mBinding.llSector.visibility = View.VISIBLE
                        mBinding.txtSector.text = it
                    }
                }
            }
        }
    }

    private fun setListeners() {
        mBinding.btnIncident.setOnClickListener(this)
    }

    interface Listener {
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun onClick()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding.btnIncident.id -> {
                mListener?.onClick()
                mListener?.dismissDialog()
                dismiss()
                if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_INCIDENT) {
                    val vucrmServiceRequest = VUCRMServiceRequest()
                    vucrmServiceRequest.incidentSubtype = incidentLocation?.incidentSubtype
                    vucrmServiceRequest.incidentSubtypeID = incidentLocation?.incidentSubtypeID
                    vucrmServiceRequest.incidentID = incidentLocation?.incidentID
                    vucrmServiceRequest.incident = incidentLocation?.incidentType
                    vucrmServiceRequest.serviceRequestDate = incidentLocation?.incidentDate
                    vucrmServiceRequest.status = incidentLocation?.status
                    vucrmServiceRequest.issueDescription = incidentLocation?.issueDescription
                    vucrmServiceRequest.serviceRequestNo = incidentLocation?.incidentNo
                    vucrmServiceRequest.latitude = incidentLocation?.latitude?.toDouble()
                    vucrmServiceRequest.longitude = incidentLocation?.longitude?.toDouble()
                    vucrmServiceRequest.priority=incidentLocation?.priority
                    val fragment = IncidentEntryFragment.newInstance(vucrmServiceRequest, fromScreenCode)
                    mListener?.addFragment(fragment, true)
                }
            }
        }
    }
}