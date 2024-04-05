package com.pru.nfcapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pru.nfcapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCheckCard.setOnClickListener {
            startActivity(Intent(this, NFCActivity::class.java))
        }
        binding.btnMiFareCard.setOnClickListener {
            startActivity(Intent(this, MiFareCardActivity::class.java))
        }
    }
}