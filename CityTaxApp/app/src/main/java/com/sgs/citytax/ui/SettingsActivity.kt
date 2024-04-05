package com.sgs.citytax.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.preference.*
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.BuildConfig
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.util.LogHelper
import com.sgs.citytax.util.PrefHelper
import com.sgs.citytax.util.formatDateTimeInMillisecond
import okio.ByteString.Companion.toByteString
import java.lang.Exception
import java.util.*


class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()

        showToolbarBackButton(R.string.title_settings)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardActivity::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat()
            , Preference.OnPreferenceClickListener {
        private lateinit var prefHelper: PrefHelper

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            prefHelper = (activity as SettingsActivity).prefHelper

            //get Shared preferences from screen
            val preferences = preferenceManager.sharedPreferences

            //region Set preference values
            val prefLanguage = findPreference<ListPreference>(getString(R.string.pref_language))
            prefLanguage?.setDefaultValue(prefHelper.language)
            if (prefHelper.language == "EN")
                prefLanguage?.setValueIndex(0)
            else
                prefLanguage?.setValueIndex(1)

            val prefVersionName = findPreference<Preference>(getString(R.string.pref_version_name))
            prefVersionName?.summary = BuildConfig.VERSION_NAME

            val prefVersionCode = findPreference<Preference>(getString(R.string.pref_version_code))
            prefVersionCode?.summary = BuildConfig.VERSION_CODE.toString()

            val prefModel = findPreference<Preference>(getString(R.string.pref_model))
            prefModel?.summary = prefHelper.getDeviceName()

            val prefOSVersion = findPreference<Preference>(getString(R.string.pref_os_Version))
            prefOSVersion?.summary = Build.VERSION.RELEASE.toString()

            val prefCurrency = findPreference<Preference>(getString(R.string.pref_currency))
            prefCurrency?.summary = prefHelper.currencyCode
            //endregion
            val prefShareLogs = findPreference<Preference>(getString(R.string.share_logs))
            prefShareLogs?.title = getString(R.string.share_logs)
            prefShareLogs?.setOnPreferenceClickListener {
                if (LogHelper.isLogFileExist()) {
                    LogHelper.sendLogFiles { showLoader ->
                        if (showLoader) {
                            (activity as SettingsActivity).showProgressDialog()
                        } else {
                            (activity as SettingsActivity).dismissDialog()
                            (activity as SettingsActivity).showAlertDialog(getString(R.string.log_sent_successfully))
                        }
                    }
                } else {
                    (activity as SettingsActivity).showAlertDialog(getString(R.string.no_log_found))
                }
                true
            }


            prefLanguage?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                prefHelper.language = newValue.toString()
                prefLanguage?.setDefaultValue(prefHelper.language)
                MyApplication.updateLanguages(activity, newValue.toString())

                (activity as SettingsActivity).showToolbarBackButton(R.string.title_settings)

                prefLanguage?.title = getString(R.string.language_title)
                prefLanguage?.dialogTitle = getString(R.string.language_title)
                prefLanguage?.negativeButtonText = getString(R.string.cancel)
                prefVersionName?.title = getString(R.string.header_version_name)
                prefVersionCode?.title = getString(R.string.header_version_code)
                prefModel?.title = getString(R.string.header_model)
                prefOSVersion?.title = getString(R.string.header_os_Version)
                prefCurrency?.title = getString(R.string.currency)
                updateValues()
                true
            }
        }

        override fun onPreferenceClick(preference: Preference?): Boolean {
            return true
        }

        private fun updateValues() {

            val prefPreference = findPreference<Preference>(getString(R.string.preferences))
            prefPreference?.title = getString(R.string.preferences)
            val prefLogs = findPreference<Preference>(getString(R.string.share_logs))
            prefLogs?.title = getString(R.string.share_logs)
            val prefDeviceInfo = findPreference<Preference>(getString(R.string.device_info))
            prefDeviceInfo?.title = getString(R.string.device_info)

            val prefGeneral = findPreference<PreferenceCategory>(getString(R.string.pref_general))
            prefGeneral?.title = getString(R.string.general_header)
            val prefPayment = findPreference<PreferenceCategory>(getString(R.string.pref_payment))
            prefPayment?.title = getString(R.string.payment_header)
            val prefTaxNotice = findPreference<PreferenceCategory>(getString(R.string.pref_tax_notice))
            prefTaxNotice?.title = getString(R.string.tax_notice_header)
            val prefApplication = findPreference<PreferenceCategory>(getString(R.string.pref_application))
            prefApplication?.title = getString(R.string.application)
            val prefSystem = findPreference<PreferenceCategory>(getString(R.string.pref_system))
            prefSystem?.title = getString(R.string.system)

            val prefSearch = findPreference<SwitchPreference>(getString(R.string.pref_search_mode))
            prefSearch?.title = getString(R.string.header_search)
            prefSearch?.summaryOff = getString(R.string.search_not_allowed)
            prefSearch?.summaryOn = getString(R.string.search_allowed)
            val prefScan = findPreference<SwitchPreference>(getString(R.string.pref_scan_mode))
            prefScan?.title = getString(R.string.scan)
            prefScan?.summaryOff = getString(R.string.barcode_or_qr_code_scan_not_allowed)
            prefScan?.summaryOn = getString(R.string.barcode_qr_code_scan_allowed)
            val prefShowTaxNotice = findPreference<SwitchPreference>(getString(R.string.pref_show_tax_notice))
            prefShowTaxNotice?.title = getString(R.string.show_tax_notice_on_screen)
            prefShowTaxNotice?.summaryOff = getString(R.string.qr_code_will_not_be_printed_in_tax_notice)
            prefShowTaxNotice?.summaryOn = getString(R.string.qr_code_will_be_printed_in_tax_notice)


            val prefCash = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_payment_cash))
            prefCash?.title = getString(R.string.cash)
            val prefOrangeWallet = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_payment_orange_wallet))
            prefOrangeWallet?.title = getString(R.string.orange_wallet)
            val prefAllowPrint = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_allow_print))
            prefAllowPrint?.title = getString(R.string.header_allow_print)
            prefAllowPrint?.summaryOff = getString(R.string.print_will_not_come_after_payment)
            prefAllowPrint?.summaryOn = getString(R.string.print_after_every_successful_payment)
            val prefQRAllowPrint = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_allow_qr_code))
            prefQRAllowPrint?.title = getString(R.string.print_qr_code)
            prefQRAllowPrint?.summaryOff = getString(R.string.print_will_not_come_after_payment)
            prefQRAllowPrint?.summaryOn = getString(R.string.print_after_every_successful_payment)
        }
    }


}