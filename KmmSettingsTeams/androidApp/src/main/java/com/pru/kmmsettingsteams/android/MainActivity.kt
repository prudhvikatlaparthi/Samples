package com.pru.kmmsettingsteams.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pru.kmmsettingsteams.Greeting
import android.widget.TextView
import com.pru.kmmsettingsteams.MyPreferences
import com.pru.kmmsettingsteams.android.databinding.ActivityMainBinding

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        counter = MyPreferences.getCounterValue()
        binding.tvCounter.text = counter.toString()
        binding.btnAdd.setOnClickListener {
            counter = counter + 1
            MyPreferences.setCounterValue(counter)
            binding.tvCounter.text = counter.toString()
        }

        binding.btnSub.setOnClickListener {
            counter = counter - 1
            MyPreferences.setCounterValue(counter)
            binding.tvCounter.text = counter.toString()
        }
    }
}
