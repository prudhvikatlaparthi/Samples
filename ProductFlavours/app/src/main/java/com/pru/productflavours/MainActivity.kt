package com.pru.productflavours

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pru.productflavours.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.tvMain.text = BuildConfig.API_BASE_URL

    }
}