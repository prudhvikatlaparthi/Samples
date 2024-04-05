package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemPenaltyWaiveoffChildBinding
import com.sgs.citytax.databinding.ItemPenaltyWaiveoffGroupBinding
import com.sgs.citytax.model.InvoicePenalties
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDateTimeInMillisecond
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision
import java.math.BigDecimal
import java.util.*


class PenaltyWaiveOffAdapter(private val listener: IClickListener) : BaseExpandableListAdapter() {

    private var groupList: ArrayList<Int> = arrayListOf()
    private var penaltyList: HashMap<Int, ArrayList<InvoicePenalties>> = hashMapOf()

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return penaltyList[groupList[groupPosition]]?.get(childPosition)!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return penaltyList[groupList[groupPosition]]?.size ?: -1
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): View {
        val binding: ItemPenaltyWaiveoffChildBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.item_penalty_waiveoff_child, parent, false)

        binding.llOne.visibility = GONE
        binding.llTwo.visibility = GONE
        binding.llThree.visibility = GONE
        binding.llFour.visibility = GONE

        when (getChild(groupPosition, childPosition)) {
            is InvoicePenalties -> {
                binding.txtKeyOne.text = parent?.context?.resources?.getString(R.string.penalty_id)
                binding.txtKeyTwo.text = parent?.context?.resources?.getString(R.string.penalty_date)
                binding.txtKeyThree.text = parent?.context?.resources?.getString(R.string.penalty_amount)
                binding.txtKeyFour.text = parent?.context?.resources?.getString(R.string.due_amount)
                val childData = getChild(groupPosition, childPosition) as InvoicePenalties
                binding.txtValueOne.text = childData.penaltyID.toString()
                binding.txtValueTwo.text = formatDisplayDateTimeInMillisecond(childData.penaltyDate)
                binding.txtValueThree.text = formatWithPrecision(childData.penaltyAmount)
                binding.txtValueFour.text = formatWithPrecision(childData.currentDue)

                binding.llOne.visibility = VISIBLE
                binding.llTwo.visibility = VISIBLE
                binding.llThree.visibility = VISIBLE
                binding.llFour.visibility = VISIBLE
                if (childData.currentDue?.compareTo(BigDecimal.ZERO)!! > 0)
                    binding.llBtn.visibility = VISIBLE

                binding.btnChildWaiveOff.setOnClickListener {
                    listener.onClick(it, childPosition, childData)
                }
            }
        }

        return binding.root
    }

    override fun getGroup(groupPosition: Int): Any {
        return groupList[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return groupList.size
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): View {
        val binding: ItemPenaltyWaiveoffGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.item_penalty_waiveoff_group, parent, false)

        binding.header.text = penaltyList[(getGroup(groupPosition))]?.get(0)?.noticeReferenceNo
                ?: ""
        var totalDue: BigDecimal = BigDecimal.ZERO
        for (item in penaltyList[groupList[groupPosition]]!!) {
            totalDue = totalDue.add(item.currentDue!!)
        }
        if (totalDue > BigDecimal.ZERO)
            binding.btnGroupWaiveOff.visibility = VISIBLE
        binding.btnGroupWaiveOff.setOnClickListener {
            listener.onClick(it, groupPosition, penaltyList[groupList[groupPosition]]?.get(0)!!)
        }

        return binding.root
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun set(_groupList: ArrayList<Int>, _penaltyList: HashMap<Int, ArrayList<InvoicePenalties>>) {
        groupList.clear()
        penaltyList.clear()
        groupList = _groupList
        penaltyList = _penaltyList
        notifyDataSetChanged()
    }
}