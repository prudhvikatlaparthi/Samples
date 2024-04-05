package com.pru.backgroundservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pru.backgroundservice.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.pru.backgroundservice.utils.Constants.ACTION_STOP_SERVICE
import com.pru.backgroundservice.utils.ServiceUtils.sendCommandToService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    override fun onDestroy() {
        super.onDestroy()
        sendCommandToService(ACTION_STOP_SERVICE)
    }
}