package com.pru.ktorteams.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pru.ktorteams.MyPreferences
import com.pru.ktorteams.android.databinding.ActivityPreferencesBinding

class PreferencesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreferencesBinding
    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        counter = MyPreferences.getCounterKey()
        binding.tvCounter.text = counter.toString()
        binding.btnAdd.setOnClickListener {
            counter += 1
            binding.tvCounter.text = counter.toString()
            MyPreferences.setCounterKey(counter)
        }
        binding.btnSub.setOnClickListener {
            counter -= 1
            binding.tvCounter.text = counter.toString()
            MyPreferences.setCounterKey(counter)
        }
    }
}