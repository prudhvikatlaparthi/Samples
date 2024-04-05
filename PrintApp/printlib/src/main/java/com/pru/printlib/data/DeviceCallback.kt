package com.pru.printlib.data

import android.bluetooth.BluetoothDevice

interface DeviceCallback {
    fun onDeviceConnected(device: BluetoothDevice)
    fun onDeviceDisconnected(device: BluetoothDevice, message: String)
    fun onMessage(message: String)
    fun onError(message: String)
    fun onConnectError(device: BluetoothDevice, message: String)
}