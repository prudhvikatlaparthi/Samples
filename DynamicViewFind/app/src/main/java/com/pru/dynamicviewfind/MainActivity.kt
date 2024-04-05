package com.pru.dynamicviewfind

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pru.dynamicviewfind.DynamicViewFinding.hideControllers
import com.pru.dynamicviewfind.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.hideControllers()
        binding.plus.setOnClickListener {
            Intent(this@MainActivity, SecondActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}