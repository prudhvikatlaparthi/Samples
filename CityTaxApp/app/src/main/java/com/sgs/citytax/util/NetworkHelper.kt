package com.sgs.citytax.util

import android.content.Context
import android.net.ConnectivityManager
import com.sgs.citytax.base.MyApplication
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


fun isOnline(): Boolean {
    val cm = MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (cm == null || cm.activeNetworkInfo == null)
        return false
    return cm?.activeNetworkInfo.isConnected
}

fun getIPAddress(): String? {
    try {
        val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addresses: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (address in addresses) {
                if (!address.isLoopbackAddress) {
                    val sAddr: String = address.hostAddress
                    //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (isIPv4) return sAddr
                    /*if (useIPv4) {
                        if (isIPv4) return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                        }
                    }*/
                }
            }
        }
    } catch (ignored: Exception) {
        LogHelper.writeLog(exception = ignored)
    }
    return ""
}