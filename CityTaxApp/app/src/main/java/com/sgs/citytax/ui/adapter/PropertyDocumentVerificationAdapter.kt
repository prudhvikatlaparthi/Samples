package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.response.PropertyImageResponse
import com.sgs.citytax.api.response.PropertyPlanImageResponse
import com.sgs.citytax.databinding.ItemBusinessSummaryApprovalGroupBinding
import com.sgs.citytax.databinding.ItemPropertyDocumentVerificationBinding
import com.sgs.citytax.databinding.ItemPropertyTaxSummaryBinding
import com.sgs.citytax.model.COMPropertyOwner
import com.sgs.citytax.model.PendingRequestList
import com.sgs.citytax.model.PropertyDueSummary
import com.sgs.citytax.model.PropertyTax
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.formatWithPrecision
import java.util.ArrayList

class PropertyDocumentVerificationAdapter(private val listener: IClickListener) : BaseExpandableListAdapter(){

    private var group: ArrayList<String> = arrayListOf()
    private var child: HashMap<String, List<Any>> = HashMap()

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        if (group.isEmpty() || child.isEmpty() || group[groupPosition].isEmpty() || child[group[groupPosition]] == null || child[group[groupPosition]]!!.isEmpty())
            return -1
        return child[group[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        if (group.isEmpty() || child.isEmpty() || group[groupPosition].isEmpty() || child[group[groupPosition]] == null || child[group[groupPosition]]!!.isEmpty())
            return -1
        return child[group[groupPosition]]!!.size
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View {
        val binding: ItemPropertyDocumentVerificationBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context)
                , R.layout.item_property_document_verification, parent, false)

        when (getChild(groupPosition, childPosition)) {

            is PendingRequestList -> {
                val childData = getChild(groupPosition, childPosition) as PendingRequestList
                childData.documents?.let {
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listener)
                    binding.rcvDocuments.visibility = View.VISIBLE
                }
            }

           is PropertyImageResponse ->{
               val childData = getChild(groupPosition,childPosition) as PropertyImageResponse
               childData.propertyImages.let {
                   binding.rcvPropertyImages.adapter = PropertyImagesPreviewAdapter(it, listener)
                   binding.rcvPropertyImages.visibility = View.VISIBLE
               }
           }

            is PropertyPlanImageResponse ->{
                val childData = getChild(groupPosition,childPosition) as PropertyPlanImageResponse
                childData.propertyplans.let {
                    binding.rcvPropertyPlans.adapter = PropertyPlanImagePreviewAdapter(it, listener)
                    binding.rcvPropertyImages.visibility = View.VISIBLE
                }
            }

        }
        return binding.root
    }

    override fun getGroup(groupPosition: Int): Any {
        return group[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return group.size
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View {
        val binding: ItemBusinessSummaryApprovalGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.item_business_summary_approval_group, parent, false)

        binding.header.text = getGroup(groupPosition) as String

        return binding.root
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun update(groupName: String, childData: List<Any>, expandableView: ExpandableListView) {
        group.add(groupName)
        child[groupName] = childData
        expandableView.expandGroup(group.size - 1, true)
        notifyDataSetChanged()
    }


}