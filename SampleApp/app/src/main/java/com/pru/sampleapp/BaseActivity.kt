package com.pru.sampleapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pru.sampleapp.databinding.ActivityBaseBinding

abstract class BaseActivity : AppCompatActivity() {
    lateinit var baseBinding: ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(baseBinding.root)
        setSupportActionBar(baseBinding.myToolBar)
        initializeView()
        setUpBackButton()
        setUpCloseButton()
    }

    fun addViewToBase(view: View) {
        baseBinding.flBaseContainer.addView(view)
    }

    private fun setUpBackButton() {
        if (this is MainActivity) {
            baseBinding.imgBtnBack.hide()
        } else {
            baseBinding.imgBtnBack.show()
        }
        baseBinding.imgBtnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpCloseButton() {
        if (this is MainActivity) {
            baseBinding.imgbtnClose.show()
        } else {
            baseBinding.imgbtnClose.hide()
        }
        baseBinding.imgbtnClose.setOnClickListener {
            onBackPressed()
        }
    }

    fun setToolbarTitle(title: String?) =
        (title ?: getString(R.string.app_name)).also { baseBinding.toolbarTitle.text = it }

    abstract fun initializeView()
}