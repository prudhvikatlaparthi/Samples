package com.pru.languageapp

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.LocaleList
import androidx.appcompat.app.AppCompatActivity
import com.pru.languageapp.databinding.ActivityMainBinding
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.app_name)

        binding.tvLanguage.setOnClickListener {
            val lng = if (AppPreferences.language == "en") "te" else "en"
            AppPreferences.language = lng
            val context = setAppLocale(AppPreferences.language)
            title = context.getString(R.string.app_name)
            binding.tvLanguage.text = context.getString(R.string.test)
        }
        binding.next.setOnClickListener {
            startActivity(Intent(this,SecondActivity::class.java))
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ContextWrapper(newBase?.setAppLocale(AppPreferences.language)))
    }
}

fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = this.resources.configuration
    config.setLocales(LocaleList(locale))
    return this.createConfigurationContext(config)
}


object AppPreferences :
    SharedPreferences by appContext.getSharedPreferences("app-settings", Context.MODE_PRIVATE) {

    var language: String
        get() = this.getString("Language", "en") ?: "en"
        set(value) {
            this.edit().putString("Language", value).apply()
        }
}