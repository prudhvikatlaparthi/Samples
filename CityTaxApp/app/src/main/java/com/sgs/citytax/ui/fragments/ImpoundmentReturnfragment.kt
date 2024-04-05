package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.databinding.FragmentImpoundmentReturnBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.DocumentReferenceImpoundReturn
import com.sgs.citytax.model.ImpoundReturnLines
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.adapter.ReturnImpoundmentListAdapter
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImpoundmentReturnfragment : BaseFragment(), View.OnClickListener, ReturnImpoundmentListAdapter.Listener {
    private lateinit var mBinding: FragmentImpoundmentReturnBinding
    private var mListener: Listener? = null
    private var mImpondmentReturn: ImpondmentReturn? = null
    private var mImpondmentDetails: ImpondmentDetails? = null
    private var fromScreen: Any? = null
    private val REQUEST_IMAGE = 100
    private var mImageFilePath = ""

    private var isDataSourceChanged = false
    private var mDocumentReference: COMDocumentReference? = null

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_impoundment_return, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            mImpondmentReturn = it.getParcelable(Constant.KEY_IMPOUNDMENT_RETURN)
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

        }
        setViews()
        bindData()
    }

    private fun setViews() {
        mBinding.btnCancel.setOnClickListener(this)
        mBinding.btnProceed.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.btnChoose.setOnClickListener(this)
        mBinding.btnClearImage.setOnClickListener(this)

    }

    private fun bindData() {
        val getImpoundmentDetails = GetImpondmentDetails()
        mImpondmentReturn?.let {
            mBinding.tvImpoundment.text = it.impoundmentType
            if (it.applicableOnVehicle == "Y") {
                mBinding.llVoucherno.visibility = View.VISIBLE
                mBinding.tvVehicleNumber.text = it.vehicleNo
                mBinding.tvOwner.text = it.vehicleOwner
                mBinding.tvPhoneNumber.text = it.vehicleOwnerMobile

            } else {
                mBinding.llVoucherno.visibility = View.GONE
                mBinding.tvVehicleNumber.text = ""
                mBinding.tvOwner.text = it.goodsOwner
                mBinding.tvPhoneNumber.text = it.goodsOwnerMobile
            }
            mBinding.tvTotalDue.text = formatWithPrecision(it.currentDue)

            getImpoundmentDetails.id = it.invoiceTransactionVoucherNo.toString()


        }
        mListener?.showProgressDialog()
        APICall.getImpondmentDetails(getImpoundmentDetails, object : ConnectionCallBack<GetImpondmentDetailsResponse> {
            override fun onSuccess(response: GetImpondmentDetailsResponse)
            {
                mListener?.dismissDialog()
                mImpondmentDetails = response.impoundment
                mImpondmentDetails?.let {
                    setData(it)
                }

                fetchChildEntriesCount()
            }
            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                fetchChildEntriesCount()

            }
        })

    }

    private fun setData(impoundment: ImpondmentDetails)
    {
        val calender = Calendar.getInstance(Locale.getDefault())

        impoundment.let {
            mBinding.tvImpounmentDate.text = formatDisplayDateTimeInMillisecond(it.impoundmentDate)
            mBinding.tvImpounmentType.text = it.impoundmentType
            mBinding.tvImpounmentSubType.text = it.impoundmentSubType
            mBinding.tvImpounmentReason.text = it.impoundmentReason
            mBinding.tvImpounmentRemarks.text = it.remarks
            mBinding.tvPoliceStation.text = it.policeStation
            mBinding.tvInvoiceNo.text = it.taxInvoiceID.toString()
            mBinding.tvReturnDate.text = formatDisplayDateTimeInMillisecond(calender.time)
        }

        if (impoundment.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code)
        {
            mBinding.llVoucherno.visibility = View.GONE
            mBinding.llOwnerHead.visibility = View.GONE
            mBinding.llPhoneNumber.visibility = View.GONE
            mBinding.llImpoundQty.visibility = View.VISIBLE
            mBinding.llReturnQty.visibility = View.VISIBLE
            mBinding.llReturnAmountPaid.visibility = View.VISIBLE
            mBinding.tvImpoundqty.text = getQuantity(impoundment.quantity.toString())
            if (impoundment.pendingReturnQuantity == null)
            mBinding.tvReturnQty.text = "0.0"
            else
            mBinding.tvReturnQty.text = getQuantity(impoundment.pendingReturnQuantity.toString())
            mBinding.tvReturnAmountPaid.text = formatWithPrecision(impoundment.pendingReturnAmount.toString())
        }


    }
    private fun fetchChildEntriesCount() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "LAW_Impoundments"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mImpondmentReturn?.invoiceTransactionVoucherNo ?: 0}"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn, "AND", "COM_DocumentReferences", "DocumentReferenceID")
    }
    private fun fetchCount(filterColumns: List<FilterColumn>, tableCondition: String, tableOrViewName: String, primaryKeyColumnName: String) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = tableOrViewName
        tableDetails.primaryKeyColumnName = primaryKeyColumnName
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = tableCondition
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                bindCounts(tableOrViewName, 0)
            }

            override fun onSuccess(response: Int) {
                bindCounts(tableOrViewName, response)
            }
        })
    }

    private fun bindCounts(tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "COM_DocumentReferences" -> {
                mBinding.txtNumberOfDocuments.text = "$count"
            }

        }
    }


    interface Listener {
        fun popBackStack()
        fun finish()
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }
    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.btnProceed ->
            {
                if (mImpondmentReturn?.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code)
                {
                    if( mBinding.tvReturnQty.text.toString()==null || mBinding.tvReturnQty.text.toString()=="0.0"||mBinding.tvReturnQty.text.toString()=="0"|| mBinding.tvReturnQty.text.toString()==""){
                        mListener!!.showSnackbarMsg(getString(R.string.msg_return_qty_greater_than_zero))
                    }
                    else{
                        saveData()
                    }
                }else {
                    saveData()
                }
            }
            R.id.btnCancel ->
            {
                mListener?.popBackStack()
            }
            R.id.btnChoose ->
            {
                // region Storage Permission
                if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                    requestForPermission(arrayOf(Manifest.permission.CAMERA), Constant.REQUEST_CODE_CAMERA)
                    return
                }
                // endregion
                openCameraIntent()
            }
            R.id.btnClearImage -> {
                mBinding.imgDocument.setImageBitmap(null)
                mDocumentReference?.data = null
                mDocumentReference?.awsfile = null
                mBinding.btnClearImage.visibility = View.GONE
            }

           R.id.llDocuments ->
            {

                val fragment = DocumentsMasterFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, mImpondmentReturn?.invoiceTransactionVoucherNo ?: 0)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                mListener?.showToolbarBackButton(R.string.documents)
                mListener?.addFragment(fragment, true)
            }

        }

    }

    private fun validateData(): Boolean {
        if (mImageFilePath.isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.image))
            return false
        }
        if (mBinding.ownerSignatureView.isEmpty) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.owner_signature))
            return false
        }
        if (mBinding.customerSignatureView.isEmpty) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.customer_signature))
            return false
        }
        return true
    }

    private fun saveData() {
        if (validateData()) {
            val mdata = InsertImpondmentDetails()
            mdata.id =  mImpondmentReturn?.invoiceTransactionVoucherNo.toString()
            val documentValues = DocumentReferenceImpoundReturn()
            documentValues.returnRemarks = mBinding.edtDescription.text.toString()
            documentValues.handoverimage = ImageHelper.getBase64String(ImageHelper.decodeFile(File(mImageFilePath)))
            documentValues.handoverimagefn = "handoverimg.jpeg"
            documentValues.ownersignatureimg = ImageHelper.getBase64String(mBinding.ownerSignatureView.signatureBitmap)
            documentValues.ownersignatureidfn = "ownersign.jpeg"
            documentValues.returnagentsignatureimg = ImageHelper.getBase64String(mBinding.customerSignatureView.signatureBitmap)
            documentValues.returnagentsignaturefn = "customersign.jpeg"
            mdata.data = documentValues
            if (mImpondmentReturn?.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code)
            {
                val impoundReturnLines = ImpoundReturnLines()
                impoundReturnLines.impoundmentID = mImpondmentDetails?.impoundmentID
                impoundReturnLines.quantity = mImpondmentDetails?.pendingReturnQuantity
                mdata.impoundReturnLines = impoundReturnLines
            }
            else
            {
                mdata.impoundReturnLines = null
            }
            mListener?.showProgressDialog()
            APICall.insertImpondmentDetails(mdata, object :  ConnectionCallBack<ArrayList<ImpoundmentReturnResponse>>{
                override fun onSuccess(response: ArrayList<ImpoundmentReturnResponse>)
                {
                    Log.e("response","##"+response)
                    mListener?.dismissDialog()
                    mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                    Handler().postDelayed({

                        navigateToReceiptScreen(response)
                    }, 500)

                }

                override fun onFailure(message: String)
                {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }

            })
        }
    }

    private fun navigateToReceiptScreen(response: ArrayList<ImpoundmentReturnResponse>) {
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        val list:ArrayList<GenerateTaxNoticeResponse> = arrayListOf()
        if (mImpondmentReturn?.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code) {
            for (item in response) {
                val generateTaxNoticeResponse = GenerateTaxNoticeResponse()
                generateTaxNoticeResponse.taxNoticeID = item.impoundmentID
                generateTaxNoticeResponse.returnLineID = item.returnLineID
                generateTaxNoticeResponse.taxRuleBookCode = Constant.TaxRuleBook.IMP_RETURN_ANIMAL.Code
                list.add(generateTaxNoticeResponse)
            }
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
            intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, list)
        }
        else {
        val generateTaxNoticeResponse = GenerateTaxNoticeResponse()
        generateTaxNoticeResponse.taxNoticeID = response.get(0).impoundmentID
        generateTaxNoticeResponse.taxRuleBookCode = Constant.TaxRuleBook.IMP_RETURN.Code
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, arrayListOf(generateTaxNoticeResponse))
        }
        startActivity(intent)
        activity?.finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mListener?.showToolbarBackButton(R.string.title_return_impondment)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                isDataSourceChanged = true
                mBinding.imgDocument.setImageURI(Uri.parse(mImageFilePath))
                mBinding.btnClearImage.visibility = View.VISIBLE
                mDocumentReference?.data = ImageHelper.getBase64String(ImageHelper.decodeFile(File(mImageFilePath)))
                mDocumentReference?.extension = "jpg"
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener!!.showSnackbarMsg(getString(R.string.cancelled))
            }
        }
        else
            fetchChildEntriesCount()
    }

    override fun onItemClick(list: ImpondmentReturn, position: Int) {
    }


}