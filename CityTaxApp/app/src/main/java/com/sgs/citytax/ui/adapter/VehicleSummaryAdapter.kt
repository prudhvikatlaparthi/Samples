package com.sgs.citytax.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemBusinessSummaryApprovalChildBinding
import com.sgs.citytax.databinding.ItemBusinessSummaryApprovalGroupBinding
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.util.IClickListener
import java.util.*
import kotlin.collections.HashMap

class VehicleSummaryAdapter(private val listner: IClickListener) : BaseExpandableListAdapter() {

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
        val binding: ItemBusinessSummaryApprovalChildBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.item_business_summary_approval_child, parent, false)

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
        binding.llEleven.visibility = GONE

        when (getChild(groupPosition, childPosition)) {
            is GeoAddress -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.plot)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.door_no)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.block)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.street)
                binding.txtKeyFive.text = parent?.context?.resources?.getString(R.string.sector)
                binding.txtKeySix.text = parent?.context?.resources?.getString(R.string.zone)
                binding.txtKeySeven.text = parent?.context?.resources?.getString(R.string.city)
                binding.txtKeyEight.text = parent?.context?.resources?.getString(R.string.state)
                val childData = getChild(groupPosition, childPosition) as GeoAddress

                if (!TextUtils.isEmpty(childData.plot)) {
                    childData.plot?.let {
                        binding.txtValueOne.text = childData.plot
                        binding.llOne.visibility = VISIBLE
                    }
                }
                if (!TextUtils.isEmpty(childData.doorNo)) {
                    childData.doorNo?.let {
                        binding.txtValueTwo.text = childData.doorNo
                        binding.llTwo.visibility = VISIBLE
                    }
                }
                if (!TextUtils.isEmpty(childData.block)) {
                    childData.block?.let {
                        binding.txtValueThree.text = childData.block
                        binding.llThree.visibility = VISIBLE
                    }
                }
                if (!TextUtils.isEmpty(childData.street)) {
                    childData.street?.let {
                        binding.txtValueFour.text = childData.street
                        binding.llFour.visibility = VISIBLE
                    }
                }
                if (!TextUtils.isEmpty(childData.sector)) {
                    childData.sector?.let {
                        binding.txtValueFive.text = childData.sector
                        binding.llFive.visibility = VISIBLE
                    }
                }
                if (!TextUtils.isEmpty(childData.zone)) {
                    childData.zone?.let {
                        binding.txtValueSix.text = childData.zone
                        binding.llSix.visibility = VISIBLE
                    }
                }
                if (!TextUtils.isEmpty(childData.city)) {
                    childData.city?.let {
                        binding.txtValueSeven.text = childData.city
                        binding.llSeven.visibility = VISIBLE
                    }
                }
                if (!TextUtils.isEmpty(childData.state)) {
                    childData.state?.let {
                        binding.txtValueEight.text = childData.state
                        binding.llEight.visibility = VISIBLE
                    }
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