package com.pru.responsiveapp.ui.alpha

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.pru.responsiveapp.R
import com.pru.responsiveapp.databinding.FragmentListBinding
import com.pru.responsiveapp.popFragment
import com.pru.responsiveapp.replaceFragment
import com.pru.responsiveapp.standardMenuList
import com.pru.responsiveapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ListFragment : BaseFragment(R.layout.fragment_list) {
    private lateinit var fragmentListBinding: FragmentListBinding
    private val dashBoardListAdapter by lazy { DashBoardListAdapter(::listItemClickListener) }
    private val ldViewModel: LDViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val smoothScroller: RecyclerView.SmoothScroller by lazy {
        object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentListBinding = FragmentListBinding.bind(view)
        prepareToolbar()
        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        ldViewModel.obxData.observe(viewLifecycleOwner, {
            dashBoardListAdapter.submitList(it)
            ldViewModel.number.value = ldViewModel.number.value
            fragmentListBinding.pbView.isVisible = false
            if (ldViewModel.isNewDataAdded) {
                ldViewModel.isNewDataAdded = false
                Handler(Looper.getMainLooper()).postDelayed({
                    smoothScroller.targetPosition = 0
                    fragmentListBinding.rcView.layoutManager?.startSmoothScroll(
                        smoothScroller
                    )
                }, 500)
            }
        })
    }

    private fun setupViews() {
        fragmentListBinding.rcView.adapter = dashBoardListAdapter
    }

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(!isTablet()) {
            override fun handleOnBackPressed() {
                if (fragmentListBinding.bottomView.progress > 0) {
                    fragmentListBinding.bottomView.progress = 0.0f
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    fun prepareToolbar() {
        setupToolbar(
            title = "Fruits",
            showBackButton = true,
            isRequiredOptionMenu = true,
            menuItemClickCallBack = ::menuOptionsCallback,
            menuList = standardMenuList
        )
    }


    private fun menuOptionsCallback(itemName: String) {
        Toast.makeText(requireContext(), itemName, Toast.LENGTH_SHORT).show()
        when (itemName.toLowerCase(Locale.getDefault())) {
            "search" -> {
                findNavController().navigate(R.id.action_global_searchFragment)
            }
            "add" -> {
                addTap()
            }
            else -> Unit
        }
    }

    private fun addTap() {
        if (isTablet()) {
            parentFragmentManager.replaceFragment(
                isAnimationRequired = true, fragment = CreateFragment(),
                containerID = R.id.detailContainer
            )
        } else {
            parentFragmentManager.replaceFragment(
                isAnimationRequired = true, fragment = CreateFragment(),
                containerID = R.id.listContainer
            )
        }
    }

    private fun listItemClickListener(position: Int) {
        ldViewModel.number.value = position
        if (!isTablet()) {
            parentFragmentManager.replaceFragment(
                isAnimationRequired = true, fragment = DetailFragment(),
                containerID = R.id.listContainer
            )
        } else {
            parentFragmentManager.popFragment()
        }
    }
}