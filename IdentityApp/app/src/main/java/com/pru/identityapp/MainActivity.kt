package com.pru.identityapp

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import androidx.appcompat.app.AppCompatActivity
import com.pru.identityapp.databinding.ActivityMainBinding
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import java.util.concurrent.atomic.AtomicReference


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val customTabIntent: AtomicReference<CustomTabsIntent> = AtomicReference()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCheck.setOnClickListener {
            val serviceConfiguration = AuthorizationServiceConfiguration(
                Uri.parse("https://pal4uat.bs-shipmanagement.com/IdentityServer/connect/authorize") /* auth endpoint */,
                Uri.parse("https://pal4uat.bs-shipmanagement.com/IdentityServer/connect/token") /* token endpoint */
            )
            val clientId = "MobileAppOffice"
            val redirectUri: Uri = Uri.parse("myapplication://oauth")
            val builder = AuthorizationRequest.Builder(
                serviceConfiguration,
                clientId,
                ResponseTypeValues.CODE,
                redirectUri
            )
            builder.setScopes("api1", "openid")
            val request = builder.build()
            val authorizationService = AuthorizationService(this)
            val intentBuilder = authorizationService.createCustomTabsIntentBuilder(request.toUri())
            customTabIntent.set(intentBuilder.build())
            val completionIntent = Intent(this, MainActivity::class.java)
            val cancelIntent = Intent(this, MainActivity::class.java)
            cancelIntent.putExtra("failed", true)
            cancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            authorizationService.performAuthorizationRequest(
                request, PendingIntent.getActivity(
                    this, 0,
                    completionIntent, 0
                ), PendingIntent.getActivity(this, 0, cancelIntent, 0),
                customTabIntent.get()

            )
        }
    }
}