package com.pru.singleactivity.ui.settings

import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.lifecycleScope
import com.pru.singleactivity.base.BaseFragment
import com.pru.singleactivity.databinding.FragmentSettingsBinding
import com.pru.singleactivity.utils.CommonUtils.getArgs
import com.pru.singleactivity.utils.CommonUtils.popBackStack
import com.pru.singleactivity.utils.CommonUtils.requiredMainActivity
import com.pru.singleactivity.utils.CommonUtils.setResult
import kotlinx.coroutines.delay


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    private lateinit var args: SettingsArgs

    override fun setup() {
        setBack(true)
        getArgs<SettingsArgs> {
            args = it
        }

        binding.tvUser.text = buildString {
            append("User ${args.userId}")
        }

        lifecycleScope.launchWhenStarted {
            delay(5000)
            setResult(
                SettingsResult(
                    settingId = (5..20).random(),
                    isFromAddressDialog = args.isFromAddressDialog
                )
            )
            popBackStack()
        }
    }

    override fun observers() {

    }

    override fun listeners() {

    }
}