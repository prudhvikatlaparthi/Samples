package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.PropertyOwners
import com.sgs.citytax.api.response.StorePropertyOwnershipWithPropertyOwnerResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.ui.adapter.PropertyOwnerAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class PropertyOwnerMasterFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var propertyId: Int? = 0
    private var propertyOwnerShip: Int? = 0
    private var fromScreen: Constant.QuickMenu? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implemeent Listener")
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
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
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.fabAdd.visibility = View.GONE
        }
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        APICall.getPropertyOwnersDetails(propertyId
            ?: 0, object : ConnectionCallBack<PropertyOwners> {
            override fun onSuccess(response: PropertyOwners) {
                mListener?.dismissDialog()
                response.let {
                    if (it?.propertyOwners != null && it?.propertyOwners.isNotEmpty()) {
                        mBinding.recyclerView.addItemDecoration(
                            DividerItemDecoration(
                                requireContext(),
                                LinearLayoutManager.VERTICAL
                            )
                        )
                        // Log.e("property owners", ">>>>>>..${it.propertyOwners}")
                        if (it.propertyOwners.size > 0)
                            propertyOwnerShip = it.propertyOwners[0].propertyOwnershipID

                        mBinding.recyclerView.adapter = PropertyOwnerAdapter(
                            it.propertyOwners,
                            this@PropertyOwnerMasterFragment
                        )

                    } //else
                    //mListener?.showAlertDialog(getString(R.string.msg_no_data))
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                ObjectHolder.notes.clear()
                ObjectHolder.documents.clear()
                val fragment = PropertyOwnerEntryFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, propertyId ?: 0)
//            bundle.putInt(Constant.KEY_PROPERTY_OWNERSHIP, propertyOwnerShip ?: 0)
                fragment.arguments = bundle
                fragment.setTargetFragment(
                    this@PropertyOwnerMasterFragment,
                    Constant.REQUEST_CODE_PROPERTY_OWNER_LIST
                )
                mListener?.addFragment(fragment, true)
            }
        })

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                }
                R.id.txtDelete -> {

                }
                R.id.txtPropEdit -> {
                    mListener?.screenMode = Constant.ScreenMode.VIEW
                    var info = obj as StorePropertyOwnershipWithPropertyOwnerResponse
                    ObjectHolder.notes.clear()
                    ObjectHolder.documents.clear()
                    val fragment = PropertyOwnerEntryFragment()
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, propertyId ?: 0)
                    bundle.putInt(Constant.KEY_PROPERTY_OWNERSHIP, info.propertyOwnershipID ?: 0)
                    bundle.putParcelable(
                        Constant.KEY_PROPERTY_OWNER_DETAILS,
                        obj as StorePropertyOwnershipWithPropertyOwnerResponse
                    )
                    fragment.arguments = bundle
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_OWNER_LIST)
                    mListener?.addFragment(fragment, true)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bindData()
    }
}