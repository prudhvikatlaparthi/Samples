package com.pru.singleactivity.ui.address

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.pru.singleactivity.base.BaseDialogFragment
import com.pru.singleactivity.databinding.FragmentAddressDialogBinding
import com.pru.singleactivity.ui.locate.LocateBottomSheet
import com.pru.singleactivity.utils.CommonUtils.getArgs
import com.pru.singleactivity.utils.CommonUtils.navigate
import com.pru.singleactivity.utils.CommonUtils.popBackStack
import com.pru.singleactivity.utils.CommonUtils.setLayout
import com.pru.singleactivity.utils.CommonUtils.setResult
import kotlinx.coroutines.delay

class AddressDialogFragment :
    BaseDialogFragment<FragmentAddressDialogBinding>(FragmentAddressDialogBinding::inflate) {
    private lateinit var args: AddressArgs

    override fun setup() {
        setLayout(
            width = 0.96f,
            height = 0.5f
        )
        getArgs<AddressArgs> {
            args = it
        }
        Toast.makeText(requireContext(), "User ${args.userId}", Toast.LENGTH_SHORT).show()

        /*lifecycleScope.launchWhenStarted {
            delay(5000)
            setResult(AddressResult(isRefresh = true))
            popBackStack()
        }*/
    }

    override fun observers() {

    }

    override fun listeners() {
        binding.next.setOnClickListener {
            navigate(LocateBottomSheet())
        }
    }

    override fun onBackPress() {
        popBackStack()
    }
}