package com.pru.touchnote.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pru.farmersapp.listeners.ToggleListener
import com.pru.touchnote.R
import com.pru.touchnote.utils.Resource
import com.pru.touchnote.utils.showToast
import com.pru.touchnote.viewmodels.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_postuser.*
import kotlinx.android.synthetic.main.appbar_layout.*

@AndroidEntryPoint
class PostUserActivity : AppCompatActivity() {

    private val postViewModel by viewModels<PostViewModel>()
    private val TAG = PostUserActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postuser)
        toolbar_right.visibility = VISIBLE
        toolbar_left.visibility = VISIBLE
        toolbar_title.text = "Add User"
        toolbarListeners()

        status_toggle.setToggleNames("Active", "Inactive")
        status_toggle.setToggleType(postViewModel.getStatus())
        status_toggle.setToggleListener(object : ToggleListener {
            override fun toggleChanged(name: String?) {
                postViewModel.setStatus(name ?: "Active")
            }
        })

        gender_toggle.setToggleNames("Male", "Female")
        gender_toggle.setToggleType(postViewModel.getGender())
        gender_toggle.setToggleListener(object : ToggleListener {
            override fun toggleChanged(name: String?) {
                postViewModel.setGender(name ?: "Male")
            }

        })

        postViewModel.postResponse.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    postProgressBar.visibility = GONE
                    response.data?.let {
                        showToast("Success! user saved.")
                        onBackPressed()
                    }
                }
                is Resource.Error -> {
                    postProgressBar.visibility = GONE
                    response.message?.let { message ->
                        showToast(message)
                    }
                }
                is Resource.Loading -> {
                    postProgressBar.visibility = VISIBLE
                }
            }
        })
    }

    private fun toolbarListeners() {
        toolbar_left.setOnClickListener {
            onBackPressed()
        }
        toolbar_right.setOnClickListener {
            if (!isDataValidate()) {
                return@setOnClickListener
            }
//            postViewModel.postUser(et_name.text.toString(), et_email.text.toString())
            postViewModel.postuserRXJ(et_name.text.toString(), et_email.text.toString())
        }
    }

    private fun isDataValidate(): Boolean {
        var isValid = true
        if (et_name.text.isNullOrEmpty()) {
            isValid = false
            showToast("Enter name")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.text).matches()) {
            isValid = false
            showToast("Enter valid email")
        }
        return isValid
    }
}