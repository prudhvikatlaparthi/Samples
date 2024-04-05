package com.pru.oldsignalr

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import microsoft.aspnet.signalr.client.Credentials
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import microsoft.aspnet.signalr.client.transport.ClientTransport
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport
import java.util.concurrent.ExecutionException

class MainActivity : AppCompatActivity() {
    private lateinit var mHubConnection: HubConnection
    private lateinit var mHubProxy: HubProxy
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startSignalR()
    }

    private fun startSignalR() {
        Platform.loadPlatformComponent(AndroidPlatformComponent())

        val credentials =
            Credentials { request ->
                request.addHeader("Content-Type", "application/json")
            }
        mHubConnection = HubConnection(
            "https://machatapi.justbilling.co/",
            null, false
        ) { p0, p1 -> Log.i("Prudhvi Log", "log: $p0 $p1") }

        mHubConnection.credentials = credentials
        mHubProxy = mHubConnection.createHubProxy("ChatHub")
        val clientTransport: ClientTransport = ServerSentEventsTransport(mHubConnection.logger)
        val signalRFuture = mHubConnection.start(clientTransport)
        try {
            signalRFuture?.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        mHubProxy.on<Unit>("TrackConnection") {
            Log.i("Prudhvi Log", "startSignalR: TrackConnection message")
            lifecycleScope.launch {
                delay(2000)
                mHubProxy.invoke("CheckConnection")
            }
        }
        mHubProxy.invoke("CheckConnection")
    }
}