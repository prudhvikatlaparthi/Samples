package com.pru.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        setTitle("Second Activity")
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}