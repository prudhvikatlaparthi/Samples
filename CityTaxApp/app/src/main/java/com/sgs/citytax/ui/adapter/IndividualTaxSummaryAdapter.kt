package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.BusinessTaxDueYearSummary
import com.sgs.citytax.databinding.ItemBusinessSummaryApprovalGroupBinding
import com.sgs.citytax.databinding.ItemIndividualTaxSummaryBinding
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.model.GamingMachineTax
import com.sgs.citytax.model.Weapon
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision
import java.util.*
import kotlin.collections.HashMap

class IndividualTaxSummaryAdapter(private val listener: IClickListener) : BaseExpandableListAdapter() {

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
        val binding: ItemIndividualTaxSummaryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.item_individual_tax_summary, parent, false)

        binding.llOne.visibility = GONE
        binding.llTwo.visibility = GONE
        binding.llThree.visibility = GONE
        binding.llFour.visibility = GONE
        binding.llFive.visibility = GONE
        binding.llSix.visibility = GONE
        binding.llSeven.visibility = GONE
        binding.llEight.visibility = GONE
        binding.llNine.visibility = GONE
        binding.llTen.visibility = GONE
        binding.rcvDocuments.visibility = GONE

        when (getChild(groupPosition, childPosition)) {

            is BusinessTaxDueYearSummary -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.product)
                binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.invoice_amount)
                binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.invoice_due)
                binding.txtKeyEight.text = parent?.context?.resources?.getString(R.string.penalty_amount)
                binding.txtKeyNine.text = parent?.context?.resources?.getString(R.string.penalty_due)

                val childData = getChild(groupPosition, childPosition) as BusinessTaxDueYearSummary

//                binding.txtValueOne.text = childData.product ?: ""
                binding.txtValueOne.text = if (childData.taxSubType == null || childData.taxSubType.isNullOrEmpty()) childData.product
                        ?: "" else childData.taxSubType
                binding.txtValueSix.text = formatWithPrecision(childData.invoiceAmount)
                binding.txtValueSeven.text = formatWithPrecision(childData.invoiceDue)
                binding.txtValueEight.text = formatWithPrecision(childData.penaltyAmount)
                binding.txtValueNine.text = formatWithPrecision(childData.penaltyDue)

                binding.llOne.visibility = VISIBLE
                binding.llSix.visibility = VISIBLE
                binding.llSeven.visibility = VISIBLE
                binding.llEight.visibility = VISIBLE
                binding.llNine.visibility = VISIBLE

            }
            is CartTax -> {
                val childData = getChild(groupPosition, childPosition) as CartTax
                childData.attachment?.let {
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listener)
                    binding.rcvDocuments.visibility = VISIBLE
                }
            }

            is Weapon -> {
                val childData = getChild(groupPosition, childPosition) as Weapon
                childData.attachment?.let {
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listener)
                    binding.rcvDocuments.visibility = VISIBLE
                }
            }

            is GamingMachineTax -> {
                val childData = getChild(groupPosition, childPosition) as GamingMachineTax
                childData.attachment?.let {
                    binding.rcvDocuments.adapter = BusinessDocumentPreviewAdapter(it, listener)
                    binding.rcvDocuments.visibility = VISIBLE
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