package com.pru.singleactivity.ui.loginauth

import androidx.fragment.app.viewModels
import com.pru.singleactivity.base.BaseFragment
import com.pru.singleactivity.databinding.FragmentLoginAuthBinding
import com.pru.singleactivity.ui.dashboard.DashBoardFragment
import com.pru.singleactivity.ui.login.LoginArgs
import com.pru.singleactivity.ui.login.LoginFragment
import com.pru.singleactivity.utils.CommonUtils.getArgs
import com.pru.singleactivity.utils.CommonUtils.navigate
import com.pru.singleactivity.utils.CommonUtils.navigatePop
import com.pru.singleactivity.utils.CommonUtils.tag

class LoginAuthFragment :
    BaseFragment<FragmentLoginAuthBinding>(FragmentLoginAuthBinding::inflate) {
    private val viewModel by viewModels<LoginAuthViewModel>()

    override fun setup() {
        setBack(true)
        binding.tvEmail.text = viewModel.loginArgs.email
    }

    override fun listeners() {
        binding.next.setOnClickListener {
            navigatePop(DashBoardFragment(),LoginFragment::class.tag,true)
        }
    }

    override fun observers() {

    }
}