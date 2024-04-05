package cloud.mariapps.chatapp.ui.joborder

import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import cloud.mariapps.chatapp.base.BaseAdapter
import cloud.mariapps.chatapp.databinding.JobOrderlistLayoutBinding
import cloud.mariapps.chatapp.model.internal.JobOrder
import cloud.mariapps.chatapp.utils.Constants

class JobOrderAdapter : BaseAdapter<JobOrder, JobOrderlistLayoutBinding>(
    bindingInflater = JobOrderlistLayoutBinding::inflate, diffCallback = jobOrderDiffCallback
) {
    companion object {
        private val jobOrderDiffCallback = object : DiffUtil.ItemCallback<JobOrder>() {
            override fun areItemsTheSame(oldItem: JobOrder, newItem: JobOrder): Boolean =
                oldItem.jobOrderId == newItem.jobOrderId

            override fun areContentsTheSame(
                oldItem: JobOrder, newItem: JobOrder
            ): Boolean = oldItem == newItem

        }
    }

    override fun bindData(binding: JobOrderlistLayoutBinding, item: JobOrder) {
        binding.tvJobOrderName.text = item.jobOrderName
    }

    override fun clickListener(binding: JobOrderlistLayoutBinding, holderCallback: () -> Int) {
        binding.tvJobOrderName.setOnClickListener {
            val action = JobOrderFragmentDirections.actionJobOrderFragmentToUsersFragment(fromScreen = Constants.FromScreen.CreateChat)
            binding.root.findNavController().navigate(action)
        }
    }
}