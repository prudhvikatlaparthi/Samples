package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.databinding.FragmentAgreementEntryBinding
import com.sgs.citytax.model.COMStatusCode
import com.sgs.citytax.ui.custom.DialogAdapter
import com.sgs.citytax.util.*
import java.util.*


class AgreementEntryFragment : BaseFragment() {

    private lateinit var mBinding: FragmentAgreementEntryBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var mAgreementResultsList: AgreementResultsList? = null
    private var mStatusCodes: MutableList<COMStatusCode>? = null
    var mAdapter: DialogAdapter? = null
    var mTaxDueList: TaxDueList? = null
    lateinit var pagination: Pagination
    private val kPAGESIZE: Int = 100
    private var accountId: Int? = 0
    var dueNoticeID: Int? = null
    var dueagreementid: Int? = null
    var noticeReferenceNo: String? = ""
    private var setViewForGeoSpatial: Boolean? = false

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            accountId = arguments?.getInt(Constant.KEY_QUICK_ACCTID) ?: 0
            mAgreementResultsList = arguments?.getParcelable(Constant.KEY_DOCUMENT)
            setViewForGeoSpatial = arguments?.getBoolean(Constant.KEY_GEO_SPATIAL_VIEW) ?: false
            if (mAgreementResultsList == null) mAgreementResultsList = AgreementResultsList()
        }
        //endregion
        setViews()
        bindSpinner()
        setListeners()
        setVisibilty()
    }

    private fun setVisibilty() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_agreement_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {

        mBinding.edtAgreementDate.setDisplayDateFormat(parkingdisplayDateTimeTimeSecondFormat)
        mBinding.edtAgreementDate.setMinDate(Calendar.getInstance().timeInMillis)
        mBinding.edtValidDate.setMinDate(Calendar.getInstance().timeInMillis)
        mBinding.edtValidDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtDueNoticeNumber.setOnClickListener {
            showSearchTaxDueCustomListDialog(getString(R.string.dueNoticeNoText), "")
        }
        mBinding.edtStatusSpn.isEnabled = false
    }

    private fun showSearchTaxDueCustomListDialog(mTitle: String, query: String) {
        val dialog = requireContext().prepareCustomListDialog(R.layout.alert_dialog_list, true)
        val lView = dialog.findViewById<RecyclerView>(R.id.listView)
        lView.setHasFixedSize(true)
        lView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                LinearLayoutManager.VERTICAL
            )
        )
        dialog.findViewById<TextView>(R.id.listTextView).text = mTitle
        mAdapter = DialogAdapter()
        lView.adapter = mAdapter
        mAdapter?.addItemClickListener { item, position ->
            if (item is TaxDueList) {
                dialog.dismiss()
                noticeReferenceNo = item.noticeReferenceNo
                dueNoticeID = item.dueNoticeID
                mBinding.edtDueNoticeNumber.setText(item.noticeReferenceNo)
            }
        }
        pagination = Pagination(1, kPAGESIZE, lView) { pageNumber, PageSize ->
            callSearchTaxDueNoticeApi(query, pageNumber, PageSize, dialog)
        }
        pagination.setDefaultValues()
    }

    private fun Dialog.showhidePaginationProgress(isShow: Boolean) {
        findViewById<ProgressBar>(R.id.paginationProgress)?.isVisible = isShow
    }

    private fun callSearchTaxDueNoticeApi(
        query: String,
        pageNumber: Int,
        pageSize: Int,
        dialog: Dialog
    ) {
        if (pageNumber == 1) {
            mListener?.showProgressDialog(R.string.msg_please_wait)
        } else {
            dialog.showhidePaginationProgress(isShow = true)
        }
        val taxPayerDetails = GetTaxDueList()
        taxPayerDetails.pageIndex = pageNumber
        taxPayerDetails.pageSize = pageSize
//        taxPayerDetails.acctid=4030
        taxPayerDetails.acctid = accountId
        taxPayerDetails.dueagreementid = dueagreementid
        APICall.getSearchTaxDueList(
            taxPayerDetails,
            object : ConnectionCallBack<TaxDueListResponse> {
                override fun onSuccess(response: TaxDueListResponse) {
                    if (response.searchResults != null) {
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
                            //  binding.searchView.setQuery("", false)
                            mListener?.showAlertDialog(
                                getString(R.string.no_record),
                                DialogInterface.OnClickListener { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    dialog.dismiss()
                                })
                        }

                    }
                    mListener?.dismissDialog()
                    dialog.showhidePaginationProgress(isShow = false)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    if (pageNumber == 1) {
                        mListener?.showAlertDialog(
                            message,
                            DialogInterface.OnClickListener { _, _ ->
                                dialog.dismiss()
                            })
                    }
                    dialog.showhidePaginationProgress(isShow = false)
                }
            })
    }

    private fun displayPayerList(list: List<TaxDueList>) {
        mAdapter?.updateList(list)
        pagination.setIsScrolled(false)
        pagination.stopPagination(list.size)
    }

    private fun setEditAction(action: Boolean) {
        mBinding.edtDueNoticeNumber.isEnabled = action
//        mBinding.edtStatusSpn.isEnabled = action
        mBinding.edtAgreementDate.isEnabled = action
        mBinding.edtValidDate.isEnabled = action
        mBinding.edtRemarks.isEnabled = action
        mBinding.edtReferenceNo.isEnabled = action
        mBinding.btnClearImage.isEnabled = action

        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
            mBinding.llSignature.visibility = View.GONE
        } else {
            mBinding.btnSave.visibility = View.GONE
            mBinding.llSignature.visibility = View.GONE
        }
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.getCorporateOfficeLOVValues(
            "ACC_DueAgreements",
            object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mListener?.dismissDialog()

                    mStatusCodes = response.statusCodes

                    context?.let {
                        val adapter = ArrayAdapter<COMStatusCode>(
                            it,
                            android.R.layout.simple_spinner_dropdown_item,
                            response.statusCodes
                        )
                        mBinding.edtStatusSpn.adapter = adapter
                    }
                    bindData()
                }

                override fun onFailure(message: String) {
                    mBinding.edtStatusSpn.adapter = null
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }

            })
    }

    private fun setListeners() {
        mBinding.btnSave.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                onLayoutClick(v)
            }
        })

        mBinding.edtAgreementDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let {
                    if (it.isNotEmpty()) {
                        val sCurrentDate: Date = Calendar.getInstance().time
                        val agreementDate: Date = parseDate(it, displayDateFormat)
                        if (agreementDate.before(sCurrentDate)) {
                            mBinding.edtValidDate.setMinDate(sCurrentDate.time)
                        } else {
                            mBinding.edtValidDate.setMinDate(parseDate(it, displayDateFormat).time)

                            if (parseDate(
                                    it,
                                    displayDateFormat
                                ).before(mBinding.edtValidDate.getDateToCalender())
                            ) {
                                mBinding.edtValidDate.setDateToCalender(
                                    parseDate(
                                        it,
                                        displayDateFormat
                                    )
                                )
                            }


                        }

                        mBinding.edtValidDate.setText("")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mBinding.llDocuments.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                onLayoutClick(v)
            }
        })
        mBinding.btnClearImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                onLayoutClick(v)
            }
        })
    }

    private fun bindData() {
        if (mAgreementResultsList != null) {
            mAgreementResultsList?.let {
                mBinding.edtDueNoticeNumber.setText(mAgreementResultsList?.noticeReferenceNo)
                if (!mAgreementResultsList?.referenceNo.isNullOrEmpty()) {
                    mBinding.edtVocherNumber.setText(mAgreementResultsList?.referenceNo)
                    mBinding.llVoucherno.visibility = View.VISIBLE
                } else {
                    mBinding.llVoucherno.visibility = View.GONE
                }
                /*TODO add signature and View Signature implemented Wrongly for now i keep in gone state for future purpose (Vijay) */

                if (!mAgreementResultsList?.awsPath.isNullOrEmpty()) {
                    Glide.with(mBinding.imgSignature.context).load(mAgreementResultsList?.awsPath)
                        .placeholder(R.drawable.ic_place_holder).into(mBinding.imgSignature)

                    mBinding.llSignatureView.visibility = View.GONE
                    mBinding.llSignature.visibility = View.GONE
                } else {
                    mBinding.llSignatureView.visibility = View.GONE
                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
                        mBinding.llSignature.visibility = View.GONE
                    } else {
                        mBinding.llSignature.visibility = View.GONE
                    }
                }

                mBinding.edtRemarks.setText(mAgreementResultsList?.remarks)
                mBinding.edtReferenceNo.setText(mAgreementResultsList?.legalAgreementNo)

                mBinding.edtAgreementDate.setText(serverFormatDatewithTime(mAgreementResultsList?.dueAgreementDate))
                val sCurrentDate: Date = Calendar.getInstance().time
                var agreementDate: Date? = null
                if (!mAgreementResultsList?.dueAgreementDate.isNullOrEmpty()) {
                    agreementDate =
                        parseDate(displayFormatDate(mAgreementResultsList?.dueAgreementDate!!), displayDateFormat)
                } else {
                    agreementDate = sCurrentDate
                }

                if (agreementDate.before(sCurrentDate)){
                    mBinding.edtValidDate.setMinDate(sCurrentDate.time)
                }
                else{
                    mBinding.edtValidDate.setMinDate(agreementDate.time)
                }

                mBinding.edtValidDate.setText(displayFormatDate(mAgreementResultsList?.validUptoDate))
                dueNoticeID = mAgreementResultsList?.dueNoticeID
                dueagreementid = mAgreementResultsList?.dueAgreementID
                noticeReferenceNo = mAgreementResultsList?.referenceNo

//                if (mStatusCodes != null) {
//                    for ((index, obj) in mStatusCodes!!.withIndex()) {
//                        if (mAgreementResultsList?.statusCode?.contentEquals(obj.statusCode!!) == true) {
//                            mBinding.edtStatusSpn.setSelection(index)
//                        }
//                    }
//                }
                if (!mAgreementResultsList?.statusCode.isNullOrEmpty()) {
                    mBinding.tvStatus.text = mAgreementResultsList?.status
                    mBinding.tvStatus.visibility = View.VISIBLE
                    mBinding.edtStatusSpn.visibility = View.GONE
                }
            }
        }
        bindCounts()
    }

    fun onLayoutClick(v: View?) {
        when (v?.id) {
            R.id.btnSave -> {
                if (validateView(v))
                    prepareData()?.let { saveDocument(it, v) }
            }
            R.id.llDocuments -> {
                when {
                    mAgreementResultsList != null && mAgreementResultsList?.dueAgreementID != null && mAgreementResultsList?.dueAgreementID != 0 -> {
//                       val fragment = DueandAgreementDocumentsMasterFragment()
                        val fragment = DueDocumentsMasterFragment()
                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                        bundle.putString(
                            Constant.KEY_PRIMARY_KEY,
                            mAgreementResultsList?.dueAgreementID.toString()
                        )
                        bundle.putString(
                            Constant.KEY_AGREEMENTNO,
                            mAgreementResultsList?.referenceNo ?: ""
                        )
                        fragment.arguments = bundle
                        //endregion
                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                        mListener?.showToolbarBackButton(R.string.agreement_documents)
                        mListener?.addFragment(fragment, true)

                    }
                    validateView(v) -> {
                        prepareData()?.let { saveDocument(it, v) }
                    }
                }

            }
        }
    }

    private fun prepareData(): SaveAgreement? {

        val saveAgreement = SaveAgreement()
        val mDueagreements = Dueagreements()
        mDueagreements.dueAgreementID = mAgreementResultsList?.dueAgreementID
        mDueagreements.dueAgreementDate = serverFormatDateTimeInMilliSecond(
            formatDate(
                mBinding.edtAgreementDate.text.toString().trim(),
                parkingdisplayDateTimeTimeSecondFormat,
                displayDateTimeTimeSecondFormat
            )
        )
        mDueagreements.dueNoticeID = dueNoticeID
        mDueagreements.docid = 0
        mDueagreements.validup2dt = serverFormatDateTimeInMilliSecond(
            formatDate(
                mBinding.edtValidDate.text.toString().trim(),
                displayDateFormat,
                displayDateTimeTimeSecondFormat
            )
        )
        mDueagreements.refno = noticeReferenceNo
        mDueagreements.rmks = mBinding.edtRemarks.text.toString()
        mDueagreements.legalAgreementNo = mBinding.edtReferenceNo.text.toString()
        if (!mAgreementResultsList?.status.isNullOrEmpty()) {
            mDueagreements.stscode = mAgreementResultsList?.statusCode
        } else {
            mDueagreements.stscode =
                (mBinding.edtStatusSpn.selectedItem as COMStatusCode).statusCode
        }

//        val bitmap = mBinding.signatureView.getTransparentSignatureBitmap(true)
//        if (bitmap != null)
//            mDueagreements.filedata = ImageHelper.getBase64String(mBinding.signatureView.signatureBitmap)
//            mDueagreements.fileNameWithExt = dueNoticeID.toString()+".jpg"
//            if (mDueagreements.filedata == null)
//            {
//                mDueagreements.docid = mAgreementResultsList?.documentID
//            }

        saveAgreement.dueagreements = mDueagreements
        return saveAgreement
    }


    private fun saveDocument(saveAgreement: SaveAgreement, view: View) {

        mListener?.showProgressDialog(R.string.msg_please_wait)
        APICall.saveAgreement(
            saveAgreement,
            object : ConnectionCallBack<StoreDueAgreementResponse> {
                override fun onSuccess(response: StoreDueAgreementResponse) {
                    mListener?.dismissDialog()
                    mAgreementResultsList?.dueAgreementID = response.dueAgreementID
                    mAgreementResultsList?.referenceNo = response.referenceNo
                    mListener?.screenMode = Constant.ScreenMode.EDIT
                    if (view.id == mBinding.btnSave.id) {
                        Handler().postDelayed({
                            targetFragment!!.onActivityResult(
                                targetRequestCode,
                                Activity.RESULT_OK,
                                null
                            )
                            mListener!!.popBackStack()
                            mListener?.showToolbarBackButton(R.string.title_agreement_list)
                        }, 500)
                    } else {
                        onLayoutClick(view)
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
//                    mListener?.showAlertDialog(message)
                    mListener?.showAlertDialog(
                        message,
                        DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            mListener?.popBackStack()
                        })
                }

            })
    }


    fun getImageCount(): Int {
        if (mBinding.txtNumberOfDocuments.text.toString().equals("0")) {
            return 0
        }
        return 1
    }


    private fun validateView(v: View): Boolean {

        if (mBinding.edtDueNoticeNumber.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.due_notice_reference_no))
            return false
        }

        if (mBinding.edtAgreementDate.text == null || "" == mBinding.edtAgreementDate.text.toString()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.agreement_date))
            return false
        }

        if (mBinding.edtValidDate.text == null || "" == mBinding.edtValidDate.text.toString()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.valid_upto_date))
            return false
        }
        if (v.id == R.id.btnSave && mBinding.txtNumberOfDocuments.text.toString().equals("0")) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.documents))
            return false
        }

        return true
    }


    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showSnackbarMsg(message: String)
        fun showProgressDialog(message: Int)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun dismissDialog()
        fun popBackStack()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bindCounts()
    }

    private fun bindCounts() {
        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "TableName"
        filterColumn.columnValue = "ACC_DueAgreements"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        filterColumn = FilterColumn()
        filterColumn.columnName = "PrimaryKeyValue"
        filterColumn.columnValue = "${mAgreementResultsList?.dueAgreementID}"
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        fetchCount(listFilterColumn)
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
                if (mListener?.screenMode == Constant.ScreenMode.VIEW && response == 0) {
                    mBinding.llDocuments.isEnabled = false
                }

            }
        })
    }

}

