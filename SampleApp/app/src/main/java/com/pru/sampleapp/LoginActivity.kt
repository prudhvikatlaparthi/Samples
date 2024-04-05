package com.pru.sampleapp

import android.content.Intent
import com.pru.sampleapp.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginBinding: ActivityLoginBinding
    override fun initializeView() {
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        addViewToBase(activityLoginBinding.root)
        setToolbarTitle("Login Page")

        activityLoginBinding.btnSecond.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }

}