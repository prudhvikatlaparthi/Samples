package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentDueNoticeImagesBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.SignaturePreviewActivity
import com.sgs.citytax.ui.adapter.HandoverDueImagesAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class DueNoticeImagesFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentDueNoticeImagesBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
    private var isDataSourceChanged = false
    private var mImageFilePath = ""
    private val REQUEST_IMAGE = 100
    private var mDocumentReference: COMDocumentReference? = null

    companion object {
        fun getTableName(screen: Constant.QuickMenu) =
            if (screen == Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_IMAGES)
                "ACC_DueNotices"
            else if (screen == Constant.QuickMenu.QUICK_MENU_STOCK_TRANSFER_IMAGES)
                "INV_StockAllocations"
            else if (screen == Constant.QuickMenu.QUICK_MENU_STOCK_MANAGEMENT_IMAGES)
                "INV_Adjustments"
            else ""

        var mPrimaryKey = ""
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mPrimaryKey = arguments?.getString(Constant.KEY_PRIMARY_KEY) ?: ""

        }
        //endregion
        setViews()
        bindData()
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

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_due_notice_images, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        mBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(),3)
        mBinding.recyclerView.adapter = HandoverDueImagesAdapter(this, fromScreen, mListener?.screenMode)
    }

    private fun bindData() {
      if (!TextUtils.isEmpty(mPrimaryKey)) {
            mListener?.showProgressDialog()
          ObjectHolder.comDocumentReferences.clear()
            APICall.getDueandAgreementDocumentDetails(
                mPrimaryKey,
                getTableName(fromScreen),
                object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        mListener?.dismissDialog()
                        comDocumentReferences = response as ArrayList<COMDocumentReference>
                        val adapter = (mBinding.recyclerView.adapter as HandoverDueImagesAdapter)
                        adapter.clear()
                        adapter.update(response)
                        ObjectHolder.dueDocumentCount = adapter.itemCount
                        ObjectHolder.comDocumentReferences = comDocumentReferences
                        if (comDocumentReferences.size==0)
                        {
                            mBinding.recyclerView.visibility = View.GONE
                            mBinding.txtNoDataFound.visibility = View.VISIBLE
                        }
                        else
                        {
                            mBinding.recyclerView.visibility = View.VISIBLE
                            mBinding.txtNoDataFound.visibility = View.GONE
                        }
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                        (mBinding.recyclerView.adapter as HandoverDueImagesAdapter).clear()
                        mBinding.recyclerView.visibility = View.GONE
                        mBinding.txtNoDataFound.visibility = View.VISIBLE
                        if (message.isNotEmpty()) {
                        }
                        mListener?.showAlertDialog(message)
                    }
                })
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {

                R.id.imgDocument -> {
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, SignaturePreviewActivity::class.java)
                    comDocumentReferences.remove(comDocumentReference)
                    comDocumentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_QUICK_MENU,Constant.QuickMenu.QUICK_MENU_IMAGES)
                    intent.putExtra(Constant.KEY_DOCUMENT, comDocumentReferences)
                    startActivity(intent)
                }

                R.id.btnClearImage -> {
                }
                else -> {

                }
            }
        }
    }



    override fun onLongClick(view: View, position: Int, obj: Any) {

    }



    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)

    }
    fun OnpopBackStack()
    {
        mListener?.popBackStack()
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun popBackStack()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode

    }
}