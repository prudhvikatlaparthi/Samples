package com.pru.languageapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pru.languageapp.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.app_name)
        binding.btnTap.setOnClickListener {
            val lng = if (AppPreferences.language == "en") "te" else "en"
            AppPreferences.language = lng
            val context = setAppLocale(AppPreferences.language)
            title = context.getString(R.string.app_name)
            binding.btnTap.text = context.getString(R.string.tap)
        }
    }
}