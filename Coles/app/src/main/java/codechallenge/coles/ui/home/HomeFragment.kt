package codechallenge.coles.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import codechallenge.coles.R
import codechallenge.coles.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        initComponents()
        initObservers()
        initListeners()
    }

    private fun initObservers() {

    }

    private fun initListeners() {

    }

    private fun initComponents() {

    }
}
