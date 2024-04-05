package com.pru.dynamicviewfind

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pru.dynamicviewfind.DynamicViewFinding.hideControllers
import com.pru.dynamicviewfind.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private val binding : ActivitySecondBinding by lazy {
        ActivitySecondBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.hideControllers()
    }
}