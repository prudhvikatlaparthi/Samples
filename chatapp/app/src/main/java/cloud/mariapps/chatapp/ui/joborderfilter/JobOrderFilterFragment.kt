package cloud.mariapps.chatapp.ui.joborderfilter

import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentJobOrderFilterBinding
import cloud.mariapps.chatapp.model.internal.ToolbarItem


class JobOrderFilterFragment :
    BaseFragment<FragmentJobOrderFilterBinding>(FragmentJobOrderFilterBinding::inflate) {
    override fun setup() {
        setupToolBar(toolbarItem = ToolbarItem(title = getString(R.string.job_order_filter)))

        //Job Category
        val jobCategoryList = listOf("Planned", "Un Planned")
        val jobCategoryAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobCategoryList)
        jobCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spnJobCategory.adapter = jobCategoryAdapter

        //Job Type
        val jobTypeList = listOf("Lubricants", "Normal")
        val jobTypeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobTypeList)
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spnJobType.adapter = jobTypeAdapter
    }

    override suspend fun observers() {
    }

    override fun listeners() {
        binding.btnFilter.setOnClickListener {
            val action = JobOrderFilterFragmentDirections.actionJobOrderFilterFragmentToJobOrderFragment()
            findNavController().navigate(action)
        }
    }

}