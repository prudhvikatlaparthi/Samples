package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.maps.android.SphericalUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetIndividualTaxInitOutstandingPenalties
import com.sgs.citytax.api.payload.InitialOutstandingWaiveOff
import com.sgs.citytax.api.response.GetOutstandingWaiveOffResponse
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.databinding.FragmentOutstandingWaiveoffBinding
import com.sgs.citytax.databinding.PenalityWaiveDialogBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.GetOutstandingWaiveOff
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.adapter.OutstandingWaiveOffAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.penality_waive_dialog.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import kotlin.math.truncate

class OutstandingWaiveOffFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentOutstandingWaiveoffBinding
    private lateinit var binding: PenalityWaiveDialogBinding
    private var listener: Listener? = null
    var dialog: Dialog? = null
    var extension: String? = null
    var base64Data: String? = null
    private val REQUEST_IMAGE = 100
    private var mImageFilePath = ""
    private var mDocumentReference: COMDocumentReference? = null
    private var accountId: Int? = 0
    private var getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails? = null
    private var property: VuComProperties? = null
    private var fromScreen: Constant.QuickMenu? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun initComponents() {
        arguments?.let {
            if (arguments?.getSerializable(Constant.KEY_ACCOUNT_ID) != null)
                accountId = arguments?.getInt(Constant.KEY_ACCOUNT_ID, 0)!!
            arguments?.let {
                if (it.containsKey(Constant.KEY_INDIVIDUAL_TAX_DETAILS))
                    getSearchIndividualTaxDetails = arguments?.getParcelable(Constant.KEY_INDIVIDUAL_TAX_DETAILS)
                if (it.containsKey(Constant.KEY_QUICK_MENU))
                    fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?

                if (it.containsKey(Constant.KEY_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF))
                    property = it.getParcelable(Constant.KEY_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF)
            }
        }
        setViews()
        bindData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_outstanding_waiveoff, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    private fun setViews() {
        mBinding.rcvInitialOutstandingWaiveOff.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
    }

    fun bindData() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF -> {
                listener?.showProgressDialog()
                val getIndividualTaxInitOutstandingPenalties = GetIndividualTaxInitOutstandingPenalties()
                getIndividualTaxInitOutstandingPenalties.accountID = getSearchIndividualTaxDetails?.accountID?.toInt()
                getIndividualTaxInitOutstandingPenalties.productCode = getSearchIndividualTaxDetails?.productCode
                getIndividualTaxInitOutstandingPenalties.voucherNo = getSearchIndividualTaxDetails?.voucherNo?.toInt()
                APICall.individualTaxInitOutstandingPenalties(getIndividualTaxInitOutstandingPenalties, object : ConnectionCallBack<GetOutstandingWaiveOffResponse> {
                    override fun onSuccess(response: GetOutstandingWaiveOffResponse) {
                        listener?.dismissDialog()
                        response.outstandingWaiveOff.let {
                            val outstandingWaiveofflist: ArrayList<GetOutstandingWaiveOff> = arrayListOf()
                            for (outstandingWaiveOff in response.outstandingWaiveOff) {
                                if (outstandingWaiveOff.currentDue!! > BigDecimal.ZERO)
                                    outstandingWaiveofflist.add(outstandingWaiveOff)
                            }
                            mBinding.rcvInitialOutstandingWaiveOff.adapter = OutstandingWaiveOffAdapter(outstandingWaiveofflist, this@OutstandingWaiveOffFragment, fromScreen)
                        }
                    }

                    override fun onFailure(message: String) {
                        mBinding.rcvInitialOutstandingWaiveOff.adapter = null
                        listener?.dismissDialog()
                        if (message.isNotEmpty())
                            listener?.showAlertDialog(message)
                    }
                })
            }
            Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF -> {
                listener?.showProgressDialog()
                APICall.getPropertyTaxInitialOutstandings(property?.propertyID?:0, property?.productCode
                        ?: "", object : ConnectionCallBack<GetOutstandingWaiveOffResponse> {
                    override fun onSuccess(response: GetOutstandingWaiveOffResponse) {
                        listener?.dismissDialog()
                        response.outstandingWaiveOff.let {
                            val outstandingWaiveofflist: ArrayList<GetOutstandingWaiveOff> = arrayListOf()
                            for (outstandingWaiveOff in response.outstandingWaiveOff) {
                                if (outstandingWaiveOff.currentDue!! > BigDecimal.ZERO)
                                    outstandingWaiveofflist.add(outstandingWaiveOff)
                            }
                            mBinding.rcvInitialOutstandingWaiveOff.adapter = OutstandingWaiveOffAdapter(outstandingWaiveofflist, this@OutstandingWaiveOffFragment, fromScreen)

                        }
                    }

                    override fun onFailure(message: String) {
                        listener?.dismissDialog()
                        listener?.showAlertDialog(message)
                    }
                })
            }
            Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF -> {
                listener?.showProgressDialog()
                APICall.getPropertyTaxInitialOutstandings(property?.propertyID?:0, property?.productCode
                        ?: "", object : ConnectionCallBack<GetOutstandingWaiveOffResponse> {
                    override fun onSuccess(response: GetOutstandingWaiveOffResponse) {
                        listener?.dismissDialog()
                        response.outstandingWaiveOff.let {
                            val outstandingWaiveofflist: ArrayList<GetOutstandingWaiveOff> = arrayListOf()
                            for (outstandingWaiveOff in response.outstandingWaiveOff) {
                                if (outstandingWaiveOff.currentDue!! > BigDecimal.ZERO)
                                    outstandingWaiveofflist.add(outstandingWaiveOff)
                            }
                            mBinding.rcvInitialOutstandingWaiveOff.adapter = OutstandingWaiveOffAdapter(outstandingWaiveofflist, this@OutstandingWaiveOffFragment, fromScreen)

                        }
                    }

                    override fun onFailure(message: String) {
                        listener?.dismissDialog()
                        listener?.showAlertDialog(message)
                    }
                })
            }
            else -> {
                listener?.showProgressDialog()
                APICall.getInitialOutstandingPenalties(accountId!!, object : ConnectionCallBack<GetOutstandingWaiveOffResponse> {
                    override fun onSuccess(response: GetOutstandingWaiveOffResponse) {
                        listener?.dismissDialog()
                        response.outstandingWaiveOff.let {
                            val outstandingWaiveofflist: ArrayList<GetOutstandingWaiveOff> = arrayListOf()
                            for (outstandingWaiveOff in response.outstandingWaiveOff) {
                                if (outstandingWaiveOff.currentDue!! > BigDecimal.ZERO)
                                    outstandingWaiveofflist.add(outstandingWaiveOff)
                            }
                            mBinding.rcvInitialOutstandingWaiveOff.adapter = OutstandingWaiveOffAdapter(outstandingWaiveofflist, this@OutstandingWaiveOffFragment, fromScreen)
                        }
                    }

                    override fun onFailure(message: String) {
                        mBinding.rcvInitialOutstandingWaiveOff.adapter = null
                        listener?.dismissDialog()
                        if (message.isNotEmpty())
                            listener?.showAlertDialog(message)
                    }
                })
            }
        }
    }


    override fun onClick(view: View, position: Int, obj: Any) {
        showWaiveOffDialog(obj as GetOutstandingWaiveOff)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun showWaiveOffDialog(getOutstandingWaiveOff: GetOutstandingWaiveOff) {
        if (dialog != null && dialog?.isShowing!!)
            return

        val layoutInflater = LayoutInflater.from(context)
        var isAmountUpdating = false
        var isPercentageUpdating = false

        dialog = Dialog(requireContext(), R.style.AlertDialogTheme)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.setCancelable(false)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.penality_waive_dialog, llWaiveDialog, false)
        dialog?.setContentView(binding.root)
        binding.llParentView.visibility = View.VISIBLE
        binding.tvPenalty.text = formatWithPrecision(getOutstandingWaiveOff.currentDue)
        binding.txtpenalityAmount.text = context?.resources?.getString(R.string.outstanding_penalty_due_waive_off)

        binding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (isAmountUpdating)
                        return

                    isPercentageUpdating = true
                    binding.edtPercentage.setText("")
                    if (s.isNotEmpty() && s.toString().toBigDecimal() > getOutstandingWaiveOff.currentDue) {
                        listener?.showToast(getString(R.string.place_holder_max_amount, formatWithPrecision(getOutstandingWaiveOff.currentDue)))
                    }
                    isPercentageUpdating = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.edtPercentage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (isPercentageUpdating)
                        return

                    isAmountUpdating = true
                    binding.edtAmount.setText("")
                    if (s.isNotEmpty() && s.toString() != "." && s.toString().toBigDecimal() > BigDecimal(100)) {
                        listener?.showToast(getString(R.string.msg_max_percentage))
                    }
                    isAmountUpdating = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.btnPhoto.setOnClickListener {
            if (hasPermission(requireActivity(), Manifest.permission.CAMERA))
                openCameraIntent()
            else
                requestPermissions(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
        }

        binding.btnClearImage.setOnClickListener {
            binding.ivImg.setImageBitmap(null)
            binding.btnClearImage.visibility = View.GONE
            mDocumentReference?.data = null
            mDocumentReference?.extension = null
            mImageFilePath = ""
        }
        binding.btnClose.setOnClickListener {
            dialog?.dismiss()
        }

        binding.rbPartial.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isAmountUpdating = true
                isPercentageUpdating = true

                binding.edtPercentage.setText("")
                binding.edtAmount.setText("")
                val totalDue = getOutstandingWaiveOff.currentDue
                binding.tvPenalty.text = "$totalDue"
                binding.edtPercentage.isEnabled = true
                binding.edtAmount.isEnabled = true

                isAmountUpdating = false
                isPercentageUpdating = false
            }
        }

        binding.rbFull.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isAmountUpdating = true
                isPercentageUpdating = true
                val totalDue = getOutstandingWaiveOff.currentDue
                binding.edtPercentage.setText("")
                binding.edtAmount.setText("$totalDue")
                binding.tvPenalty.text = "$totalDue"
                binding.edtPercentage.isEnabled = false
                binding.edtAmount.isEnabled = false

                isAmountUpdating = false
                isPercentageUpdating = false
            }
        }
        binding.btnAllWaiveOff.setOnClickListener {
            if (validateView(getOutstandingWaiveOff.currentDue)) {
                waiveOff(getOutstandingWaiveOff, getOutstandingWaiveOff.currentDue)
            }
        }
        dialog?.show()
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File?
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                LogHelper.writeLog(exception = e)
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(requireContext(), context?.packageName.toString() + ".provider", photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(pictureIntent, REQUEST_IMAGE)
        }
    }


    private fun createImageFile(): File {
        val imageFileName = "IMG_" + formatDateTimeSecondFormat(Date()) + "_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }


    private fun validateView(totalDue: BigDecimal?): Boolean {

        if (!binding.rbPartial.isChecked && !binding.rbFull.isChecked) {
            Toast.makeText(requireContext(), getString(R.string.msg_waive_off_type), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtPercentage.text?.toString() == "." || (binding.rbPartial.isChecked && binding.edtPercentage.text?.isEmpty()!! && binding.edtAmount.text?.isEmpty()!!)) {
            Toast.makeText(requireContext(), getString(R.string.msg_percentage_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.rbPartial.isChecked && !binding.edtPercentage.text?.isEmpty()!! && (binding.edtPercentage.text.toString().toBigDecimal() == BigDecimal(0))) {
            Toast.makeText(requireContext(), getString(R.string.msg_zero_percentage), Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.rbPartial.isChecked && !binding.edtPercentage.text?.isEmpty()!! && binding.edtPercentage.text.toString().toBigDecimal() > BigDecimal(100)) {
            Toast.makeText(requireContext(), getString(R.string.msg_max_percentage), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.rbPartial.isChecked && binding.edtPercentage.text?.isEmpty()!! && binding.edtAmount.text.toString().toBigDecimal() == BigDecimal(0)) {
            var doubleVal = getDecimalVal(resources.getString(R.string.msg_zero_amount))
            var errormsg=getTextWithPrecisionVal(resources.getString(R.string.msg_zero_amount), doubleVal)

            Toast.makeText(requireContext(), errormsg, Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.rbPartial.isChecked && binding.edtPercentage.text?.isEmpty()!! && binding.edtAmount.text.toString().toBigDecimal() > totalDue) {
            Toast.makeText(requireContext(), getString(R.string.place_holder_max_amount, totalDue), Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.edtRemarks.text?.toString().isNullOrEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_enter_remarks), Toast.LENGTH_SHORT).show()
            return false
        }
        if (mImageFilePath.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_take_picture_to_upload), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun waiveOff(getOutstandingWaiveOff: GetOutstandingWaiveOff, totalDue: BigDecimal?) {
        listener?.showProgressDialog(getString(R.string.msg_please_wait))

        val initialOutstandingWaiveOff = InitialOutstandingWaiveOff()
        if (binding.rbPartial.isChecked) {
            initialOutstandingWaiveOff.initialOutstandingID = getOutstandingWaiveOff.initialOutstandingID
            if (binding.edtPercentage.text?.isEmpty()!!) {
                initialOutstandingWaiveOff.percentage = BigDecimal.ZERO
                initialOutstandingWaiveOff.waiveOffAmt = binding.edtAmount.text.toString().toBigDecimal()
            }
            if (binding.edtAmount.text?.isEmpty()!!) {
                initialOutstandingWaiveOff.percentage = binding.edtPercentage.text.toString().toBigDecimal()
                var value =  totalDue?.multiply(initialOutstandingWaiveOff.percentage?.divide(BigDecimal(100)))
//                initialOutstandingWaiveOff.waiveOffAmt = totalDue?.multiply(initialOutstandingWaiveOff.percentage?.divide(BigDecimal(100)))
                initialOutstandingWaiveOff.waiveOffAmt = value?.let { truncateTo(it) }
            }
        }
        if (binding.rbFull.isChecked) {
            initialOutstandingWaiveOff.initialOutstandingID = getOutstandingWaiveOff.initialOutstandingID
            initialOutstandingWaiveOff.percentage = BigDecimal.ZERO
            initialOutstandingWaiveOff.waiveOffAmt = binding.edtAmount.text.toString().toBigDecimal()
        }

        initialOutstandingWaiveOff.remarks = binding.edtRemarks.text?.toString()

        if (mImageFilePath.isNotEmpty()) {
            initialOutstandingWaiveOff.fileNameWithExtension = extension
            initialOutstandingWaiveOff.fileData = base64Data
        }

        APICall.getInitialOutstandingWaiveOff(initialOutstandingWaiveOff, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                dialog?.dismiss()
                listener?.dismissDialog()
                bindData()
                listener?.showToast(R.string.waive_off_successful)
                navigateToPreviewScreen(response)
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
                listener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToPreviewScreen(advanceReceivedId: Int) {
        val intent = Intent(activity, AllTaxNoticesActivity::class.java)
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedId)
        intent.putExtra(Constant.KEY_QUICK_MENU,fromScreen)
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.OUTSTANDING_WAIVE_OFF.Code)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                openCameraIntent()
            else
                listener?.showAlertDialog(getString(R.string.msg_permission_storage_camera))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                binding.ivImg.setImageURI(Uri.parse(mImageFilePath))
                binding.btnClearImage.visibility = View.VISIBLE
                base64Data = ImageHelper.getBase64String(ImageHelper.decodeFile(File(mImageFilePath)))
                extension = "${formatDateTimeSecondFormat(Date())}.jpg"
            } else if (resultCode == Activity.RESULT_CANCELED) {
                listener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        }
    }

    interface Listener {
        fun showToast(message: String)
        fun showToast(message: Int)
        fun popBackStack()
        fun showProgressDialog(message: String)
        fun showProgressDialog()
        fun dismissDialog()
        fun finish()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: String?)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

}