package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.FragmentAdvertisementEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.TaxNoticeCaptureActivity
import com.sgs.citytax.ui.adapter.AdvertisementImageAdapter
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AdvertisementEntryFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentAdvertisementEntryBinding
    private var mListener: Listener? = null
    private var mVUCrmAdvertisements: VUCRMAdvertisements? = null
    private var mCrmAdvertisementTypes: MutableList<CRMAdvertisementTypes>? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var documentList: ArrayList<COMDocumentReference> = arrayListOf()
    private var mTaskCode: String? = ""
    private var isAdvertisementTypeChanged: Boolean = false
    private var measurementUnits: List<VUINVMeasurementUnits> = arrayListOf()
    private var calValue = 0.0
    private var calTaxMatter = 0.0
    private var minArea = 0.0
    private var maxArea = 0.0
    private var unitCode = ""
    private var mImageFilePath = ""
    private var mTaxRuleBookCode:String?=""



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_advertisement_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mVUCrmAdvertisements = arguments?.getParcelable(Constant.KEY_ADVERTISEMENTS)
            mTaskCode = it.getString(Constant.KEY_TASK_CODE)
            mTaxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
        }

        setViews()
        bindSpinners()
        //fetchChildEntriesCount()
        setListeners()
        setTextListeners()
    }

    private fun setTextListeners() {

        mBinding.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()) {
                        var quantity = s.toString().toDouble()
                        var length = 0.0
                        var width = 0.0
                        if (!TextUtils.isEmpty(mBinding.edtLength.text.toString()))
                            length = currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble()
                        if (!TextUtils.isEmpty(mBinding.edtWidth.text.toString()))
                            width = currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble()

                        calTaxMatter = length * width * quantity
                        calValue = length * width
                        if(mBinding.llLength.isVisible && mBinding.llWidth.isVisible){
                            mBinding.edtTaxableMatter.setText(formatWithPrecisionCustomDecimals(calTaxMatter.toString(), false, 3))
                        }else
                            mBinding.edtTaxableMatter.setText(formatWithPrecisionCustomDecimals(quantity.toString(), false, 3))
                    } else
                        mBinding.edtTaxableMatter.setText("")

                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        mBinding.edtLength.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()) {
                        var quantity = 0.0
                        var length = 0.0
                        if (s.contains(","))
                            length = currencyToDouble(s.toString().trim())!!.toDouble()
                        else if (s.toString().substring(0, 1) != ".") {
                            length = s.toString().trim().toDouble()
                        }
                        var width = 0.0
                        if (!TextUtils.isEmpty(mBinding.edtQuantity.text.toString()))
                            quantity = mBinding.edtQuantity.text.toString().toDouble()
                        if (!TextUtils.isEmpty(mBinding.edtWidth.text.toString()))
                            width = currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble()

                        calTaxMatter = length * width * quantity
                        calValue = length * width
                        mBinding.edtTaxableMatter.setText(formatWithPrecisionCustomDecimals(calTaxMatter.toString(), false, 3))

                        /*mBinding.edtLength.setText("")
                        mBinding.edtLength.setText(formatWithPrecision(length, false))*/

                    } else
                        mBinding.edtTaxableMatter.setText("")
                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        /*mBinding.edtLength.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text: String = mBinding.edtLength.text.toString()
                if (text?.isNotEmpty()!!) {
                    mBinding.edtLength.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
                    mBinding.edtLength.setText("${currencyToDouble(text)}");
                }
            } else {
                //this if condition is true when edittext lost focus...
                //check here for number is larger than 10 or not
                var text = mBinding.edtLength.text.toString();
                if (!TextUtils.isEmpty(text)) {
                    val enteredText: Double = formatNumber(currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble())!!.toDouble()
                    mBinding.edtLength.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7 + 15))
                    mBinding.edtLength.setText("${formatWithPrecisionCustomDecimals(text, false, 3)}")
                }
            }
        }*/

        mBinding.edtWidth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (s.isNotEmpty()) {
                        var quantity = 0.0
                        var length = 0.0
                        var width = 0.0
                        if (s.contains(","))
                            width = currencyToDouble(s.toString().trim())!!.toDouble()
                        else if (s.toString().substring(0, 1) != ".") {
                            width = s.toString().trim().toDouble()
                        }

                        if (!TextUtils.isEmpty(mBinding.edtQuantity.text.toString()))
                            quantity = mBinding.edtQuantity.text.toString().toDouble()
                        if (!TextUtils.isEmpty(mBinding.edtLength.text.toString()))
                            length = currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble()

                        calTaxMatter = length * width * quantity
                        calValue = length * width
                        mBinding.edtTaxableMatter.setText(formatWithPrecisionCustomDecimals(calTaxMatter.toString(), false, 3))
                    } else
                        mBinding.edtTaxableMatter.setText("")
                }
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun edtRemoveFocus(focus: Boolean = false) {

        mBinding.edtLength.isFocusable = focus
        mBinding.edtWidth.isFocusable = focus

        mBinding.edtLength.isFocusableInTouchMode = true
        mBinding.edtWidth.isFocusableInTouchMode = true

    }

    private fun setViews() {
        mBinding.edtStartDate.setMaxDate(Calendar.getInstance().timeInMillis)
        mBinding.edtStartDate.setDisplayDateFormat(displayDateFormat)

        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
            Constant.ScreenMode.ADD -> if(mVUCrmAdvertisements!=null && mVUCrmAdvertisements?.allowDelete=="N")setEditAction(false)
        }
    }

    private fun setEditActionForSave(action: Boolean) {
        mBinding.spnAdvertisementType.isEnabled = action
        mBinding.edtQuantity.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.addImageButton.isEnabled = action
        mBinding.edtStartDate.isEnabled = action
        mBinding.edtLength.isEnabled = action
        mBinding.edtWidth.isEnabled = action
        mBinding.btnGet.isEnabled = action
        mBinding.chkActive.isEnabled = true
        mBinding.btnSave.visibility = View.VISIBLE

    }

    private fun setEditAction(action: Boolean) {
        mBinding.spnAdvertisementType.isEnabled = action
        mBinding.edtQuantity.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.chkActive.isEnabled = action
        mBinding.addImageButton.isEnabled = action
        mBinding.edtStartDate.isEnabled = action

        mBinding.edtLength.isEnabled = action
        mBinding.edtWidth.isEnabled = action

        mBinding.btnGet.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
        } else {
            mBinding.btnSave.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_IMAGE_CAPTURE) {
            //val photo = data?.extras?.get("data") as Bitmap
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val photo = BitmapFactory.decodeFile(mImageFilePath, options)
            val comDocumentReference = COMDocumentReference()
            comDocumentReference.documentName = System.currentTimeMillis().toString()
            comDocumentReference.extension = "jpeg"
            comDocumentReference.data = ImageHelper.getBase64String(photo,80)
            comDocumentReference.documentProofType = ""
            comDocumentReference.verified = "N"
            documentList.add(comDocumentReference)
            mBinding.rcvImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            updateAdapter(documentList)
        }
        bindCounts()
    }

    private fun bindSpinners() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_Advertisements", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()
                mCrmAdvertisementTypes = response.crmAdvertisementTypes
                measurementUnits = response.measurementUnits

                if (mCrmAdvertisementTypes != null && mCrmAdvertisementTypes!!.isNotEmpty()) {
                    mCrmAdvertisementTypes?.add(0, CRMAdvertisementTypes(-1, getString(R.string.select), ""))
                    mBinding.spnAdvertisementType.adapter = ArrayAdapter<CRMAdvertisementTypes>(activity!!, android.R.layout.simple_spinner_dropdown_item, mCrmAdvertisementTypes!!)
                } else
                    mBinding.spnAdvertisementType.adapter = null

                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindData() {
        mBinding.txtNumberOfDocuments.text = "0"
        mBinding.edtStartDate.setText(displayFormatDate(getCurrentYearStartDate()))
        if (mVUCrmAdvertisements != null) {
            mBinding.edtQuantity.setText(mVUCrmAdvertisements!!.quantity.toString())
            if (!mVUCrmAdvertisements!!.description.isNullOrEmpty())
                mBinding.edtDescription.setText(mVUCrmAdvertisements!!.description)
            mVUCrmAdvertisements?.estimatedTax?.let {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(it))
            }

            if (!mVUCrmAdvertisements?.startDate.isNullOrEmpty()) {
                mBinding.edtStartDate.setText(displayFormatDate(mVUCrmAdvertisements?.startDate))
            }

            mBinding.chkActive.isChecked = mVUCrmAdvertisements!!.active == "Y"

            if (mCrmAdvertisementTypes != null) {
                for ((index, obj) in mCrmAdvertisementTypes!!.withIndex()) {
                    if (mVUCrmAdvertisements!!.advertisementTypeId == obj.advertisementTypeId) {
                        mBinding.spnAdvertisementType.setSelection(index)
                        break
                    }
                }
            }
            if (!mVUCrmAdvertisements!!.Length.isNullOrEmpty())
                mBinding.edtLength.setText(formatWithPrecisionCustomDecimals(mVUCrmAdvertisements!!.Length.toString(), false, 3))
            if (!mVUCrmAdvertisements!!.wdth.isNullOrEmpty())
                mBinding.edtWidth.setText(formatWithPrecisionCustomDecimals(mVUCrmAdvertisements!!.wdth.toString(), false, 3))
            if (!mVUCrmAdvertisements!!.TaxableMatter.isNullOrEmpty())
                mBinding.edtTaxableMatter.setText(formatWithPrecisionCustomDecimals(mVUCrmAdvertisements!!.TaxableMatter.toString(), false, 3))

            bindDocuments()
            bindCounts()
            getInvoiceCount4Tax()
        }
    }

    private fun clearFields() {
        mBinding.edtQuantity.setText("")
        mBinding.edtDescription.setText("")
        //  mBinding.edtStartDate.setText("")
        mBinding.edtEstimatedAmountForProduct.setText("")
        // mBinding.chkActive.isChecked = false

        mBinding.edtLength.setText("")
        mBinding.edtWidth.setText("")
        mBinding.edtTaxableMatter.setText("")
        calTaxMatter = 0.0
        calValue = 0.0
        minArea = 0.0
        maxArea = 0.0

        documentList.clear()
        mBinding.rcvImages.adapter = null
    }

    private fun bindDocuments() {
        mVUCrmAdvertisements?.let {
            it.advertisementId?.let {
                APICall.getDocumentDetails(it.toString(), "CRM_Advertisements", object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        if (response.isNotEmpty()) {
                            mBinding.rcvImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            documentList = response as ArrayList<COMDocumentReference>
                            updateAdapter(documentList)
                        }
                    }

                    override fun onFailure(message: String) {
                        if (message.isNotEmpty())
                            mListener?.showAlertDialog(message)
                    }
                })
            }
        }
    }

    private fun fetchCount(filterColumns: List<FilterColumn>) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "COM_DocumentReferences"
        tableDetails.primaryKeyColumnName = "DocumentReferenceID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                mBinding.txtNumberOfDocuments.text = "0"
            }

            override fun onSuccess(response: Int) {
                mBinding.txtNumberOfDocuments.text = "$response"
            }
        })
    }

    private fun bindCounts() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "CRM_Advertisements"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mVUCrmAdvertisements?.advertisementId}"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn)
    }

    private fun setListeners() {
        mBinding.llDocuments.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.btnSave.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.btnGet.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        mBinding.addImageButton.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onLayoutClick(v)
            }
        })
        //mBinding.llDocuments.setOnClickListener(this)
        mBinding.spnAdvertisementType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (isAdvertisementTypeChanged)
                    clearFields()

                isAdvertisementTypeChanged = true

                val obj = p0?.selectedItem as CRMAdvertisementTypes
                if(obj.unitcode == Constant.UnitCode.EA.name){
                    mBinding.llLength.visibility = View.GONE
                    mBinding.llWidth.visibility = View.GONE
                }else{
                    mBinding.llLength.visibility = View.VISIBLE
                    mBinding.llWidth.visibility = View.VISIBLE
                }
                obj.unitcode.let {
                    if (it != null) {
                        unitCode = it
                    }
                    measurementUnits.let {
                        for ((index, unitObj) in it.withIndex()) {
                            if (unitCode == unitObj.unitCode!!) {
                                mBinding.edtUnitCode.setText(unitObj.unit)
                                break
                            } else
                                mBinding.edtUnitCode.setText("")
                        }
                    }
                }

                obj.minArea.let {
                    if (it != null && it.isNotEmpty())
                        minArea = it.toString().toDouble()
                }

                obj.maxArea.let {
                    if (it != null && it.isNotEmpty())
                        maxArea = it.toString().toDouble()
                }

            }
        }
    }

    fun onLayoutClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.btnSave -> {

                    if (validateView()) {
                        mListener?.showAlertDialog(R.string.are_you_sure_you_have_entered_all_valid_information,
                                R.string.yes,
                                View.OnClickListener {
                                    edtRemoveFocus()
                                    val dialog = (it as Button).tag as AlertDialog
                                    dialog.dismiss()
                                    saveAdvertisements(view)
                                },
                                R.string.no,
                                View.OnClickListener
                                {
                                    val dialog = (it as Button).tag as AlertDialog
                                    dialog.dismiss()
                                })
                    } else {
                    }
                }
                R.id.btnGet -> {
                    edtRemoveFocus()
                    fetchTaxableMatter()
                }
                R.id.addImageButton -> {
                    if (hasPermission(requireContext(), android.Manifest.permission.CAMERA)) {
                        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, Constant.REQUEST_IMAGE_CAPTURE)*/
                        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                            val photoFile: File?
                            try {
                                photoFile = createImageFile()
                            } catch (e: IOException) {
                                LogHelper.writeLog(exception = e)
                                return
                            }
                            val photoUri: Uri = FileProvider.getUriForFile(requireActivity(), requireActivity()?.packageName.toString() + ".provider", photoFile)
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                            startActivityForResult(pictureIntent, Constant.REQUEST_IMAGE_CAPTURE)
                        }else{

                        }
                    } else {
                        requestForPermission(android.Manifest.permission.CAMERA, Constant.REQUEST_CODE_CAMERA)
                    }
                }
                R.id.llDocuments -> {
                    when {
                        mVUCrmAdvertisements != null && mVUCrmAdvertisements?.advertisementId != null && mVUCrmAdvertisements?.advertisementId != 0 -> {
                            val documentMasterFragment = DocumentsMasterFragment()
                            val bundle = Bundle()
                            bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_ADVERTISEMENTS)
                            bundle.putInt(Constant.KEY_PRIMARY_KEY, mVUCrmAdvertisements?.advertisementId
                                    ?: 0)
                            documentMasterFragment.arguments = bundle
                            documentMasterFragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)

                            mListener?.showToolbarBackButton(R.string.documents)
                            mListener?.addFragment(documentMasterFragment, true)
                        }
                        validateView() -> {
                            saveAdvertisements(view)
                        }
                        else -> {
                        }
                    }

                }
                else -> {
                }
            }
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

    private fun navigateToDocumentPreviewActivity(comDocumentReference: COMDocumentReference) {
        if (documentList.isNotEmpty()) {
            val intent = Intent(context, DocumentPreviewActivity::class.java)
            documentList.remove(comDocumentReference)
            documentList.add(0, comDocumentReference)
            //intent.putExtra(Constant.KEY_DOCUMENT_URL, comDocumentReference.awsfile)
            intent.putExtra(Constant.KEY_DOCUMENT, documentList)
            startActivity(intent)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.id.let { it ->
            if (it == R.id.btnDelete) {
                val comDocumentReference = obj as COMDocumentReference?
                comDocumentReference?.documentID?.let {
                    val list = (mBinding.rcvImages.adapter as AdvertisementImageAdapter).get()
                    if (it.isEmpty()) {
                        list.remove(comDocumentReference)
                        updateAdapter(list)
                    } else
                        deleteDocument(comDocumentReference)
                }
            }
            /*if (it == R.id.itemImageDocumentPreview) {
                val comDocumentReference = obj as COMDocumentReference
                navigateToDocumentPreviewActivity(comDocumentReference)
            }*/
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun deleteDocument(document: COMDocumentReference) {
        document.documentReferenceID?.let {
            mListener?.showProgressDialog()
            APICall.deleteDocument(it.toInt(), object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    val list = (mBinding.rcvImages.adapter as AdvertisementImageAdapter).get()
                    if (!list.isNullOrEmpty()) {
                        list.remove(document)
                        updateAdapter(list)
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun updateAdapter(documents: ArrayList<COMDocumentReference>) {
        mBinding.rcvImages.adapter = AdvertisementImageAdapter(documents, this)
        if (mListener?.screenMode == Constant.ScreenMode.VIEW)
            (mBinding.rcvImages.adapter as AdvertisementImageAdapter).enableDelete(false)
        else
            (mBinding.rcvImages.adapter as AdvertisementImageAdapter).enableDelete(true)
    }

    private fun fetchTaxableMatter() {
        edtRemoveFocus(true)
        val getTaxableMatterColumnData = GetTaxableMatterColumnData()
        getTaxableMatterColumnData.taskCode = mTaskCode
        mListener?.showProgressDialog()
        APICall.getTaxableMatterColumnData(getTaxableMatterColumnData, object : ConnectionCallBack<List<DataTaxableMatter>> {
            override fun onSuccess(response: List<DataTaxableMatter>) {

                val list: java.util.ArrayList<DataTaxableMatter> = arrayListOf()
                for (it in response) {
                    val taxableMatter = DataTaxableMatter()
                    taxableMatter.taxableMatterColumnName = it.taxableMatterColumnName
                    if ("TaxableMatter" == it.taxableMatterColumnName) {
                        if (!TextUtils.isEmpty(mBinding.edtTaxableMatter.text?.toString()?.trim()))
                            taxableMatter.taxableMatter = currencyToDouble(mBinding.edtTaxableMatter.text.toString().trim())!!.toDouble().toBigDecimal().toString()
                    }
                    list.add(taxableMatter)
                }
                fetchEstimatedAmount(list)

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun fetchEstimatedAmount(taxableMatter: ArrayList<DataTaxableMatter>) {
        val getEstimatedTaxForProduct = GetEstimatedTaxForProduct()
        getEstimatedTaxForProduct.dataTaxableMatter = taxableMatter
        getEstimatedTaxForProduct.taskCode = mTaskCode
        getEstimatedTaxForProduct.customerID = ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                ?: 0
        if (mBinding.spnAdvertisementType.selectedItem != null) {
            val advertisementType = mBinding.spnAdvertisementType.selectedItem as CRMAdvertisementTypes?
            advertisementType?.advertisementTypeId?.let {
                getEstimatedTaxForProduct.entityPricingVoucherNo = "$it"
            }
        }
        if (!TextUtils.isEmpty(mBinding.edtStartDate.text?.toString()?.trim()))
            getEstimatedTaxForProduct.startDate = serverFormatDate(mBinding.edtStartDate.text.toString().trim())
        mListener?.showProgressDialog()
        APICall.getEstimatedTaxForProduct(getEstimatedTaxForProduct, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                mBinding.edtEstimatedAmountForProduct.setText(formatWithPrecision(response))
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.edtEstimatedAmountForProduct.setText("")
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun saveDocuments(view: View?, advertisement: CRMAdvertisements, updateEntityDocument: UpdateEntityDocument) {
        mListener?.showProgressDialog()
        APICall.updateEntityDocuments(updateEntityDocument, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                insertAdvertisement(view, advertisement, arrayListOf())
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun insertAdvertisement(view: View?, advertisement: CRMAdvertisements, documents: ArrayList<COMDocumentReference>) {
        APICall.insertAdvertisements(advertisement, documents, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()

                if (mVUCrmAdvertisements == null)
                    mVUCrmAdvertisements = VUCRMAdvertisements()
                if (response != 0)
                    mVUCrmAdvertisements?.advertisementId = response

                if (view?.id == R.id.btnSave) {
                    Handler().postDelayed({
                        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                        mListener!!.popBackStack()
                    }, 500)
                } else
                    onLayoutClick(view)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun saveAdvertisements(view: View?) {
        edtRemoveFocus(true)
        mListener?.showProgressDialog()
        val advertisement = getAdvertisements()
        var documents = arrayListOf<COMDocumentReference>()
        for (doc in documentList) {
            if (doc.documentID.isNullOrEmpty()) {
                doc.documentNo = ""
                documents.add(doc)
            }
        }
        if (advertisement.advertisementId != null && advertisement.advertisementId != 0 && documents.isNotEmpty()) {
            val updateEntityDocument = UpdateEntityDocument()
            updateEntityDocument.comDocumentReference = documents
            updateEntityDocument.primaryKeyValue = "${advertisement.advertisementId}"
            updateEntityDocument.tableName = "CRM_Advertisements"
            saveDocuments(view, advertisement, updateEntityDocument)
        } else
            insertAdvertisement(view, advertisement, documentList)
    }

    private fun getAdvertisements(): CRMAdvertisements {
        val crmAdvertisements = CRMAdvertisements()

        if (mBinding.spnAdvertisementType.selectedItem != null) {
            val advertisementType: CRMAdvertisementTypes = mBinding.spnAdvertisementType.selectedItem as CRMAdvertisementTypes
            crmAdvertisements.advertisementTypeId = advertisementType.advertisementTypeId
        }

        if (mVUCrmAdvertisements != null && mVUCrmAdvertisements!!.advertisementId != 0)
            crmAdvertisements.advertisementId = mVUCrmAdvertisements!!.advertisementId

        if (mBinding.edtQuantity.text != null && !TextUtils.isEmpty(mBinding.edtQuantity.text.toString())) {
            crmAdvertisements.quantity = mBinding.edtQuantity.text.toString().toInt()
        }

        if (mBinding.edtDescription.text != null && !TextUtils.isEmpty(mBinding.edtDescription.text.toString())) {
            crmAdvertisements.description = mBinding.edtDescription.text.toString()
        }

        crmAdvertisements.startDate = serverFormatDate(mBinding.edtStartDate.text.toString())

        crmAdvertisements.organisationId = ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
        if (mBinding.chkActive.isChecked)
            crmAdvertisements.active = "Y"
        else
            crmAdvertisements.active = "N"

        if (mBinding.edtLength.text != null && !TextUtils.isEmpty(mBinding.edtLength.text.toString())) {
            crmAdvertisements.Length = (currencyToDouble(mBinding.edtLength.text.toString().trim())!!.toDouble()).toBigDecimal().toString()
        }

        if (mBinding.edtWidth.text != null && !TextUtils.isEmpty(mBinding.edtWidth.text.toString())) {
            crmAdvertisements.wdth = (currencyToDouble(mBinding.edtWidth.text.toString().trim())!!.toDouble()).toBigDecimal().toString()
        }

        if (unitCode != null && !TextUtils.isEmpty(unitCode)) {
            crmAdvertisements.unitcode = unitCode
        }

        if (mBinding.edtTaxableMatter.text != null && !TextUtils.isEmpty(mBinding.edtTaxableMatter.text.toString())) {
            crmAdvertisements.TaxableMatter = (currencyToDouble(mBinding.edtTaxableMatter.text.toString().trim())!!.toDouble()).toBigDecimal().toString()
        }

        return crmAdvertisements

    }

    private fun validateView(): Boolean {
        edtRemoveFocus()
        val advertisementTypes = mBinding.spnAdvertisementType.selectedItem as CRMAdvertisementTypes?
        if (advertisementTypes?.advertisementTypeName == null || advertisementTypes.advertisementTypeId == -1) {
            mListener!!.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.advertisement_type))
            mBinding.spnAdvertisementType.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(mBinding.edtQuantity.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.quantity))
            mBinding.edtQuantity.requestFocus()
            return false
        }

        if (mBinding.edtQuantity.text.toString().toBigInteger() > Int.MAX_VALUE.toBigInteger()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_quantity_limit) + " " + "${Int.MAX_VALUE}")
            mBinding.edtQuantity.requestFocus()
            return false
        }

        if ((TextUtils.isEmpty(mBinding.edtLength.text.toString())) && (mBinding.llLength.isVisible)) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.length_))
            mBinding.edtLength.requestFocus()
            return false
        }

        if ((TextUtils.isEmpty(mBinding.edtWidth.text.toString())) && (mBinding.llWidth.isVisible)) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.width))
            mBinding.edtWidth.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(mBinding.edtTaxableMatter.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.taxable_matter))

            return false
        }

        if (TextUtils.isEmpty(mBinding.edtUnitCode.text.toString())) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.unit_code))

            return false
        }

        if (mBinding.llLength.isVisible && mBinding.llWidth.isVisible ) {
            if (minArea == 0.0 || calValue < minArea) {
                mListener?.showSnackbarMsg(getString(R.string.min_area_condition))
                mBinding.edtLength.requestFocus()
                return false
            }
        }

        if (mBinding.llLength.isVisible && mBinding.llWidth.isVisible ) {
            if (maxArea == 0.0 || calValue > maxArea) {
                mListener?.showSnackbarMsg(getString(R.string.min_area_condition))
                mBinding.edtLength.requestFocus()
                return false
            }
        }

        return true
    }

    private fun getInvoiceCount4Tax() {

        val currentDue = CheckCurrentDue()
        currentDue.accountId = mVUCrmAdvertisements?.accountID
        currentDue.vchrno  = mVUCrmAdvertisements?.advertisementId
        currentDue.taxRuleBookCode  = mTaxRuleBookCode
        APICall.getInvoiceCount4Tax(currentDue, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                if (response>0)
                {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }else{
                        setEditActionForSave(false)
                    }
                }
                else
                {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        setEditAction(false)
                    }
                    else {
                        setEditAction(true)
                    }
                }
            }
            override fun onFailure(message: String) {
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                {
                    setEditAction(false)
                }
            }
        })
    }

    interface Listener {
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode

    }

}