package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.StorePropertyData
import com.sgs.citytax.api.response.PropertyImageResponse
import com.sgs.citytax.api.response.PropertyPlanImageResponse
import com.sgs.citytax.databinding.FragmentPropertyDocumentVerificationBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.PropertyImagesPreviewActivity
import com.sgs.citytax.ui.PropertyPlansPreviewActivity
import com.sgs.citytax.ui.adapter.PropertyDocumentVerificationAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class PropertyDocumentVerificationFragment : BaseFragment(), IClickListener, View.OnClickListener {
    private lateinit var mBinding: FragmentPropertyDocumentVerificationBinding
    private var mListener: Listener? = null
    private var pendingList: PendingRequestList? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var propertyDetails: StorePropertyData? = null
    private var geoAddressList: ArrayList<GeoAddress>? = null
    private var mSycoTaxID: String? = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_document_verification, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        //region Arguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

            if (it.containsKey(Constant.KEY_PENDING_PROPERTY_LIST))
                pendingList = it.getParcelable(Constant.KEY_PENDING_PROPERTY_LIST)

            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                mSycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)

            if (it.containsKey(Constant.KEY_PROPERTY_DETAILS))
                propertyDetails = it.getParcelable(Constant.KEY_PROPERTY_DETAILS)

            if (it.containsKey(Constant.KEY_ADDRESS))
                geoAddressList = it.getParcelableArrayList(Constant.KEY_ADDRESS)
        }
        //endregion
        setViews()
        getPropertyPlans()
        setListeners()
    }

    private fun setViews() {
        mBinding.listView.setAdapter(PropertyDocumentVerificationAdapter(this))
    }

    private fun setListeners() {
        mBinding.btnApprove.setOnClickListener(this)
        mBinding.btnReject.setOnClickListener(this)
    }

    private fun getOwnerShipDocuments() {
        mListener?.showProgressDialog()
        APICall.getDocumentDetails(pendingList?.ownershipID.toString(), "CRM_PropertyOwnership", object : ConnectionCallBack<List<COMDocumentReference>> {
            override fun onSuccess(response: List<COMDocumentReference>) {
                mListener?.dismissDialog()
                val comDocumentReferences = response as ArrayList<COMDocumentReference>
                val list = arrayListOf<PendingRequestList>()
                val pendingList = PendingRequestList()
                pendingList.documents = comDocumentReferences
                list.add(pendingList)
                if (list.isNotEmpty())
                    (mBinding.listView.expandableListAdapter as PropertyDocumentVerificationAdapter).update(getString(R.string.ownership_documents), list, mBinding.listView)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })

    }

    private fun getPropertyImages() {
        mListener?.showProgressDialog()
        APICall.getPropertyImages(pendingList?.propertyId
                ?: 0, object : ConnectionCallBack<PropertyImageResponse> {
            override fun onSuccess(response: PropertyImageResponse) {
                mListener?.dismissDialog()
                val images = response.propertyImages
                val list = arrayListOf<PropertyImageResponse>()
                val imageResponse = PropertyImageResponse()
                imageResponse.propertyImages = images
                list.add(imageResponse)

                if (list.isNotEmpty())
                    (mBinding.listView.expandableListAdapter as PropertyDocumentVerificationAdapter).update(getString(R.string.property_images), list, mBinding.listView)
                getOwnerShipDocuments()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    private fun getPropertyPlans() {
        mListener?.showProgressDialog()
        APICall.getPropertyPlans(pendingList?.propertyId
                ?: 0, object : ConnectionCallBack<PropertyPlanImageResponse> {
            override fun onSuccess(response: PropertyPlanImageResponse) {
                mListener?.dismissDialog()
                val plans = response.propertyplans
                val list = arrayListOf<PropertyPlanImageResponse>()
                val propertyPlan = PropertyPlanImageResponse()
                propertyPlan.propertyplans = plans
                list.add(propertyPlan)
                if (list.isNotEmpty())
                    (mBinding.listView.expandableListAdapter as PropertyDocumentVerificationAdapter).update(getString(R.string.property_plan_documents), list, mBinding.listView)

                getPropertyImages()

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    private fun showRemarksPopUp(isApprove: Boolean) {
        // region EditText
        val view = EditText(context)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 0, 16, 0)
        view.layoutParams = params
        view.hint = getString(R.string.hint_enter_remarks)
        // endregion
        mListener?.showAlertDialog(R.string.remarks,
                R.string.save,
                View.OnClickListener {
                    val remarks = view.text?.toString()?.trim()
                    if (TextUtils.isEmpty(remarks)) {
                        view.error = getString(R.string.msg_enter_remarks)
                    } else {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        if (isApprove)
                            approveVerification(remarks ?: "")
                        else
                            rejectVerification(remarks ?: "")
                    }
                }, R.string.cancel, View.OnClickListener {
            val dialog = (it as Button).tag as AlertDialog
            dialog.dismiss()
        }, 0, null, view)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnApprove -> {
                    showRemarksPopUp(true)
                }
                R.id.btnReject -> {
                    showRemarksPopUp(false)
                }
                else -> {
                }
            }
        }
    }

    private fun approveVerification(remarks: String) {
        mListener?.showProgressDialog()
        val data = PropertyVerificationRequestData()
        data.documentRemarks = remarks
        data.allowDocumentVerification = pendingList?.allowDocumentVerification
        data.propertyId = pendingList?.propertyId
        data.verificationRequestId = pendingList?.propertyVerificationRequestId

        APICall.approvePropertyVerification(data, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                pendingList?.isDocumentVerified = true
                if (!pendingList?.isPhysicalVerified!!)
                    showPhysicalVerificationPopUp()
                else {
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    mListener?.popBackStack()
                }

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun rejectVerification(remarks: String) {
        mListener?.showProgressDialog()
        val data = PropertyVerificationRequestData()
        data.documentRemarks = remarks
        data.allowDocumentVerification = pendingList?.allowDocumentVerification
        data.propertyId = pendingList?.propertyId
        data.verificationRequestId = pendingList?.propertyVerificationRequestId

        APICall.rejectPropertyVerification(data, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                pendingList?.isDocumentVerified = true
                if (!pendingList?.isPhysicalVerified!!)
                    showPhysicalVerificationPopUp()
                else {
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    mListener?.popBackStack()
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun showPhysicalVerificationPopUp() {
        mListener?.showAlertDialog(R.string.msg_proceed_phy_verification,
                R.string.yes,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_IS_PHYSICAL_VERIFICATION_PENDING, true)
                    intent.putExtra(Constant.KEY_PENDING_PROPERTY_LIST, pendingList)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    mListener?.popBackStack()
                }, R.string.no,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    mListener?.popBackStack()
                })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.itemImageDocumentPreview -> {
                    val documentReferences: ArrayList<COMDocumentReference> = view.tag as ArrayList<COMDocumentReference>
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    documentReferences.remove(comDocumentReference)
                    documentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, documentReferences)
                    startActivity(intent)
                }
                R.id.itemPropertyImagePreview -> {
                    val images = view.tag as ArrayList<COMPropertyImage>
                    val image = obj as COMPropertyImage
                    val intent = Intent(context, PropertyImagesPreviewActivity::class.java)
                    images.remove(image)
                    images.add(0, image)
                    intent.putExtra(Constant.KEY_PROPERTY_IMAGE, images)
                    startActivity(intent)
                }
                R.id.itemPropertyPlanImagePreview -> {
                    val plans = view.tag as ArrayList<COMPropertyPlanImage>
                    val plan = obj as COMPropertyPlanImage
                    val intent = Intent(context, PropertyPlansPreviewActivity::class.java)
                    plans.remove(plan)
                    plans.add(0, plan)
                    intent.putExtra(Constant.KEY_PROPERTY_PLAN_IMAGE, plans)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    interface Listener {
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener?, view: View)
        fun showProgressDialog()
        fun showSnackbarMsg(msg: String)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun finish()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener?)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?, view: View?)
    }
}