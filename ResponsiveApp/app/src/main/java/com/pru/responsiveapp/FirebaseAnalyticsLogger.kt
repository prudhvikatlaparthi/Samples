package com.pru.responsiveapp

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun sendCurrentScreenEvent(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }
    }

    fun sendClickEvent(eventName: String) {
        firebaseAnalytics.logEvent("ButtonClickEvent") {
            param("ButtonClickEventName", eventName)
        }
    }
}