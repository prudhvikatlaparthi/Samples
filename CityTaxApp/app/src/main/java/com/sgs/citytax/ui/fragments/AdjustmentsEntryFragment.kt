package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.GetAdministrationOffice
import com.sgs.citytax.api.response.INVAdjustmentType
import com.sgs.citytax.api.response.InventoryProductsDetails
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentAdjustmentEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.LocalDocumentPreviewActivity
import com.sgs.citytax.ui.adapter.MultipleDocumentsAdapter
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AdjustmentsEntryFragment : BaseFragment() {
    private var selectedAdminOffAccountId: Int = 0
    private var mListener: Listener? = null
    private var mHelper: LocationHelper? = null
    private lateinit var binding: FragmentAdjustmentEntryBinding
    private val adjustmentTypeList = mutableListOf<INVAdjustmentType>()
    private val productLineItemsList = mutableListOf<InventoryProductsDetails>()
    private val adjustmentDate: Date = Date()
    private var adjustmentsListResults: AdjustmentsListResults? = null
    
    val MULTIPLE_DOCUMENTS = 10001
    private val kFileExtension = "jpg"
    private var mImageFilePath : String? = null
    // Multiple docs
    private var mDocumentsList: ArrayList<COMDocumentReference> = arrayListOf()

    private val documentListAdapter: MultipleDocumentsAdapter by lazy {
        MultipleDocumentsAdapter(mDocumentsList, object : IClickListener {
            override fun onClick(view: View, position: Int, obj: Any) {
                view.let {
                    when (view.id) {
                        R.id.imgDocument -> {
                            val comDocumentReference = obj as COMDocumentReference
                            mDocumentsList.remove(comDocumentReference)
                            mDocumentsList.add(0, comDocumentReference)
                            documentListAdapter.notifyDataSetChanged()
                            startLocalPreviewActivity(mDocumentsList)
                        }
                        R.id.btnClearImage -> {
                            if (mDocumentsList.size == 1)
                                binding.multipleDoc.txtNoDataFound.visibility = View.GONE
                            mDocumentsList.removeAt(position)
                            documentListAdapter.notifyDataSetChanged()
                            if (mDocumentsList.isEmpty()){
                                binding.multipleDoc.txtNoDataFound.show()
                            }
                            ObjectHolder.documents = mDocumentsList
                        }
                        else -> {
                        }
                    }
                }
            }

            override fun onLongClick(view: View, position: Int, obj: Any) {

            }
        })
    }

    companion object {
        fun newInstance() = AdjustmentsEntryFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_adjustment_entry, container, false)
         adjustmentsListResults = arguments?.getParcelable(Constant.KEY_STOCK_MANAGEMENT)
        if (mListener?.screenMode == Constant.ScreenMode.VIEW){
            setEditAction(false)
        }
        initComponents()
        listeners()
        return binding.root
    }

    private fun setEditAction(action: Boolean) {
        binding.edAdjustmentDate.isEnabled = action
        binding.edAdministrativeOffice.isEnabled = action
        binding.spnLineItemCode.isEnabled = action
        binding.edtLineItem.isEnabled = action
        binding.edtLineUnit.isEnabled = action
        binding.spnLineAdjustmentType.isEnabled = action
        binding.edtLineQty.isEnabled = action
        binding.edtLineRemarks.isEnabled = action
        binding.btnSave.isVisible = action
        if(!action) {
            binding.multipleDoc.llRootView.visibility = View.GONE
            binding.viewDoc.visibility = View.VISIBLE
        }else{
            binding.multipleDoc.llRootView.visibility = View.VISIBLE
            binding.viewDoc.visibility = View.GONE
        }
    }

    private fun listeners() {
        binding.viewDoc.setOnClickListener {
            val fragment = DueNoticeImagesFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU,Constant.QuickMenu.QUICK_MENU_STOCK_MANAGEMENT_IMAGES)
            bundle.putString(Constant.KEY_PRIMARY_KEY,
                adjustmentsListResults?.AdjustmentID.toString()
            )
            fragment.arguments = bundle
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
            mListener?.showToolbarBackButton(R.string.stock_management_images)
            mListener?.addFragment(fragment, true)
        }
        binding.btnSave.setOnClickListener {
            saveAdjustment()
        }

        binding.spnLineItemCode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p0 != null && p0.selectedItem != null) {
                        val selectedInventoryProductsDetails =
                            p0.selectedItem as InventoryProductsDetails
                        binding.edtLineItem.setText(selectedInventoryProductsDetails.product)
                        binding.edtLineUnit.setText(selectedInventoryProductsDetails.unit)
                        if(adjustmentsListResults == null){
                            binding.edtLineQty.setText("")
                        }else{
                            binding.edtLineQty.setText(adjustmentsListResults?.Quantity?.toString())
                        }
                        if (selectedInventoryProductsDetails.allwfrctnlqty == "Y") {
                            binding.edtLineQty.setInputType(allowFraction = true)
                        } else {
                            binding.edtLineQty.setInputType()
                        }
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        binding.multipleDoc.fabAddImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(MULTIPLE_DOCUMENTS)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun initComponents() {
        ObjectHolder.documents.clear()
        if (adjustmentsListResults == null){
            binding.edAdjustmentDate.setText(formatDisplayDateTime(adjustmentDate))
        }else{
            binding.edAdjustmentDate.setText(formatDisplayDateTimeInMillisecond(adjustmentsListResults?.AdjustmentDate))
            binding.edtLineQty.setText(adjustmentsListResults?.Quantity?.toString())
            binding.edtLineRemarks.setText(adjustmentsListResults?.Remarks)
        }
        binding.edtLineQty.setInputType()
        binding.multipleDoc.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        binding.multipleDoc.rcDocuments.adapter = documentListAdapter

        getAdministrationOffices()
        getAdjustmentTypes()
    }

    private fun getAdministrationOffices() {
        APICall.getAdministrationOffices(
            GetAdminOfficeAddressPayload(
                context = SecurityContext(),
                accountID = MyApplication.getPrefHelper().accountId
            ),
            object : ConnectionCallBack<List<GetAdministrationOffice>> {
                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                }

                override fun onSuccess(response: List<GetAdministrationOffice>) {
                    mListener?.dismissDialog()
                    if (response.isNotEmpty()) {
                        binding.edAdministrativeOffice.setText(response[0].acctname)
                        response[0].acctid?.let {
                            selectedAdminOffAccountId = it
                            getInventoryProductsDetails(it)
                        }
                    }
                }
            })
    }

    private fun getInventoryProductsDetails(adminAccountID: Int) {
        APICall.getInventoryProductsDetails(
            GetInventoryProductsDetailsPayload(
                context = SecurityContext(),
                FromAccountID = adminAccountID
            ),
            object : ConnectionCallBack<List<InventoryProductsDetails>> {
                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                }

                override fun onSuccess(response: List<InventoryProductsDetails>) {
                    mListener?.dismissDialog()
                    productLineItemsList.clear()
                    productLineItemsList.addAll(response)
                    updateItemCodeAdapter()
                }
            })
    }

    private fun getAdjustmentTypes() {
        val searchFilter = AdvanceSearchFilter()
        searchFilter.pageSize = 50
        searchFilter.pageIndex = null
        searchFilter.query = null
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "INV_AdjustmentTypes"
        tableDetails.primaryKeyColumnName = "AdjustmentTypeID"
        tableDetails.TableCondition = ""
        tableDetails.selectColoumns =
            "AdjustmentType,StockInOut,AdjustmentTypeCode,AdjustmentTypeID"
        tableDetails.sendCount = false
        searchFilter.tableDetails = tableDetails
        APICall.getAdjustmentTypes(
            searchFilter,
            object : ConnectionCallBack<List<INVAdjustmentType>> {
                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                }

                override fun onSuccess(response: List<INVAdjustmentType>) {
                    mListener?.dismissDialog()
                    adjustmentTypeList.clear()
                    adjustmentTypeList.addAll(response)
                    updateAdjustmentTypeAdapter()
                }
            })
    }

    private fun updateAdjustmentTypeAdapter() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_separator_layout_item,
            R.id.title,
            adjustmentTypeList
        )
        binding.spnLineAdjustmentType.adapter = adapter
        adjustmentsListResults?.let {
            adjustmentTypeList.forEachIndexed { index,item ->
                if (item.adjustmentTypeID == it.AdjustmentTypeID){
                    binding.spnLineAdjustmentType.setSelection(index)
                }
            }
        }
    }

    private fun updateItemCodeAdapter() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_separator_layout_item,
            R.id.title,
            productLineItemsList
        )
        binding.spnLineItemCode.adapter = adapter
        binding.btnSave.isEnabled = true
        binding.btnSave.isClickable = true
        adjustmentsListResults?.let {
            productLineItemsList.forEachIndexed { index,item ->
                if (item.itemCode == it.ItemCode){
                    binding.spnLineItemCode.setSelection(index)
                }
            }
        }
    }

    private fun saveAdjustment() {
        if (isValidForm()) {
            mListener?.showProgressDialog()
            val adj: INVAdjustmentType =
                binding.spnLineAdjustmentType.selectedItem as INVAdjustmentType
            val inventoryProductsDetails: InventoryProductsDetails =
                binding.spnLineItemCode.selectedItem as InventoryProductsDetails
            val storeAdjustmentPayload = StoreAdjustmentsPayload(
                context = SecurityContext(),
                ObjectHolder.documents,
                adjustments = listOf(
                    AdjustmentItemPayload(
                        acctid = selectedAdminOffAccountId,
                        adjdt = adjustmentDate,
                        adjid = 0,
                        adjtypid = adj.adjustmentTypeID,
                        binlocid = 0,
                        crtd = null,
                        crtddt = null,
                        customProperties = null,
                        departurePreparationNo = null,
                        designFilePath = null,
                        designSource = null,
                        id = 0,
                        isUpdateable = null,
                        itemCode = inventoryProductsDetails.itemCode,
                        mdfd = null,
                        mdfddt = null,
                        orgid = 0,
                        prodcode = inventoryProductsDetails.productCode,
                        qty = binding.edtLineQty.text.toString().toDoubleOrNull() ?: 0.0,
                        refno = null,
                        reftxntypcode = null,
                        refvchrno = null,
                        rmks = binding.edtLineRemarks.text.toString(),
                        trolleyNo = null,
                        usrorgbrid = 0,
                        variantCode = null
                    )
                ))

            APICall.storeAdjustment(

                storeAdjustmentPayload,
                object : ConnectionCallBack<Boolean> {
                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        mListener?.showAlertDialog(message)
                    }

                    override fun onSuccess(response: Boolean) {
                        mListener?.dismissDialog()
                        targetFragment?.onActivityResult(
                            targetRequestCode,
                            Activity.RESULT_OK,
                            Intent()
                        )
                        mListener?.popBackStack()
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper?.onActivityResult(requestCode, resultCode)
//        mListener?.showToolbarBackButton(R.string.sto)
//        bindCounts()
        if (requestCode == MULTIPLE_DOCUMENTS) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                binding.multipleDoc.txtNoDataFound.hide()
                mDocumentsList.add(doc)
                documentListAdapter.notifyDataSetChanged()
                ObjectHolder.documents = mDocumentsList
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        }
    }

    private fun isValidForm(): Boolean {
        var isValid = true
        when {
            binding.spnLineItemCode.selectedItem == null -> {
                mListener?.showAlertDialog(getString(R.string.please_select_item_code))
                isValid = false
            }
            binding.spnLineAdjustmentType.selectedItem == null -> {
                mListener?.showAlertDialog(getString(R.string.please_sel_adj_type))
                isValid = false
            }
            binding.edtLineQty.text?.toString()?.isEmpty() == true -> {
                mListener?.showAlertDialog(getString(R.string.please_enter_qty))
                binding.edtLineQty.requestFocus()
                isValid = false
            }
            binding.edtLineQty.text?.toString()?.toDoubleOrNull() ?: 0.0 <= 0.0 -> {
                mListener?.showAlertDialog(getString(R.string.qty_less_than_equal_zero))
                binding.edtLineQty.requestFocus()
                isValid = false
            }
        }
        return isValid
    }

    private fun checkAndStartCameraIntent(resultCode: Int) {
        // region Storage Permission
        if (!hasPermission(requireContext(), Manifest.permission.CAMERA)) {
            requestForPermission(
                arrayOf(Manifest.permission.CAMERA),
                Constant.REQUEST_CODE_CAMERA
            )
            return
        }
        // endregion
        openCameraIntent(resultCode)
    }

    private fun openCameraIntent(requestCode: Int) {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File?
            try {
                mImageFilePath = null
                photoFile = createImageFile()
            } catch (e: IOException) {
                LogHelper.writeLog(exception = e)
                return
            }
            val photoUri: Uri = FileProvider.getUriForFile(requireContext(), context?.packageName.toString() + ".provider", photoFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(pictureIntent, requestCode)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".${kFileExtension}", storageDir)
        mImageFilePath = image.absolutePath
        return image
    }

    private fun afterDocumentResult(): COMDocumentReference {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(mImageFilePath, options)
        val mDocumentReference = COMDocumentReference()
        mDocumentReference.localPath = mImageFilePath
        mDocumentReference.data = ImageHelper.getBase64String(bitmap, 70)
        mDocumentReference.documentTypeID = 0
        mDocumentReference.documentProofType = null
        mDocumentReference.documentTypeName = null
        mDocumentReference.documentNo = "${UUID.randomUUID()}"
        mDocumentReference.documentName = mDocumentReference.documentNo
        mDocumentReference.extension = kFileExtension
        return mDocumentReference
    }

    private fun startLocalPreviewActivity(list: ArrayList<COMDocumentReference>) {
        val localDocList = list.map {
            LocalDocument(localSrc = it.localPath)
        }
        val intent = Intent(context, LocalDocumentPreviewActivity::class.java)
        intent.putExtra(Constant.KEY_DOCUMENT, ArrayList(localDocList))
        startActivity(intent)
    }

    fun onBackPressed() {
        mListener?.popBackStack()
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun popBackStack()
        fun showAlertDialog(message: String)
        fun finish()
        fun showSnackbarMsg(message: String?)
        fun showAlertDialog(
            message: Int,
            positiveButton: Int,
            positiveListener: View.OnClickListener,
            neutralButton: Int,
            neutralListener: View.OnClickListener?,
            negativeButton: Int,
            negativeListener: View.OnClickListener,
            view: View
        )
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }

}