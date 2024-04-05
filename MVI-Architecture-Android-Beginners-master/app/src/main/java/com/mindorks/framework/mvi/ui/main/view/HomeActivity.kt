package com.mindorks.framework.mvi.ui.main.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.mindorks.framework.mvi.data.model.User
import com.mindorks.framework.mvi.databinding.ActivityHomeBinding
import com.mindorks.framework.mvi.ui.main.intent.HomeEventIntent
import com.mindorks.framework.mvi.ui.main.viewmodel.HomeViewModel
import com.mindorks.framework.mvi.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var activityHomeBinding: ActivityHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(activityHomeBinding.root)

        activityHomeBinding.btnGet.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.homeEventIntent.send(HomeEventIntent.FetchInfo)
            }
        }

        homeViewModel.myLoad.observe(this) { res ->
            when (res) {
                is Resource.Loading -> {
                    activityHomeBinding.tvHome.text = "Loading"
                }
                is Resource.Success -> {
                    (res as List<*>).let {
                        activityHomeBinding.tvHome.text = it.toString()
                    }
                }
                is Resource.Error -> {
                    activityHomeBinding.tvHome.text = res.e.localizedMessage
                }
            }
        }
    }
}