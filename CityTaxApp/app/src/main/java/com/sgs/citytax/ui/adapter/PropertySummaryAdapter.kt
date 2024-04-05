package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemBusinessSummaryApprovalGroupBinding
import com.sgs.citytax.databinding.ItemPropertyTaxSummaryBinding
import com.sgs.citytax.model.COMPropertyOwner
import com.sgs.citytax.model.PropertyDueSummary
import com.sgs.citytax.model.PropertyTax
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.formatWithPrecision
import java.util.*
import kotlin.collections.HashMap


class PropertySummaryAdapter(private val listener: IClickListener) : BaseExpandableListAdapter() {
    private var group: ArrayList<String> = arrayListOf()
    private var child: HashMap<String, List<Any>> = HashMap()
    private var isParentDocs = false

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
        val binding: ItemPropertyTaxSummaryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context)
                , R.layout.item_property_tax_summary, parent, false)

        binding.llOne.visibility = View.GONE
        binding.llTwo.visibility = View.GONE
        binding.llThree.visibility = View.GONE
        binding.llFour.visibility = View.GONE
        binding.llFive.visibility = View.GONE
        binding.llSix.visibility = View.GONE

        when (getChild(groupPosition, childPosition)) {
            is PropertyDueSummary -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.product)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.invoice_amount)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.invoice_due)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.penalty_amount)
                binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.penalty_due)

                val childData = getChild(groupPosition, childPosition) as PropertyDueSummary
                binding.txtValueOne.text = childData.product ?: ""
                binding.txtValueTwo.text = formatWithPrecision(childData.invoiceAmount)
                binding.txtValueThree.text = formatWithPrecision(childData.invoiceDue)
                binding.txtValueFour.text = formatWithPrecision(childData.penaltyAmount)
                binding.txtValueFive.text = formatWithPrecision(childData.penaltyDue)

                binding.llOne.visibility = View.VISIBLE
                binding.llTwo.visibility = View.VISIBLE
                binding.llThree.visibility = View.VISIBLE
                binding.llFour.visibility = View.VISIBLE
                binding.llFive.visibility = View.VISIBLE
                binding.llSix.visibility = View.VISIBLE

            }

            is PropertyTax -> {
                val childData = getChild(groupPosition, childPosition) as PropertyTax
                childData.documents.let {
                    if (isParentDocs) {
                        binding.rcvDocuments.adapter = ParentDocumentPreviewAdapter(it, listener)
                        binding.rcvDocuments.layoutManager = LinearLayoutManager(parent!!.context, LinearLayoutManager.VERTICAL, true)
                        binding.rcvDocuments.addItemDecoration(DividerItemDecoration(parent!!.context, DividerItemDecoration.VERTICAL))

                        binding.rcvDocuments.visibility = View.VISIBLE
                    } else {
                        binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listener)
                        binding.rcvDocuments.visibility = View.VISIBLE
                    }
                }
                childData.propertyImages.let {
                    binding.rcvPropertyImages.adapter = PropertyImagesPreviewAdapter(it, listener)
                    binding.rcvPropertyImages.visibility = View.VISIBLE
                }
                childData.propertyPlans.let {
                    binding.rcvPropertyPlans.adapter = PropertyPlanImagePreviewAdapter(it, listener)
                    binding.rcvPropertyPlans.visibility = View.VISIBLE
                }
            }

            is COMPropertyOwner -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.property_owner)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.from_date)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.to_date)

                val childData = getChild(groupPosition, childPosition) as COMPropertyOwner

                binding.txtValueOne.text = childData.owner
                binding.txtValueTwo.text = displayFormatDate(childData.fromDate)
                binding.txtValueThree.text = displayFormatDate(childData.toDate)

                binding.llOne.visibility = View.VISIBLE
                binding.llTwo.visibility = View.VISIBLE
                binding.llThree.visibility = View.VISIBLE

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

    fun update(groupName: String, childData: List<Any>, expandableView: ExpandableListView, parentDocs: Boolean = false) {
        group.add(groupName)
        child[groupName] = childData
        expandableView.expandGroup(group.size - 1, true)
        isParentDocs = parentDocs
        notifyDataSetChanged()
    }

}