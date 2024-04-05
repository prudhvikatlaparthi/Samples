package com.pru.navigationcomponentdemo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pru.ktorteams.android.R
import com.pru.ktorteams.android.databinding.FragmentPostListBinding
import com.pru.ktorteams.android.utils.AppUtils

class PostListFragment : Fragment(R.layout.fragment_post_list) {
    private lateinit var binding : FragmentPostListBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostListBinding.bind(view)
        binding.btnNav.setOnClickListener {
            val action = PostListFragmentDirections.actionPostListFragmentToPostDetailFragment(userID = (0 until 10).random())
            findNavController().navigate(action)
        }
        binding.btnSettings.setOnClickListener {
//            val action = PostListFragmentDirections.actionGlobalSettingsFragment(fromScreen = AppUtils.FromScreen.PostList)
            val action = PostListFragmentDirections.actionGlobalMyDialogFragment()
            findNavController().navigate(action)
        }
    }
}