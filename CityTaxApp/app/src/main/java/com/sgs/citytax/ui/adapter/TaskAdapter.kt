package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.VUCRMServiceRequest
import com.sgs.citytax.databinding.ItemTaskBinding
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.getString
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapter(iClickListener: IClickListener) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private var tasks: ArrayList<VUCRMServiceRequest> = arrayListOf()
    private var mIClickListener: IClickListener? = iClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_task,
                parent, false))
    }

    class ViewHolder(var binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: VUCRMServiceRequest, iClickListener: IClickListener?) {
            binding.tvDate.text = task.complaintDate?.let { formatDisplayDateTimeInMillisecond(it) }
            binding.tvStatus.text = task.status
            binding.tvCreatedBy.text = task.createdBy.toString()
            binding.tvComplaint.text = task.taskType
            task.priority?.let {
                binding.tvPriority.text = it
            }
            binding.tvTaskNo.text = "${task.serviceRequestNo}"

            task.taskType?.let { it
                if (it.toLowerCase(Locale.getDefault()) == "incident") {
                    binding.tvTaskParentNo.text = task.parentServiceRequestNo
                    binding.tvTaskParent.text = getString(R.string.label_incident_no)
                } else if (it.toLowerCase(Locale.getDefault()) == "complaint") {
                    binding.tvTaskParentNo.text = task.parentServiceRequestNo
                    binding.tvTaskParent.text = getString(R.string.label_complaint_no)
                } else if (task.taskType.toLowerCase(Locale.getDefault()) == "service") {
                    binding.tvTaskParentNo.text = task.parentServiceRequestNo
                    binding.tvTaskParent.text = getString(R.string.label_service_no)
                }
            }

            task.taskSubCategory?.let {
                binding.llTaskSubType.visibility = View.VISIBLE
                binding.tvTaskSubType.text = it
            }

            binding.root.setOnClickListener { iClickListener!!.onClick(it, adapterPosition, task) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasks[position], mIClickListener)

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun update(list: List<VUCRMServiceRequest>) {
        for (item: VUCRMServiceRequest in list)
            tasks.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        tasks = arrayListOf()
        notifyDataSetChanged()
    }

}
