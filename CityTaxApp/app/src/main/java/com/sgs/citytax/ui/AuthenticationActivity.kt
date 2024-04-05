package com.sgs.citytax.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.ui.adapter.SectionsPagerAdapter
import com.sgs.citytax.databinding.ActivityAuthenticationBinding
import com.sgs.citytax.ui.fragments.OTPValidationFragment
import com.sgs.citytax.ui.fragments.TwoFactorAuthFragment
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.util.Constant

class AuthenticationActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private val titles:ArrayList<String> = arrayListOf()
    private val fragments: ArrayList<Fragment> = arrayListOf()
    var sectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title=resources.getString(R.string.authenticator_text)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        titles.add(getString(R.string.authenticator_text))
        titles.add(getString(R.string.otp_title_text))

        val userName=intent.extras?.getString(Constant.USERNAME)
        val bundle=Bundle()
        bundle.putString(Constant.USERNAME,userName)
        val twoFactorAuth= TwoFactorAuthFragment()
        twoFactorAuth.arguments=bundle

        fragments.add(twoFactorAuth)
        fragments.add(OTPValidationFragment())

        sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, titles, fragments)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        OTPValidationFragment().timer?.cancel()
    }
}