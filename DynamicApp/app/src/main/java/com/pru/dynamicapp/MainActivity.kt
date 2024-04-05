package com.pru.dynamicapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.pru.dynamicapp.databinding.ActivityMainBinding
import com.pru.dynamicapp.models.ViewItem

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data = Gson().fromJson(FakeRepository.json, Array<ViewItem>::class.java).toList()
        Log.i("Prudhvi Log", "onCreate: $data")
        for (viewItem in data) {
            val view = when (viewItem.type) {
                "edit_text" -> {
                    CEditText(this, viewItem, binding.llParent)
                }
                else -> {
                    CTextView(this, viewItem, binding.llParent)
                }
            }
            binding.llParent.addView(view)
        }
        binding.btnSave.setOnClickListener {
            for (i in 0 until binding.llParent.childCount) {
                val child: CEditText = binding.llParent.getChildAt(i) as CEditText
                if (child.isMandatory()) {

                }
            }
        }
    }
}