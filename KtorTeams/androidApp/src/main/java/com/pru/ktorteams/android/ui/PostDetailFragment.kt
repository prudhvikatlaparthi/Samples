package com.pru.navigationcomponentdemo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pru.ktorteams.android.R
import com.pru.ktorteams.android.databinding.FragmentPostDetailBinding
import com.pru.ktorteams.android.utils.AppUtils

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private lateinit var binding : FragmentPostDetailBinding
    private val args by navArgs<PostDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostDetailBinding.bind(view)

        binding.tvTitle.append(" ".plus(args.userID))

        binding.btnNav.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSettings.setOnClickListener {
            val action = PostDetailFragmentDirections.actionGlobalSettingsFragment(fromScreen = AppUtils.FromScreen.PostDetail)
            findNavController().navigate(action)
        }
    }
}