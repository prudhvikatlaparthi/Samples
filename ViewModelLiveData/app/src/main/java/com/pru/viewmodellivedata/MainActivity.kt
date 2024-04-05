package com.pru.viewmodellivedata

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pru.viewmodellivedata.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Prudhvi Log", "onCreate: ")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvCounter.text = mainViewModel.counter.toString()
        binding.btnTap.setOnClickListener {
//            mainViewModel.counter++
//            binding.tvCounter.text = mainViewModel.counter.toString()
            mainViewModel.callAPI()
        }
        mainViewModel.commonSource.observe(this){
            binding.tvCounter.text = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Prudhvi Log", "onDestroy: ")
    }
}