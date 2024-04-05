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
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.PropertyImageResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMPropertyImage
import com.sgs.citytax.ui.PropertyImagesPreviewActivity
import com.sgs.citytax.ui.adapter.PropertyImageAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.LogHelper
import com.sgs.citytax.util.OnSingleClickListener

class PropertyImageMasterFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var propertyId: Int? = 0
    private var fromScreen: Constant.QuickMenu? = null
    private var propertyImages: ArrayList<COMPropertyImage> = arrayListOf()

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
        showViewsEnabled()
        bindData()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_IMAGE) {
            bindData()
        }
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyId = it.getInt(Constant.KEY_PRIMARY_KEY)

            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun showViewsEnabled() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
            mBinding.fabAdd.visibility = View.GONE
        }
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        APICall.getPropertyImages(propertyId
            ?: 0, object : ConnectionCallBack<PropertyImageResponse> {
            override fun onSuccess(response: PropertyImageResponse) {
                mListener?.dismissDialog()
                response.let {
                    propertyImages = response.propertyImages
                    mBinding.recyclerView.addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            LinearLayoutManager.VERTICAL
                        )
                    )
                    mBinding.recyclerView.adapter = PropertyImageAdapter(
                        it.propertyImages,
                        this@PropertyImageMasterFragment,
                        mListener?.screenMode
                    )
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
                val fragment = PropertyImageEntryFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putInt(Constant.KEY_PRIMARY_KEY, propertyId ?: 0)
                fragment.arguments = bundle
                fragment.setTargetFragment(
                    this@PropertyImageMasterFragment,
                    Constant.REQUEST_CODE_PROPERTY_IMAGE
                )
                mListener?.addFragment(fragment, true)
            }
        })

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {
                    val fragment = PropertyImageEntryFragment()
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putInt(Constant.KEY_PRIMARY_KEY, propertyId ?: 0)
                    bundle.putParcelable(Constant.KEY_PROPERTY_IMAGE, obj as COMPropertyImage)
                    fragment.arguments = bundle
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_PROPERTY_IMAGE)
                    mListener?.addFragment(fragment, true)
                }
                R.id.txtDelete -> {
                    val propertyImage = obj as COMPropertyImage
                    deleteImage(propertyImage.propertyImageID ?: 0)
                }
                R.id.img_property -> {
                    val image = obj as COMPropertyImage
                    val intent = Intent(context, PropertyImagesPreviewActivity::class.java)
                    propertyImages.remove(image)
                    propertyImages.add(0, image)
                    intent.putExtra(Constant.KEY_PROPERTY_IMAGE, propertyImages)
                    startActivity(intent)
                }
                else -> {
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun deleteImage(propertyImageId: Int) {
        mListener?.showProgressDialog()
        APICall.deletePropertyImage(propertyImageId, object : ConnectionCallBack<Boolean> {
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
}