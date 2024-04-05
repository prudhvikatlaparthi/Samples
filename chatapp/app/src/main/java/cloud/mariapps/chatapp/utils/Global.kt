package cloud.mariapps.chatapp.utils

import android.os.Build
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import cloud.mariapps.chatapp.MainActivity
import cloud.mariapps.chatapp.appContext
import cloud.mariapps.chatapp.base.ResultBack

object Global {

    val Fragment.mainActivity: MainActivity
        get() = this.requireActivity() as MainActivity

    fun showToast(message: String?) {
        message?.let {
            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any? = emptyArray()): String {
        return appContext.resources.getString(resId, *formatArgs)
    }

    inline fun Fragment.createOptionsMenu(
        @MenuRes menuRes: Int,
        crossinline onMenuItemSelected: (menuItem: MenuItem) -> Boolean
    ) {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
                    menuInflater.inflate(menuRes, menu)

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    onMenuItemSelected(menuItem)
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    fun DialogFragment.setLayout(width: Float, height: Float) {
        val actualWidth = resources.displayMetrics.widthPixels
        val actualHeight = resources.displayMetrics.heightPixels
        requireDialog().window?.setLayout(
            (actualWidth * width).toInt(),
            (actualHeight * height).toInt()
        )
    }

    fun Fragment.setResult(result: ResultBack) {
        setFragmentResult("result", bundleOf("result" to result))
    }

    @Suppress("DEPRECATION")
    fun Fragment.setResultListener(listener: (ResultBack) -> Unit) {
        setFragmentResultListener("result") { _, bundle ->
            if (bundle.containsKey("result")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable("result", ResultBack::class.java)?.let {
                        listener.invoke(it)
                    }
                } else {
                    bundle.getParcelable<ResultBack?>("result")?.let {
                        listener.invoke(it)
                    }
                }
            }
        }
    }

    fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(length) { charset.random() }
            .joinToString("")
    }
}