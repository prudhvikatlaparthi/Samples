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
import com.sgs.citytax.model.LAWViolationType
import com.sgs.citytax.model.LAWViolatorTypes
import com.sgs.citytax.model.MultipleViolationTypes
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.adapter.ViolationTypeAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class ViolationTypeMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var violationTypes: ArrayList<LAWViolationType>? = arrayListOf()
    private var violationClasses: ArrayList<LAWViolationType>? = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must Implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_VIOLATION_TYPES))
                violationTypes = it.getParcelableArrayList<LAWViolationType>(Constant.KEY_VIOLATION_TYPES) as ArrayList<LAWViolationType>

            if (it.containsKey(Constant.KEY_VIOLATION_CLASSES))
                violationClasses = it.getParcelableArrayList<LAWViolationType>(Constant.KEY_VIOLATION_CLASSES) as ArrayList<LAWViolationType>
        }
        //endregion
        setViews()
        bindData()
        setListeners()
    }

    private fun setViews() {
        mBinding.fabAdd.visibility = View.VISIBLE
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mBinding.recyclerView.adapter = ViolationTypeAdapter(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_VIOLATION_TYPE) {
            bindData()
        }
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    private fun bindData() {
        val adapter = (mBinding.recyclerView.adapter as ViolationTypeAdapter)
        adapter.clear()
        adapter.update(ObjectHolder.violations)
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            if (it.id == R.id.fabAdd) {
                val fragment = ViolationTypeEntryFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, violationTypes)
                bundle.putParcelableArrayList(Constant.KEY_VIOLATION_CLASSES, violationClasses)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATION_TYPE)
                mListener?.addFragment(fragment, true)
            }
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    val fragment = ViolationTypeEntryFragment()
                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putParcelable(Constant.KEY_VIOLATIONS, obj as MultipleViolationTypes)
                    bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, violationTypes)
                    bundle.putParcelableArrayList(Constant.KEY_VIOLATION_CLASSES, violationClasses)
                    fragment.arguments = bundle
                    //endregion
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATION_TYPE)
                    mListener?.addFragment(fragment, true)
                }

                R.id.txtDelete -> {
                    deleteViolationType(obj as MultipleViolationTypes)
                }
                else -> {
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun deleteViolationType(violationType: MultipleViolationTypes) {
        if (violationType.violationID != null && !TextUtils.isEmpty(violationType.violationID)) {
            val list = ObjectHolder.violations
            list.remove(violationType)
            ObjectHolder.violations = list
            bindData()
        }
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
    }
}