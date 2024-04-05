package com.pru.sampleapp

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.pru.sampleapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun initializeView() {
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        addViewToBase(activityMainBinding.root)
        setToolbarTitle("MainActivity")

        lifecycleScope.launchWhenStarted {
            launch(Dispatchers.IO) {
                for (i in 1..20) {
                    val btn =
                        com.google.android.material.button.MaterialButton(this@MainActivity)
                    btn.text = "$i Button"
                    btn.setOnClickListener {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                    launch(Dispatchers.Main) {
                        activityMainBinding.containerBtns.addView(btn)
                    }
                }
            }
        }
    }

    fun doMath() {

    }

}