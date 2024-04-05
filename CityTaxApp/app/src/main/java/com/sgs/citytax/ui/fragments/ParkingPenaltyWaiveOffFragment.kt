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
import android.widget.*
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.PenaltyWaiveOff
import com.sgs.citytax.databinding.FragmentParkingPenaltyWaiveOffBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ParkingPenalties
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.adapter.ParkingPenaltyWaiveOffAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.penality_waive_dialog.view.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class ParkingPenaltyWaiveOffFragment : BaseFragment(), View.OnClickListener, IClickListener {

    private lateinit var mBinding: FragmentParkingPenaltyWaiveOffBinding
    private var mListener: Listener? = null
    private var accountId = ""
    var pageIndex: Int = 1
    val pageSize: Int = 100
    private var mServiceRequest: ParkingPenalties? = null

    var rbPartial: RadioButton? = null
    var rbFull: RadioButton? = null
    var tvPenalty: TextView? = null
    var edtPercentage: EditText? = null
    var edtAmount: EditText? = null
    var edtRemarks: EditText? = null
    var imgDocument: ImageView? = null
    var btnClearImage: ImageButton? = null
    var llParentView: LinearLayout? = null
    var dialog: Dialog? = null
    var extension: String? = null
    var base64Data: String? = null
    private val REQUEST_IMAGE = 100
    private var mImageFilePath = ""
    private var mDocumentReference: COMDocumentReference? = null
//    var penaltyList: HashMap<Int, ArrayList<InvoicePenalties>>? = null
    var groupList: ArrayList<Int>? = null
    private var fromScreen: Constant.QuickMenu? = null
    var mParkingPenalties: ArrayList<ParkingPenalties>? = null
    var parkingPenaltyList: HashMap<Int, ArrayList<ParkingPenalties>>? = null

    companion object {
        @JvmStatic
        fun newInstance(item: ParkingPenalties) = ParkingPenaltyWaiveOffFragment().apply {
            mServiceRequest = item
        }
    }

    override fun initComponents() {
        arguments?.let {
            if (arguments?.getSerializable(Constant.KEY_ACCOUNT_ID) != null)
                accountId = arguments?.getString(Constant.KEY_ACCOUNT_ID)!!
            if (it.containsKey(Constant.KEY_PARKING_TAX_DETAILS))
                mParkingPenalties = arguments?.getParcelableArrayList(Constant.KEY_PARKING_TAX_DETAILS)
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
        }
        initViews()
        bindData()
    }

    private fun initViews() {
        mBinding.expandableListView.setAdapter(ParkingPenaltyWaiveOffAdapter(this))
    }

    private fun bindData() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_PARKING_PENALTY_WAIVE_OFF -> {

                mParkingPenalties?.let {
                    parkingPenaltyList = mParkingPenalties?.groupBy { it.taxInvoiceID!! } as HashMap<Int, ArrayList<ParkingPenalties>>
                    groupList = arrayListOf()

                    for (group in parkingPenaltyList!!) {
                        groupList?.add(group.key)
                    }

                    if (it.size > 0)
                        (mBinding.expandableListView.expandableListAdapter as ParkingPenaltyWaiveOffAdapter)
                                .set(groupList!!, parkingPenaltyList!!)
                    else (mBinding.expandableListView.expandableListAdapter as ParkingPenaltyWaiveOffAdapter)
                            .set(arrayListOf(), hashMapOf())

                    repeat(parkingPenaltyList!!.count()) {
                        mBinding.expandableListView.expandGroup(it, true)
                    }
                }

            }
        }
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_parking_penalty_waive_off, container, false)
        initComponents()
        return mBinding.root
    }

    interface Listener {
        fun showToast(message: String)
        fun showToast(message: Int)
        fun popBackStack()
        fun showProgressDialog(message: String)
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showToolbarBackButton(title: Int)
        fun finish()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)

    }

    private fun showWaiveOffDialog(invoicePenalties: ParkingPenalties, isChild: Boolean, _totalDue: BigDecimal = BigDecimal.ZERO) {
        if (dialog != null && dialog?.isShowing!!)
            return
        dialog = Dialog(requireContext(), R.style.AlertDialogTheme)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.penality_waive_dialog)
        llParentView = dialog?.findViewById(R.id.llParentView) as LinearLayout
        tvPenalty = dialog?.findViewById(R.id.tvPenalty) as TextView

        val totalDue = if (isChild) invoicePenalties.currentDue else _totalDue

        if (!isChild) {
            llParentView?.visibility = View.VISIBLE
            tvPenalty?.text = formatWithPrecision(totalDue)
        } else {
            llParentView?.visibility = View.GONE
            tvPenalty?.text = formatWithPrecision(totalDue)
        }

        edtPercentage = dialog?.findViewById(R.id.edtPercentage) as EditText
        edtAmount = dialog?.findViewById(R.id.edtAmount) as EditText

        var isAmountUpdating = false
        var isPercentageUpdating = false

        edtAmount?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (isAmountUpdating)
                        return

                    isPercentageUpdating = true
                    edtPercentage?.setText("")

                    if (s.isNotEmpty() && s.toString().toBigDecimal() > totalDue) {
                        mListener?.showToast(getString(R.string.place_holder_max_amount, formatWithPrecision(totalDue)))
                    }
                    isPercentageUpdating = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        edtPercentage?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (isPercentageUpdating)
                        return
                    isAmountUpdating = true
                    edtAmount?.setText("")
                    if (s.isNotEmpty() && s.toString() != "." && s.toString().toBigDecimal() > BigDecimal(100)) {
                        mListener?.showToast(getString(R.string.msg_max_percentage))
                    }
                    isAmountUpdating = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        edtRemarks = dialog?.findViewById(R.id.edtRemarks) as EditText
        imgDocument = dialog?.findViewById(R.id.ivImg) as ImageView

        val btnChoose = dialog?.findViewById(R.id.btnPhoto) as Button
        btnChoose.setOnClickListener {
            if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                requestForPermission(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
                return@setOnClickListener
            }
            openCameraIntent()
        }
        btnClearImage = dialog?.findViewById(R.id.btnClearImage) as ImageButton
        btnClearImage?.setOnClickListener {
            imgDocument?.setImageBitmap(null)
            btnClearImage?.visibility = View.GONE
            mDocumentReference?.data = null
            mDocumentReference?.extension = null
            mImageFilePath = ""
        }
        val btnClose = dialog?.findViewById(R.id.btnClose) as Button
        btnClose.setOnClickListener {
            dialog?.dismiss()
        }
        rbPartial = dialog?.findViewById(R.id.rbPartial) as RadioButton
        rbPartial?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isAmountUpdating = true
                isPercentageUpdating = true

                edtPercentage?.setText("")
                edtAmount?.setText("")
                edtPercentage?.isEnabled = true
                edtAmount?.isEnabled = true

                isAmountUpdating = false
                isPercentageUpdating = false
            }
        }

        rbFull = dialog?.findViewById(R.id.rbFull) as RadioButton
        rbFull?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isAmountUpdating = true
                isPercentageUpdating = true

                edtPercentage?.setText("")
                edtAmount?.setText("$totalDue")
                edtPercentage?.isEnabled = false
                edtAmount?.isEnabled = false

                isAmountUpdating = false
                isPercentageUpdating = false
            }
        }
        val btnAllWaiveOff = dialog?.findViewById(R.id.btnAllWaiveOff) as Button
        btnAllWaiveOff.setOnClickListener {
            if (validateView(isChild, totalDue)) {
                waiveOff(invoicePenalties, isChild, totalDue)
            }
        }
        dialog!!.show()
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
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }


    private fun validateView(isChild: Boolean, totalDue: BigDecimal?): Boolean {

        if (!isChild && (!rbPartial?.isChecked!! && !rbFull?.isChecked!!)) {
            Toast.makeText(requireContext(), getString(R.string.msg_waive_off_type), Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtPercentage?.text?.toString() == "." || (!isChild && (rbPartial?.isChecked!! && edtPercentage?.text?.isEmpty()!!) && edtAmount?.text?.isEmpty()!!)) {
            Toast.makeText(requireContext(), getString(R.string.msg_percentage_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isChild && rbPartial?.isChecked!! && !edtPercentage?.text?.isEmpty()!! && (edtPercentage?.text.toString().toBigDecimal() == BigDecimal(0))) {
            Toast.makeText(requireContext(), getString(R.string.msg_zero_percentage), Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isChild && rbPartial?.isChecked!! && !edtPercentage?.text?.isEmpty()!! && edtPercentage?.text.toString().toBigDecimal() > BigDecimal(100)) {
            Toast.makeText(requireContext(), getString(R.string.msg_max_percentage), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isChild && rbPartial?.isChecked!! && edtPercentage?.text?.isEmpty()!! && edtAmount?.text.toString().toBigDecimal() == BigDecimal(0)) {
            var doubleVal = getDecimalVal(resources.getString(R.string.msg_zero_amount))
            var errormsg=getTextWithPrecisionVal(resources.getString(R.string.msg_zero_amount), doubleVal)

            Toast.makeText(requireContext(), errormsg, Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isChild && rbPartial?.isChecked!! && edtPercentage?.text?.isEmpty()!! && edtAmount?.text.toString().toBigDecimal() > totalDue) {
            Toast.makeText(requireContext(), getString(R.string.place_holder_max_amount, totalDue), Toast.LENGTH_SHORT).show()
            return false
        }

        if (edtRemarks?.text?.toString()?.isNullOrEmpty()!!) {
            Toast.makeText(requireContext(), getString(R.string.msg_enter_remarks), Toast.LENGTH_SHORT).show()
            return false
        }
        if (mImageFilePath.isNullOrEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_take_picture_to_upload), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun waiveOff(invoicePenalties: ParkingPenalties, isChild: Boolean, totalDue: BigDecimal?) {
        mListener?.showProgressDialog(getString(R.string.msg_please_wait))

        val penaltyWaiveOff = PenaltyWaiveOff()
        if (isChild) {
            penaltyWaiveOff.taxInvoiceID = invoicePenalties.taxInvoiceID
            penaltyWaiveOff.penaltyID = invoicePenalties.penaltyID
            penaltyWaiveOff.penaltyAmount = invoicePenalties.currentDue
            penaltyWaiveOff.percentage = BigDecimal.ZERO
            penaltyWaiveOff.waiveOffAmount = BigDecimal.ZERO
        } else {
            if (rbPartial?.isChecked!!) {
                penaltyWaiveOff.taxInvoiceID = invoicePenalties.taxInvoiceID
                penaltyWaiveOff.penaltyID = 0
                penaltyWaiveOff.penaltyAmount = totalDue
                if (edtPercentage?.text?.isEmpty()!!) {
                    penaltyWaiveOff.percentage = BigDecimal.ZERO
                    penaltyWaiveOff.waiveOffAmount = edtAmount!!.text.toString().toBigDecimal()
                }
                if (edtAmount?.text?.isEmpty()!!) {
                    penaltyWaiveOff.percentage = edtPercentage!!.text.toString().toBigDecimal()
                    penaltyWaiveOff.waiveOffAmount = totalDue?.multiply(penaltyWaiveOff.percentage?.divide(BigDecimal(100)))
                }
            }
            if (rbFull?.isChecked!!) {
                penaltyWaiveOff.taxInvoiceID = invoicePenalties.taxInvoiceID
                penaltyWaiveOff.penaltyID = 0
                penaltyWaiveOff.penaltyAmount = totalDue
                penaltyWaiveOff.percentage = BigDecimal.ZERO
                penaltyWaiveOff.waiveOffAmount = edtAmount!!.text.toString().toBigDecimal()
            }
        }
        if (mImageFilePath.isNotEmpty()) {
            penaltyWaiveOff.filenameWithExt = extension
            penaltyWaiveOff.fileData = base64Data
        }

        penaltyWaiveOff.remarks = edtRemarks?.text?.toString()

        APICall.penaltyWaiveOff(penaltyWaiveOff, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                dialog?.dismiss()
                mListener?.dismissDialog()
                mListener?.showToast(R.string.waive_off_successful)
                bindData()
                navigateToPreviewScreen(response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToPreviewScreen(advanceReceivedId: Int) {
        val intent = Intent(activity, AllTaxNoticesActivity::class.java)
        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedId)
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.PENALTY_WAIVE_OFF.Code)
        startActivity(intent)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.btnGroupWaiveOff -> {
                    val invoicePenalties = obj as ParkingPenalties
                    var totalDue = BigDecimal.ZERO
                    for (item in parkingPenaltyList?.get(groupList?.get(position))!!) {
                        totalDue = totalDue + item.currentDue!!
                    }
                    showWaiveOffDialog(invoicePenalties, false, totalDue)
                }
                R.id.btnChildWaiveOff -> {
                    val invoicePenalties = obj as ParkingPenalties
                    showWaiveOffDialog(invoicePenalties, true)
                }
                else -> {
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    override fun onClick(v: View?) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_CAMERA) {
            if (isPermissionGranted(grantResults))
                openCameraIntent()
            else
                mListener!!.showAlertDialog(getString(R.string.msg_permission_storage_camera))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                imgDocument?.setImageURI(Uri.parse(mImageFilePath))
                btnClearImage?.btnClearImage?.visibility = View.VISIBLE
                base64Data = ImageHelper.getBase64String(ImageHelper.decodeFile(File(mImageFilePath)))
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                extension = "$timeStamp.jpg"
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener!!.showSnackbarMsg(getString(R.string.cancelled))
            }
        }
    }

}