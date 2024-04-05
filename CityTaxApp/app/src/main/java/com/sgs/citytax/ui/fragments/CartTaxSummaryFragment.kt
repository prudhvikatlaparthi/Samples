package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.BusinessTaxDueYearSummary
import com.sgs.citytax.api.payload.GetIndividualTax
import com.sgs.citytax.api.payload.GetIndividualTaxDueYearSummary
import com.sgs.citytax.api.payload.GetQrNoteAndLogoPayload
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.api.response.OrgData
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentCartTaxSummaryBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.IndividualTaxSummaryAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.fragment_cart_tax_summary.*

class CartTaxSummaryFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentCartTaxSummaryBinding
    private var mListener: Listener? = null
    private var sycoTaxID: String? = ""
    private var mCartTax: CartTax? = null
    private var individualTaxSummary: List<BusinessTaxDueYearSummary> = arrayListOf()
    private val printHelper = PrintHelper()
    var orgData: List<OrgData>? = null

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                sycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)
        }
        //endregion
        setView()
        getCartTax()
        setListeners()
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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart_tax_summary, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setView() {
        mBinding.listView.setAdapter(IndividualTaxSummaryAdapter(this))
    }

    private fun bindData() {
        mCartTax?.let { cartTax ->

            cartTax.cartID?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.INDIVIDUAL_TAX_SUMMARY, it.toString(), cartTax.cartSycoTaxID))
                mBinding.imgQRCode.visibility = VISIBLE
                CommonLogicUtils.checkNUpdateQRCodeNotes(mBinding.qrCodeWrapper)
            }
            cartTax.cartSycoTaxID?.let {
                mBinding.txtSycoTaxID.text = it
                mBinding.llSycoTaxID.visibility = VISIBLE
            }
            cartTax.registrationDate?.let {
                mBinding.txtRegistrationDate.text = displayFormatDate(it)
                mBinding.llRegistrationDate.visibility = VISIBLE
            }
            cartTax.cartNo?.let {
                mBinding.txtCartNo.text = it
                mBinding.llCartNo.visibility = VISIBLE
            }
            cartTax.cartType?.let {
                mBinding.txtCartType.text = it
                mBinding.llCartType.visibility = VISIBLE
            }
            cartTax.estimatedTax?.let {
                mBinding.txtEstimatedTaxAmount.text = formatWithPrecision(it)
                mBinding.llEstimatedTaxAmount.visibility = VISIBLE
            }
            cartTax.active?.let {
                mBinding.txtStatus.text = if (it == "Y") getString(R.string.active) else getString(R.string.inactive)
                mBinding.llStatus.visibility = VISIBLE
            }
            cartTax.accountName?.let {
                mBinding.txtOwnerName.text = it
                mBinding.llOwnerName.visibility = VISIBLE
            }
            cartTax.accountPhone?.let {
                mBinding.txtPhoneNumber.text = it
                mBinding.llPhoneNumber.visibility = VISIBLE
            }

            mBinding.llOutstandings.visibility = VISIBLE

            //region Outstandings
            getOutStanding()
        }
        val payload = GetQrNoteAndLogoPayload()
        APICall.getQrNoteAndLogo(payload, object : ConnectionCallBack<List<OrgData>> {
            override fun onSuccess(response: List<OrgData>)
            {
                orgData  = response
            }

            override fun onFailure(message: String) {

            }
        })
    }

    private fun getCartTax() {
        mListener?.showProgressDialog()
        val getIndividualTax = GetIndividualTax()
        getIndividualTax.columnName = "CartSycotaxID"
        getIndividualTax.sycoTaxID = sycoTaxID
        getIndividualTax.tableName = "VU_CRM_Carts"
        APICall.getIndividualTaxForCart(getIndividualTax, object : ConnectionCallBack<CartTax> {
            override fun onSuccess(response: CartTax) {
                mListener?.dismissDialog()
                mCartTax = response
                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun setAdapter(response: DataResponse) {
        if (response.individualTaxSummary.isNotEmpty()) {
            individualTaxSummary = response.individualTaxSummary.sortedByDescending { it.year }
            val list = individualTaxSummary.groupBy { it.year }
            for (group in list) {
                group.key?.let {
                    (mBinding.listView.expandableListAdapter as IndividualTaxSummaryAdapter).update(getString(R.string.tax_year) + " " + group.key.toString(), group.value, mBinding.listView)
                }
            }
        }
    }

    private fun getOutStanding() {
        mListener?.showProgressDialog()
        val getIndividualTaxDueYearSummary = GetIndividualTaxDueYearSummary()
        getIndividualTaxDueYearSummary.taxRuleBookCode = Constant.TaxRuleBook.CART.Code
        getIndividualTaxDueYearSummary.voucherNo = mCartTax?.cartID ?: 0
        APICall.getIndividualTaxDueYearSummary(getIndividualTaxDueYearSummary, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()
                setAdapter(response)
                getDocuments()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                getDocuments()
            }
        })
    }

    private fun getDocuments() {
        mListener?.showProgressDialog()
        APICall.getDocumentDetails("${mCartTax?.cartID ?: 0}", "CRM_Carts", object : ConnectionCallBack<List<COMDocumentReference>> {
            override fun onSuccess(response: List<COMDocumentReference>) {
                mListener?.dismissDialog()
                val comDocumentReferences = response as ArrayList<COMDocumentReference>
                val list = arrayListOf<CartTax>()
                val cartTax = CartTax()
                cartTax.attachment = comDocumentReferences
                list.add(cartTax)
                (mBinding.listView.expandableListAdapter as IndividualTaxSummaryAdapter).update(getString(R.string.documents), list, mBinding.listView)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.itemImageDocumentPreview -> {
                    val documentReferences: ArrayList<COMDocumentReference> = view.tag as ArrayList<COMDocumentReference>
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    documentReferences.remove(comDocumentReference)
                    documentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, documentReferences)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun setListeners() {
        mBinding.btnPrint.setOnClickListener {
            try {
                printHelper.printCartSummaryContent(requireContext(), mCartTax, individualTaxSummary, MyApplication.getPrefHelper().language,orgData)
                mListener?.finish()
            }catch (e:Exception){
                mListener?.showAlertDialog(getString(R.string.msg_print_not_support))
            }

        }
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
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
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?, view: View?)
    }

}