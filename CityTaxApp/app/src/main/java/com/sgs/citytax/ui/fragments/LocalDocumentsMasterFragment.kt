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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.DocumentAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class LocalDocumentsMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {

    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var mPropertyOwnerShip: Int = 0

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()

    override fun initComponents() {
        arguments?.let {
            if (arguments?.containsKey(Constant.KEY_QUICK_MENU)!!)
                fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_PROPERTY_OWNERSHIP))
                mPropertyOwnerShip = it.getInt(Constant.KEY_PROPERTY_OWNERSHIP)
        }
        setViews()
        bindData()
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
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.fabAdd.visibility = View.GONE

        } else {
            mBinding.fabAdd.visibility = View.VISIBLE
        }
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mBinding.recyclerView.adapter = DocumentAdapter(this, Constant.QuickMenu.QUICK_MENU_NONE, mListener?.screenMode)
    }

    private fun bindData() {
        val adapter = (mBinding.recyclerView.adapter as DocumentAdapter)
        adapter.clear()
        adapter.update(ObjectHolder.documents)
    }

    private fun setListeners() {
       // mBinding.fabAdd.setOnClickListener(this)
        mBinding.fabAdd.setOnClickListener(object:OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = LocalDocumentEntryFragment()
                fragment.setTargetFragment(this@LocalDocumentsMasterFragment, Constant.REQUEST_CODE_DOCUMENT_ENTRY)
                mListener?.showToolbarBackButton(R.string.documents)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    override fun onClick(v: View?) {

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    val fragment = LocalDocumentEntryFragment()
                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putParcelable(Constant.KEY_DOCUMENT, obj as COMDocumentReference?)
                    fragment.arguments = bundle
                    //endregion
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENT_ENTRY)
                    mListener?.showToolbarBackButton(R.string.documents)
                    mListener?.addFragment(fragment, true)
                }

                R.id.imgDocument -> {
                    //Don't Show Preview
                    /*val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    comDocumentReferences.remove(comDocumentReference)
                    comDocumentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, comDocumentReferences)
                    startActivity(intent)*/
                }

                R.id.txtDelete -> {
                    deleteDocument(obj as COMDocumentReference)
                }
                else -> {

                }
            }
        }
    }

    private fun deleteDocument(comDocumentReference: COMDocumentReference?) {
        if (null != comDocumentReference?.documentReferenceID && !TextUtils.isEmpty(comDocumentReference.documentReferenceID)) {
            val list = ObjectHolder.documents
            list.remove(comDocumentReference)
            ObjectHolder.documents = list
            bindData()
        }
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_DOCUMENT_ENTRY) {
            bindData()
        }
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode
    }
}