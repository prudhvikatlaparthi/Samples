package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.internal.LinkedTreeMap
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentParentPropertyPlanImageBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMPropertyImage
import com.sgs.citytax.model.COMPropertyPlanImage
import com.sgs.citytax.model.PropertyTax
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.PropertyImagesPreviewActivity
import com.sgs.citytax.ui.PropertyPlansPreviewActivity
import com.sgs.citytax.ui.adapter.PropertySummaryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener


class ParentPropertyPlanImageFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentParentPropertyPlanImageBinding
    private var mListener: Listener? = null
    var mPropertyID: Int = 0
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var mParentType: String = ""

    companion object {
        @JvmStatic
        fun newInstance(propertyID: Int, fromScreenMode: Constant.QuickMenu, parentType: String) = ParentPropertyPlanImageFragment().apply {
            mPropertyID = propertyID
            mCode = fromScreenMode
            mParentType = parentType

        }
    }

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_parent_property_plan_image, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        setViews()

        when (mParentType) {
            //property
            getString(R.string.parent_property_plan_documents) -> getParentPlanImages()
            getString(R.string.parent_property_images) -> getParentImages()
            getString(R.string.parent_documents_property) -> getParentDocuments()
            //land
            getString(R.string.parent_land_plan_images) -> getParentPlanImages()
            getString(R.string.parent_land_images) -> getParentImages()
            getString(R.string.parent_documents_land) -> getParentDocuments()
        }

    }

    private fun setViews() {
        mBinding.listView.setAdapter(PropertySummaryAdapter(this))
    }

    private fun getParentPlanImages() {
        mListener?.showProgressDialog()
        APICall.getParentPropertyPlanImages(mPropertyID
                ?: 0, false, object : ConnectionCallBack<LinkedTreeMap<String, java.util.ArrayList<String>>> {
            override fun onSuccess(response: LinkedTreeMap<String, java.util.ArrayList<String>>) {
                try {

                    mListener?.dismissDialog()
                    for ((key, list) in response) {
                        val gson = Gson()
                        val json = gson.toJson(list, ArrayList::class.java)
                        var objList = getObjectList(json, COMPropertyPlanImage::class.java)
                        val listArry = arrayListOf<PropertyTax>()
                        val propertyTax = PropertyTax()
                        propertyTax.propertyPlans = objList as ArrayList<COMPropertyPlanImage>
                        listArry.add(propertyTax)

                        if (listArry.isNotEmpty()) {
                            (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(key, listArry, mBinding.listView)
                        } else {
                            mBinding.errorView.isVisible = true
                        }

                    }
                } catch (e: java.lang.Exception) {
                    mBinding.errorView.isVisible = true
                    Log.e("exp in e", ">>>>>>>>>>${e.localizedMessage}")
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding.errorView.isVisible = true
            }
        })
    }

    private fun getParentImages() {
        mListener?.showProgressDialog()
        APICall.getParentPropertyImages(mPropertyID
                ?: 0, false, object : ConnectionCallBack<LinkedTreeMap<String, java.util.ArrayList<String>>> {
            override fun onSuccess(response: LinkedTreeMap<String, java.util.ArrayList<String>>) {
                try {

                    mListener?.dismissDialog()
                    for ((key, list) in response) {
                        val gson = Gson()
                        val json = gson.toJson(list, ArrayList::class.java)
                        var objList = getObjectList(json, COMPropertyImage::class.java)
                        val listArry = arrayListOf<PropertyTax>()
                        val propertyTax = PropertyTax()
                        propertyTax.propertyImages = objList as ArrayList<COMPropertyImage>
                        listArry.add(propertyTax)

                        if (listArry.isNotEmpty()) {
                            (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(key, listArry, mBinding.listView)
                        } else {
                            mBinding.errorView.isVisible = true
                        }

                    }
                } catch (e: java.lang.Exception) {
                    Log.e("exp in e", ">>>>>>>>>>${e.localizedMessage}")
                    mBinding.errorView.isVisible = true
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding.errorView.isVisible = true
            }
        })
    }

    private fun getParentDocuments() {
        mListener?.showProgressDialog()
        APICall.getParentPropertyDocuments(mPropertyID
                ?: 0, false, object : ConnectionCallBack<LinkedTreeMap<String, java.util.ArrayList<String>>> {
            override fun onSuccess(response: LinkedTreeMap<String, java.util.ArrayList<String>>) {
                try {

                    mListener?.dismissDialog()
                    for ((key, list) in response) {
                        val gson = Gson()
                        val json = gson.toJson(list, ArrayList::class.java)
                        var objList = getObjectList(json, COMDocumentReference::class.java)
                        val listArry = arrayListOf<PropertyTax>()
                        val propertyTax = PropertyTax()
                        propertyTax.documents = objList as ArrayList<COMDocumentReference>
                        listArry.add(propertyTax)

                        if (listArry.isNotEmpty()) {
                            (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(key, listArry, mBinding.listView, true)
                        } else {
                            mBinding.errorView.isVisible = true
                        }

                    }
                } catch (e: java.lang.Exception) {
                    mBinding.errorView.isVisible = true
                    Log.e("exp in e", ">>>>>>>>>>${e.localizedMessage}")
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding.errorView.isVisible = true
            }
        })
    }


    fun <T> getObjectList(jsonString: String?, cls: Class<T>?): List<T>? {
        val list: MutableList<T> = ArrayList()
        try {
            val gson = Gson()
            val arry = JsonParser().parse(jsonString).asJsonArray
            for (jsonElement in arry) {
                list.add(gson.fromJson(jsonElement, cls))
            }
        } catch (e: Exception) {
            Log.e("exp in objlist", ">>>>>>>>>>>>>${e.localizedMessage}")
        }
        return list
    }


    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.itemImageDocumentPreview -> {
                    val documentReferences: ArrayList<COMDocumentReference> = view.tag as ArrayList<COMDocumentReference>
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    documentReferences.remove(comDocumentReference)
                    documentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, documentReferences)
                    startActivity(intent)
                }
                R.id.itemPropertyImagePreview -> {
                    val images = view.tag as ArrayList<COMPropertyImage>
                    val image = obj as COMPropertyImage
                    val intent = Intent(context, PropertyImagesPreviewActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                    images.remove(image)
                    images.add(0, image)
                    intent.putExtra(Constant.KEY_PROPERTY_IMAGE, images)
                    startActivity(intent)
                }
                R.id.itemPropertyPlanImagePreview -> {
                    val plans = view.tag as ArrayList<COMPropertyPlanImage>
                    val plan = obj as COMPropertyPlanImage
                    val intent = Intent(context, PropertyPlansPreviewActivity::class.java)
                    intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
                    plans.remove(plan)
                    plans.add(0, plan)
                    intent.putExtra(Constant.KEY_PROPERTY_PLAN_IMAGE, plans)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

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