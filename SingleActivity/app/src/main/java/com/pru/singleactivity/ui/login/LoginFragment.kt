package com.pru.singleactivity.ui.login

import android.content.Intent
import com.pru.singleactivity.base.BaseFragment
import com.pru.singleactivity.databinding.FragmentLoginBinding


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override fun setup() {
        setBack(false)
    }

    override fun listeners() {
        binding.loginBtn.setOnClickListener {
            /*navigate(
                fragment = LoginAuthFragment(),
                args = LoginArgs(email = binding.etEmail.text.toString())
            )*/
            /*val captureVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5)
            captureVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
            startActivityForResult(captureVideoIntent, 10)*/
            Intent(requireContext(), AudioRecordTest::class.java).apply {
                startActivity(this)
            }
        }
    }

    override fun observers() {

    }


}