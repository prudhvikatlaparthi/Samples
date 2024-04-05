package com.pru.mvvm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pru.mvvm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()
    private var message : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnClick.setOnClickListener {
            viewModel.fetchUsers()
        }
        viewModel.userData.observe(this) {
            binding.tvView.text = it.toString()


        }
        someThing {
            display(it)
        }
        message?.let {
            display(it)
            it.length
            it.lowercase()
        }
    }

    fun someThing(function: (String) -> Unit) {
        if (true){
            function.invoke("Hello")
        }
    }

    fun display(st : String){
        Toast.makeText(this, st, Toast.LENGTH_SHORT).show()
    }
}