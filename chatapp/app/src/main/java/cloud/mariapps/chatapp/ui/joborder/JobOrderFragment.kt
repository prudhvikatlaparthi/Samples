package cloud.mariapps.chatapp.ui.joborder

import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentJobOrderBinding
import cloud.mariapps.chatapp.model.internal.JobOrder
import cloud.mariapps.chatapp.model.internal.ToolbarItem


class JobOrderFragment : BaseFragment<FragmentJobOrderBinding>(FragmentJobOrderBinding::inflate) {

    private val adapter by lazy {
        JobOrderAdapter()
    }
    override fun setup() {
        setupToolBar(toolbarItem = ToolbarItem(title = getString(R.string.select_job_order)))
        adapter.submitList(List(10){
            JobOrder(jobOrderId = it, jobOrderName = "Job Order $it")
        })
        binding.rcJobOrders.adapter = adapter
    }

    override suspend fun observers() {

    }

    override fun listeners() {

    }

}