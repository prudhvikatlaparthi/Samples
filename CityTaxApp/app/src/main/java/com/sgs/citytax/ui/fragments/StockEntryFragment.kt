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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.AccountsPayload
import com.sgs.citytax.api.payload.StoreStockTransferPayload
import com.sgs.citytax.api.response.SalesProductData
import com.sgs.citytax.databinding.FragmentStockEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.LocalDocumentPreviewActivity
import com.sgs.citytax.ui.adapter.MultipleDocumentsAdapter
import com.sgs.citytax.util.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StockEntryFragment : BaseFragment() {

    private lateinit var mBinding: FragmentStockEntryBinding
    private var mListener: Listener? = null
    private val stockTransferDate: Date = Date()
    private var fromAccount: Int = 0
    private val stockDate: Date = Date()
    val MULTIPLE_DOCUMENTS = 10001

    private var mHelper: LocationHelper? = null
    private val kFileExtension = "jpg"
    private var mImageFilePath : String? = null
    // Multiple docs
    private var mDocumentsList: ArrayList<COMDocumentReference> = arrayListOf()
    private var mStockTransfer:StockTransferListResults ? = null
    private var fromAccounts : ArrayList<Account>? = arrayListOf()
    private var toAccounts : ArrayList<Account>? = arrayListOf()
    private var mSalesProductData : ArrayList<SalesProductData> = arrayListOf()

    companion object {
        @JvmStatic
        fun newInstance() = StockEntryFragment()
    }


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
                                mBinding.multipleDoc.txtNoDataFound.visibility = View.GONE
                            mDocumentsList.removeAt(position)
                            documentListAdapter.notifyDataSetChanged()
                            if (mDocumentsList.isEmpty()){
                                mBinding.multipleDoc.txtNoDataFound.show()
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


    override fun initComponents() {
        //region getArguments
        ObjectHolder.documents.clear()
        arguments?.let {
            mStockTransfer = arguments?.getParcelable(Constant.KEY_STOCK_TRANSFER)
        }
        //endregion

        mBinding.multipleDoc.rcDocuments.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.multipleDoc.rcDocuments.adapter = documentListAdapter
        if(mStockTransfer==null) {
            mBinding.edStockTransferDate.setText(formatDisplayDateTime(stockTransferDate))
            mBinding.edtLineQty.setInputType()
        }else{
            mBinding.edStockTransferDate.setText(formatDisplayDateTimeInMillisecond(mStockTransfer?.allocationDate))
            mBinding.edtTaxName.setText(mStockTransfer?.itemCode)
            mBinding.edtLineUnit.setText(mStockTransfer?.unit)
            mBinding.edtLineRemarks.setText(mStockTransfer?.remarks)
            mBinding.edtLineQty.setText(getQuantity(mStockTransfer?.quantity.toString()))
        }
        getFromAndToAccounts(true)
    }

    private fun setViews() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.ADD -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    private fun setEditAction(action: Boolean) {
        mBinding.edStockTransferDate.isEnabled = action
        mBinding.spnFromAccount.isEnabled = action
        mBinding.spnToAccount.isEnabled = action
        mBinding.spnLineTaxCode.isEnabled = action
        mBinding.edtTaxName.isEnabled = action
        mBinding.edtLineUnit.isEnabled = action
        mBinding.edtSystemStock.isEnabled = action
        mBinding.edtLineQty.isEnabled = action
        mBinding.edtLineRemarks.isEnabled = action
        mBinding.btnViewImages.visibility = if(!action) View.VISIBLE else View.GONE
        mBinding.multipleDoc.llRootView.visibility = if(action) View.VISIBLE else View.GONE
        mBinding.btnSave.visibility = if(action) View.VISIBLE else View.GONE
    }

    private fun getTaxCode() {
        mListener?.showProgressDialog()
        val payload = AccountsPayload()
        payload.frmAcctIdForProd = fromAccount
        APICall.getProductsByTypeForStockTransfer(payload, object : ConnectionCallBack<List<SalesProductData>> {
            override fun onSuccess(response: List<SalesProductData>) {
                mListener?.dismissDialog()
                mSalesProductData = response as java.util.ArrayList<SalesProductData>
                updateTaxCode()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (message.isNotEmpty())
                    mListener?.showAlertDialog(message)
            }
        })
    }

    private fun updateTaxCode() {

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_separator_layout_item,
            R.id.title,
            mSalesProductData
        )

        mBinding.spnLineTaxCode.adapter = adapter
        mBinding.btnSave.isEnabled = true
        mBinding.btnSave.isClickable = true
        if(mStockTransfer!=null) {
            mSalesProductData.forEachIndexed { index, obj ->
                if (mStockTransfer?.item == obj.item || mStockTransfer?.product == obj.item) {
                    mBinding.spnLineTaxCode.setSelection(index)
                    mBinding.edtLineQty.setInputType(allowFraction = obj.allwfrctnlqty == "Y")
                }
            }
        }
    }


    private fun getFromAndToAccounts(isFromAccount: Boolean) {
        val payload = AccountsPayload()
        if (!isFromAccount) {
            payload.fromAccountId = fromAccount
        }
        mListener?.showProgressDialog()
        APICall.getToandFromAccounts(payload, object : ConnectionCallBack<List<Account>> {
            override fun onSuccess(response: List<Account>) {
                mListener?.dismissDialog()
                if (response.isNotEmpty()) {
                    if (isFromAccount) {
                        fromAccounts?.addAll(response)
                    }else{
                        toAccounts?.addAll(response)
                    }
                    updateFromToAccountSpinners(response, isFromAccount)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    private fun updateFromToAccountSpinners(
        accounts: List<Account>,
        isFromAccount: Boolean
    ) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_separator_layout_item,
            R.id.title,
            accounts
        )
        if (isFromAccount) {
            mBinding.spnFromAccount.adapter = adapter
        } else {
            mBinding.spnToAccount.adapter = adapter
        }

        if(mStockTransfer !=null){
            if (isFromAccount) {
                fromAccounts?.forEachIndexed { index, obj ->
                    if (mStockTransfer?.fromAccountName == obj.accountName) {
                        mBinding.spnFromAccount.setSelection(index)
                    }
                }
            } else {
                toAccounts?.forEachIndexed { index, obj ->
                    if (mStockTransfer?.toAccountName == obj.accountName) {
                        mBinding.spnToAccount.setSelection(index)
                    }
                }
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_stock_entry, container, false)
        initComponents()
        setViews()
        setListeners()
        return mBinding.root
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener {
            if (validateData()) {
                saveData()
            }
        }

        mBinding.spnLineTaxCode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val salesProduct = p0?.selectedItem as SalesProductData
                    mBinding.edtTaxName.setText(salesProduct.itemCode)
                    mBinding.edtLineUnit.setText(salesProduct.unit)
                    mBinding.edtSystemStock.setText(getQuantity(salesProduct.stockInHand.toString()))
                    if(mListener?.screenMode!=Constant.ScreenMode.VIEW) mBinding.edtLineQty.setText("")
                    if (salesProduct.allwfrctnlqty == "Y") {
                        mBinding.edtLineQty.setInputType(allowFraction = true)
                    } else {
                        mBinding.edtLineQty.setInputType()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        mBinding.spnFromAccount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    fromAccount = (p0?.selectedItem as Account).accountId.toInt()
                    updateFromToAccountSpinners(arrayListOf(),false)
                    getFromAndToAccounts(false)
                    getTaxCode()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        mBinding.multipleDoc.fabAddImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                checkAndStartCameraIntent(MULTIPLE_DOCUMENTS)
            }
        })

        mBinding.btnViewImages.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val fragment = DueNoticeImagesFragment()
                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU,Constant.QuickMenu.QUICK_MENU_STOCK_TRANSFER_IMAGES)
                bundle.putString(Constant.KEY_PRIMARY_KEY, mStockTransfer?.allocationID.toString() ?: "")
                fragment.arguments = bundle
                //endregion
                fragment.setTargetFragment(this@StockEntryFragment, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                mListener?.showToolbarBackButton(R.string.stock_transfer_images)
                mListener?.addFragment(fragment, true)
            }
        })


    }

    private fun validateData(): Boolean {
        if (mBinding.edtLineQty.text?.toString()
                ?.toDoubleOrNull() ?: 0.0 > mBinding.edtSystemStock.text.toString().toDouble()
        ) {
            mListener?.showSnackbarMsg(getString(R.string.msg_greater_than_stock))
            return false
        }
        if (mBinding.edtLineQty.text?.toString()
                ?.toDoubleOrNull() ?: 0.0 == 0.0
        ) {
            mListener?.showSnackbarMsg(getString(R.string.msg_qty_greater_than_zero))
            return false
        }
        if (mBinding.spnToAccount.selectedItem == null) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)}  ${getString(R.string.to_account_name)} ")
            return false
        }
        if ((mBinding.spnFromAccount.selectedItem as Account).accountId == (mBinding.spnToAccount.selectedItem as Account).accountId){
            mListener?.showSnackbarMsg(getString(R.string.msg_fromacc_toacc_not_same))
            return false
        }
        return true
    }

    private fun saveData() {
        mListener?.showProgressDialog()
        val fromAcc: Account =
            mBinding.spnFromAccount.selectedItem as Account
        val toAccount: Account =
            mBinding.spnToAccount.selectedItem as Account
        val salesData =
            mBinding.spnLineTaxCode.selectedItem as SalesProductData
        val qty =
            mBinding.edtLineQty.text.toString()
        val remarks =
            mBinding.edtLineRemarks.text.toString()

        val stockTransferPayload = StoreStockTransferPayload(
            context = SecurityContext(),
            stockAllocation = listOf(
                INVStockAllocation(
                    date = stockDate,
                    fromAccountId = fromAcc.accountId.toInt(),
                    toAccountID = toAccount.accountId.toInt(),
                    itemCode = salesData.itemCode,
                    quantity = qty.toDoubleOrNull() ?: 0.0,
                    remarks = remarks
                )//productName = salesData.item,unit = salesData.unit,
            ),
            isAndroid = true,
            ObjectHolder.documents)

        APICall.storeStockTransfer(

            stockTransferPayload,
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHelper?.onActivityResult(requestCode, resultCode)
//        mListener?.showToolbarBackButton(R.string.sto)
//        bindCounts()
        if (requestCode == MULTIPLE_DOCUMENTS) {
            if (resultCode == Activity.RESULT_OK) {
                val doc = afterDocumentResult()
                mBinding.multipleDoc.txtNoDataFound.hide()
                mDocumentsList.add(doc)
                documentListAdapter.notifyDataSetChanged()
                ObjectHolder.documents = mDocumentsList
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mListener?.showSnackbarMsg(getString(R.string.cancelled))
            }
        }
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

    /*companion object {
        fun newInstance() = StockEntryFragment()
    }*/

    fun onBackPressed() {
        mListener?.popBackStack()
    }

    interface Listener {
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showToolbarBackButton(title: Int)
        fun showProgressDialog()
        fun dismissDialog()
        fun showSnackbarMsg(message: String)
        fun popBackStack()
        fun showAlertDialog(message: String)
        fun finish()
        var screenMode: Constant.ScreenMode
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
    }
}