package com.pru.shopping.androidApp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.pru.shopping.androidApp.PaginationScrollListener
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.adapters.ImagePagerAdapter
import com.pru.shopping.androidApp.adapters.ShopByCategoryAdapter
import com.pru.shopping.androidApp.databinding.DummyBinding
import com.pru.shopping.androidApp.ui.BaseFragment
import com.pru.shopping.androidApp.utils.hide
import kotlinx.coroutines.delay

class ImageViewPagerFragment : BaseFragment(R.layout.dummy) {
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private lateinit var fragmentImageViewPagerBinding: DummyBinding
    private val imagePagerAdapter: ImagePagerAdapter by lazy {
        ImagePagerAdapter(
            listOf(
                "",
                "",
                ""
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentImageViewPagerBinding = DummyBinding.bind(view)
        fragmentImageViewPagerBinding.viewPager2.adapter = imagePagerAdapter
        TabLayoutMediator(
            fragmentImageViewPagerBinding.tabLayout,
            fragmentImageViewPagerBinding.viewPager2
        ) { tab, position ->

        }.attach()

        lifecycleScope.launchWhenStarted {
            delay(2000)
            imagePagerAdapter.updateData(
                listOf(
                    "https://image.shutterstock.com/image-photo/bright-spring-view-cameo-island-260nw-1048185397.jpg",
                    "https://images.unsplash.com/photo-1494548162494-384bba4ab999?ixid=MXwxMjA3fDB8MHxzZWFyY2h8MXx8ZGF3bnxlbnwwfHwwfA%3D%3D&ixlib=rb-1.2.1&w=1000&q=80",
                    "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg"
                )
            )
            fragmentImageViewPagerBinding.imageProgress.hide()
            delay(2000)
            val items = (0..50).map { "Title $it" }
            val spadapter = ShopByCategoryAdapter(listOf())
            fragmentImageViewPagerBinding.list.apply {
                layoutManager = LinearLayoutManager(requireContext()).also {
                    it.orientation = LinearLayoutManager.VERTICAL
                }
                adapter = spadapter
                addOnScrollListener(object :
                    PaginationScrollListener() {
                    override fun isLastPage(): Boolean {
                        return isLastPage
                    }

                    override fun isLoading(): Boolean {
                        return isLoading
                    }

                    override fun loadMoreItems() {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            spadapter.setItemClickListener {
                findNavController().navigate(R.id.action_global_sampleBottomSheet)
            }
            spadapter.updateData(items)
            fragmentImageViewPagerBinding.pbView.hide()

        }
    }

}