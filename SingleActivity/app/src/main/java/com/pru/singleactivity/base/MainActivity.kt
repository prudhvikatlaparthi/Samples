package com.pru.singleactivity.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pru.singleactivity.databinding.ActivityMainBinding
import com.pru.singleactivity.listeners.OnResumeListener
import com.pru.singleactivity.ui.dashboard.DashBoardFragment
import com.pru.singleactivity.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navigate(LoginFragment())
    }

    fun navigate(fragment: Fragment, args: Args? = null) {
        val tag = fragment::class.java.simpleName
        args?.let {
            fragment.arguments = bundleOf("args" to it)
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(binding.container.id, fragment, tag).addToBackStack(tag).commit()
    }

    fun navigate(
        dialogFragment: DialogFragment,
        args: Args? = null
    ) {
        val tag = dialogFragment::class.java.simpleName
        args?.let {
            dialogFragment.arguments = bundleOf("args" to it)
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        dialogFragment.show(fragmentTransaction, tag)
    }

    fun navigate(
        bottomSheetDialogFragment: BottomSheetDialogFragment,
        args: Args? = null
    ) {
        val tag = bottomSheetDialogFragment::class.java.simpleName
        args?.let {
            bottomSheetDialogFragment.arguments = bundleOf("args" to it)
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        bottomSheetDialogFragment.show(fragmentTransaction, tag)
    }

    fun navigatePop(fragment: Fragment, args: Args?) {
        popBackStack()
        navigate(fragment, args = args)
    }

    fun navigatePop(fragment: Fragment, name: String, inclusive: Boolean = false, args: Args?) {
        popBackStack(name, inclusive)
        navigate(fragment, args = args)
    }

    fun popBackStack() {
        val fragment = supportFragmentManager
        resume()
        fragment.popBackStackImmediate()
    }

    fun popBackStack(name: String, inclusive: Boolean = false) {
        val fragment = supportFragmentManager
        resume()
        val result =
            fragment.popBackStackImmediate(name, if (inclusive) POP_BACK_STACK_INCLUSIVE else 0)
        if (!result) {
            popBackStack()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            resume()
            super.onBackPressed()
        }
    }

    val currentFragment : Fragment?
    get() = supportFragmentManager.fragments.getOrNull(0)

    private fun resume() {
        (supportFragmentManager.fragments.getOrNull(supportFragmentManager.backStackEntryCount - 2) as OnResumeListener?)?.resume()
    }

}