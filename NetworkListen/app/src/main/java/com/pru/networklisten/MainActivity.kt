package com.pru.networklisten

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pru.networklisten.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val checkConnection by lazy {
        CheckConnection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            checkConnection.observe(this@MainActivity) {
                if (it) {
                    imgStatus.setImageResource(R.drawable.ic_wifi)
                    tvStatus.text = "Connected ! :)"

                } else {
                    imgStatus.setImageResource(R.drawable.ic_wifi_off)
                    tvStatus.text = "DisConnected ! :("
                }
            }
        }
    }
}