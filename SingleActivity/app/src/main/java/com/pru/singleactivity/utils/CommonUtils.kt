package com.pru.singleactivity.utils

import android.content.Context
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pru.singleactivity.R
import com.pru.singleactivity.base.Args
import com.pru.singleactivity.base.MainActivity
import com.pru.singleactivity.base.Result
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import kotlin.reflect.KClass

object CommonUtils {

    fun Fragment.navigate(fragment: Fragment, args: Args? = null) {
        this.setHasOptionsMenu(false)
        (requireActivity() as MainActivity).navigate(fragment = fragment, args = args)
    }

    fun DialogFragment.navigate(fragment: Fragment, args: Args? = null) {
        this.dismiss()
        (requireActivity() as MainActivity).navigate(fragment = fragment, args = args)
    }

    fun DialogFragment.navigate(dialogFragment: DialogFragment, args: Args? = null) {
        this.dismiss()
        (requireActivity() as MainActivity).navigate(dialogFragment = dialogFragment, args = args)
    }

    fun DialogFragment.navigate(bottomSheetDialogFragment: BottomSheetDialogFragment, args: Args? = null) {
//        this.dismiss()
        (requireActivity() as MainActivity).navigate(bottomSheetDialogFragment = bottomSheetDialogFragment, args = args)
    }

    fun Fragment.navigate(dialogFragment: DialogFragment, args: Args? = null) {
        (requireActivity() as MainActivity).navigate(dialogFragment = dialogFragment, args = args)
    }

    fun Fragment.navigatePop(fragment: Fragment, args: Args? = null) {
        (requireActivity() as MainActivity).navigatePop(fragment = fragment, args = args)
    }

    fun Fragment.navigatePop(
        fragment: Fragment,
        name: String,
        inclusive: Boolean = false,
        args: Args? = null
    ) {
        (requireActivity() as MainActivity).navigatePop(fragment, name, inclusive, args = args)
    }

    fun Fragment.popBackStack() {
        (requireActivity() as MainActivity).popBackStack()
    }

    fun DialogFragment.popBackStack() {
        this.dismiss()
    }

    fun Fragment.popBackStack(name: String, inclusive: Boolean = false) {
        (requireActivity() as MainActivity).popBackStack(name, inclusive)
    }

    val <T : Fragment> KClass<T>.tag: String
        @JvmName("getTag")
        get() = ((this as ClassBasedDeclarationContainer).jClass).simpleName

    fun Fragment.prepareFragment() {
        this.view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        this.view?.isClickable = true
        this.view?.isFocusable = true
    }

    /**
     * width range 0f to 1f
     * height range 0f to 1f
    fun DialogFragment.prepareDialog(width: Float, height: Float) {
    requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    this.setLayout(width = width, height = height)
    }*/

    /**
     * width range 0f to 1f
     * height range 0f to 1f
     */
    fun DialogFragment.setLayout(width: Float, height: Float) {
        val actualWidth = resources.displayMetrics.widthPixels
        val actualHeight = resources.displayMetrics.heightPixels
        requireDialog().window?.setLayout(
            (actualWidth * width).toInt(),
            (actualHeight * height).toInt()
        )
    }

    fun BottomSheetDialogFragment.setLayout(width: Float, height: Float) {
        val actualWidth = resources.displayMetrics.widthPixels
        val actualHeight = resources.displayMetrics.heightPixels
        requireDialog().window?.setLayout(
            (actualWidth * width).toInt(),
            (actualHeight * height).toInt()
        )
    }

    fun Context.getHeight(): Int = resources.displayMetrics.heightPixels

    fun Context.getWidth(): Int = resources.displayMetrics.widthPixels

    inline fun <Args : Parcelable> Fragment.getArgs(crossinline listener: (Args) -> Unit) {
        arguments?.let { bundle ->
            if (bundle.containsKey("args")) {
                bundle.getParcelable<Args?>("args")?.let {
                    listener.invoke(it)
                } ?: throw ArgsNotFoundException("${this.tag} args not found")
            } else throw ArgsNotFoundException("${this.tag} args not found")
        } ?: throw ArgsNotFoundException("${this.tag} args not found")
    }

    fun <Args : Parcelable> ViewModel.getArgs(savedStateHandle: SavedStateHandle) : Args {
        if (savedStateHandle.contains("args")) {
            savedStateHandle.get<Args?>("args")?.let {
                return it
            } ?: throw ArgsNotFoundException("${this::class.java.simpleName} args not found")
        } else throw ArgsNotFoundException("${this::class.java.simpleName} args not found")
    }

    fun Fragment.setResult(result: Result) {
        setFragmentResult("result", bundleOf("result" to result))
    }

    fun Fragment.setResultListener(listener: (Result) -> Unit) {
        setFragmentResultListener("result") { _, bundle ->
            if (bundle.containsKey("result")) {
                bundle.getParcelable<Result?>("result")?.let {
                    listener.invoke(it)
                }
            }
        }
    }

    fun Fragment.requiredMainActivity() = requireActivity() as MainActivity

}