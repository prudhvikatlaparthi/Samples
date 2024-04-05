package com.pru.geo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnGenerate).setOnClickListener {
            Main.prepareShapeFile()
        }

        findViewById<Button>(R.id.btnGss).setOnClickListener {
            startActivity(Intent(this, GNSSActivity::class.java))
        }
    }
}