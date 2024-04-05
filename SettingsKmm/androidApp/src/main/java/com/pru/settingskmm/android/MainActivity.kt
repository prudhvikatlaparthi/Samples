package com.pru.settingskmm.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pru.settingskmm.MyPreferences
import com.pru.settingskmm.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.btnSave.setOnClickListener {
            MyPreferences.saveName(activityMainBinding.edtName.text.toString())
        }

        activityMainBinding.btnRetrieve.setOnClickListener {
            activityMainBinding.tvName.text = MyPreferences.getName()
        }
    }
}
