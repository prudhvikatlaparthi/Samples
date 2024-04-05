package com.pru.workdesigns

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pru.workdesigns.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            val placesView = PlacesView(this@MainActivity)
            withContext(Dispatchers.Main){
                binding.viewNearByPlaces.addView(placesView)
            }
            val placesThumbView = PlacesView(this@MainActivity, true)
            withContext(Dispatchers.Main){
                binding.viewThumbNailPlaces.addView(placesThumbView)
            }
        }
    }
}