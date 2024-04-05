package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.adapter.ImpoundmentTypeAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class ImpoundmentTypeMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var mImpoundmentTypes: ArrayList<LAWImpoundmentType>? = arrayListOf()
    private var mImpoundmentSubTypes: ArrayList<LAWImpoundmentSubType>? = arrayListOf()
    private var mImpoundmentReasons: ArrayList<LAWImpoundmentReason>? = arrayListOf()
    private var mViolationTypes: ArrayList<LAWViolationType>? = arrayListOf()
    private var mPoliceStationYards: ArrayList<PoliceStationYards> = arrayListOf()
    private var mCraneTypes: ArrayList<VULAWTowingCraneTypes> = arrayListOf()

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
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_TYPES))
                mImpoundmentTypes = it.getParcelableArrayList<LAWImpoundmentType>(Constant.KEY_IMPOUNDMENT_TYPES) as ArrayList<LAWImpoundmentType>
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_SUB_TYPES))
                mImpoundmentSubTypes = it.getParcelableArrayList<LAWImpoundmentSubType>(Constant.KEY_IMPOUNDMENT_SUB_TYPES) as ArrayList<LAWImpoundmentSubType>
            if (it.containsKey(Constant.KEY_IMPOUNDMENT_REASONS))
                mImpoundmentReasons = it.getParcelableArrayList<LAWImpoundmentReason>(Constant.KEY_IMPOUNDMENT_REASONS) as ArrayList<LAWImpoundmentReason>
            if (it.containsKey(Constant.KEY_VIOLATION_TYPES))
                mViolationTypes = it.getParcelableArrayList<LAWViolationType>(Constant.KEY_VIOLATION_TYPES) as ArrayList<LAWViolationType>
            if (it.containsKey(Constant.KEY_POLICE_STATION_YARDS))
                mPoliceStationYards = it.getParcelableArrayList<PoliceStationYards>(Constant.KEY_POLICE_STATION_YARDS) as ArrayList<PoliceStationYards>
            if (it.containsKey(Constant.KEY_CRANE_TYPES))
                mCraneTypes = it.getParcelableArrayList<VULAWTowingCraneTypes>(Constant.KEY_CRANE_TYPES) as ArrayList<VULAWTowingCraneTypes>
        }
        //endregion
        setViews()
        bindData()
        setListeners()
    }

    private fun setViews() {
        mBinding.fabAdd.visibility = View.VISIBLE
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mBinding.recyclerView.adapter = ImpoundmentTypeAdapter(this)

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
        val adapter = (mBinding.recyclerView.adapter as ImpoundmentTypeAdapter)
        adapter.clear()
        adapter.update(ObjectHolder.impoundments)
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = ImpoundmentTypeEntryFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_TYPES, mImpoundmentTypes)
                bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_SUB_TYPES, mImpoundmentSubTypes)
                bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_REASONS, mImpoundmentReasons)
                bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, mViolationTypes)
                bundle.putParcelableArrayList(Constant.KEY_POLICE_STATION_YARDS, mPoliceStationYards)
                bundle.putParcelableArrayList(Constant.KEY_CRANE_TYPES,mCraneTypes)
                fragment.arguments = bundle
                fragment.setTargetFragment(this@ImpoundmentTypeMasterFragment, Constant.REQUEST_CODE_VIOLATION_TYPE)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    override fun onClick(view: View?) {
        view?.let {
           /* if (it.id == R.id.fabAdd) {
                val fragment = ImpoundmentTypeEntryFragment()
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_TYPES, mImpoundmentTypes)
                bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_SUB_TYPES, mImpoundmentSubTypes)
                bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_REASONS, mImpoundmentReasons)
                bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, mViolationTypes)
                bundle.putParcelableArrayList(Constant.KEY_POLICE_STATION_YARDS, mPoliceStationYards)
                bundle.putParcelableArrayList(Constant.KEY_CRANE_TYPES,mCraneTypes)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATION_TYPE)
                mListener?.addFragment(fragment, true)
            }*/
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    val fragment = ImpoundmentTypeEntryFragment()
                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putParcelable(Constant.KEY_IMPOUNDMENTS, obj as MultipleImpoundmentTypes)
                    bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_TYPES, mImpoundmentTypes)
                    bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_SUB_TYPES, mImpoundmentSubTypes)
                    bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_REASONS, mImpoundmentReasons)
                    bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, mViolationTypes)
                    bundle.putParcelableArrayList(Constant.KEY_POLICE_STATION_YARDS, mPoliceStationYards)
                    bundle.putParcelableArrayList(Constant.KEY_CRANE_TYPES,mCraneTypes)
                    fragment.arguments = bundle
                    //endregion
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_VIOLATION_TYPE)
                    mListener?.addFragment(fragment, true)
                }

                R.id.txtDelete -> {
                    deleteViolationType(obj as MultipleImpoundmentTypes)
                }
                else -> {
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun deleteViolationType(impoundmentType: MultipleImpoundmentTypes) {
        if (impoundmentType.impoundmentTypeID != null && impoundmentType.impoundmentTypeID != 0) {
            val list = ObjectHolder.impoundments
            list.remove(impoundmentType)
            ObjectHolder.impoundments = list
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