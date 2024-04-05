package com.pru.arcmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.pru.arcmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val apiKey = "AAPK2a27bfb8b77d45cd956f64ab40c19fa3pAhXc_PN22KaKFKVz5MX7EKgqDc7gXdwg5Rnqh8etNLMso502ScgriGQYIeD7Yp2"
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(binding.mapView)
        ArcGISEnvironment.apiKey = ApiKey.create(apiKey)
        val map = ArcGISMap(BasemapStyle.ArcGISTopographic)
        binding.mapView.map = map
        binding.mapView.setViewpoint(Viewpoint(latitude = 16.7565064, longitude = 81.6766119, scale = 100.0))
    }
}