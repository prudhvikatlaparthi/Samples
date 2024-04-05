package cloud.mariapps.chatapp.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding>(private val bindingInflater: (inflater: LayoutInflater) -> VB) :
    BottomSheetDialogFragment() {

    private var _binding: VB? = null

    protected val binding: VB
        get() = _binding as VB


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requireDialog().window!!.setGravity(Gravity.BOTTOM)
        view.post {
            val parent = view.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<View?>?
            if (bottomSheetBehavior != null) {
                bottomSheetBehavior.peekHeight = view.measuredHeight
            }
        }
        setup()
        viewLifecycleOwner.lifecycleScope.launch {
            observers()
        }
        listeners()
    }

    abstract fun setup()

    abstract suspend fun observers()

    abstract fun listeners()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}