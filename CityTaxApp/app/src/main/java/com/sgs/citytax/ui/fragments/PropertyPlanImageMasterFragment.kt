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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.PropertyPlanImageResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMPropertyPlanImage
import com.sgs.citytax.ui.PropertyPlansPreviewActivity
import com.sgs.citytax.ui.adapter.PropertyPlanImageAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class PropertyPlanImageMasterFragment : BaseFragment(), IClickListener{
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var propertyPlans: ArrayList<COMPropertyPlanImage> = arrayListOf()

    private var propertyId: Int? = 0
    private var fromScreen: Constant.QuickMenu? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
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

    override fun initComponents() {
        processIntent()
        setViews()
        bindData()
        setListeners()
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyId = it.getInt(Constant.KEY_PRIMARY_KEY)

            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }


    private fun setViews() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY) {
            mBinding.fabAdd.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_PLAN_IMAGE) {
            bindData()
        }
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        APICall.getPropertyPlans(propertyId
                ?: 0, object : ConnectionCallBack<PropertyPlanImageResponse> {
            override fun onSuccess(response: PropertyPlanImageResponse) {
                mListener?.dismissDialog()
                response.let {
                    propertyPlans = it.propertyplans
                    // mBinding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                    mBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(),3)
                    mBinding.recyclerView.adapter = PropertyPlanImageAdapter(it.propertyplans,
                            this@PropertyPlanImageMasterFragment, mListener?.screenMode, fromScreen)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }



    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object:OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = PropertyPlanImageEntryFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, propertyId ?: 0)
                fragment.arguments = bundle
                fragment.setTargetFragment(this@PropertyPlanImageMasterFragment, Constant.REQUEST_CODE_PROPERTY_PLAN_IMAGE)
                mListener?.addFragment(fragment, true)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.txtEdit -> {
                    val fragment = PropertyPlanImageEntryFragment()
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, propertyId ?: 0)
                    bundle.putParcelable(Constant.KEY_PROPERTY_PLAN_IMAGE, obj as COMPropertyPlanImage)
                    fragment.arguments = bundle
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_PLAN_IMAGE)
                    mListener?.addFragment(fragment, true)
                }

                R.id.btnClearImage ->{
                    val image = obj as COMPropertyPlanImage
                    delete(image.propertyPlanId ?: 0)
                }
                R.id.img_property -> {
                    val image = obj as COMPropertyPlanImage
                    val intent = Intent(context, PropertyPlansPreviewActivity::class.java)
                    propertyPlans.remove(image)
                    propertyPlans.add(0, image)
                    intent.putExtra(Constant.KEY_PROPERTY_PLAN_IMAGE, propertyPlans)
                    startActivity(intent)
                }
                else -> {

                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }


    private fun delete(propertyPlanId: Int) {
        mListener?.showProgressDialog()
        APICall.deletePropertyPlanImage(propertyPlanId, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }


    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showSnackbarMsg(message: String)
        fun showProgressDialog()
        fun dismissDialog()
        fun popBackStack()
        fun showToast(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }
}