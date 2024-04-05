package com.pru.printlib.utilities

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.pru.printlib.data.BluetoothCallback
import com.pru.printlib.data.DeviceCallback
import com.pru.printlib.data.DiscoveryCallback
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.UUID

class Bluetooth {
    private var context: Context? = null
    private var uuid: UUID? = null
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var socket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null
    private var devicePair: BluetoothDevice? = null
    private var input: BufferedReader? = null
    private var out: OutputStream? = null
    private var deviceCallback: DeviceCallback? = null
    private var discoveryCallback: DiscoveryCallback? = null
    private var bluetoothCallback: BluetoothCallback? = null
    var isConnected = false
        private set

    constructor(context: Context) {
        initialize(context, UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"))
    }

    constructor(context: Context, uuid: UUID) {
        initialize(context, uuid)
    }

    private fun initialize(context: Context, uuid: UUID) {
        this.context = context
        this.uuid = uuid
        deviceCallback = null
        discoveryCallback = null
        bluetoothCallback = null
        isConnected = false
    }

    private val pairReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val state =
                    intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                val prevState = intent.getIntExtra(
                    BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                    BluetoothDevice.ERROR
                )
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    context.unregisterReceiver(this)
                    if (discoveryCallback != null) {
                        discoveryCallback!!.onDevicePaired(devicePair!!)
                    }
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    context.unregisterReceiver(this)
                    if (discoveryCallback != null) {
                        discoveryCallback!!.onDeviceUnpaired(devicePair!!)
                    }
                }
            }
        }
    }

    fun onStop() {
        context!!.unregisterReceiver(bluetoothReceiver)
    }

    fun enable() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter!!.isEnabled) {
                bluetoothAdapter!!.enable()
            }
        }
    }

    private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                if (bluetoothCallback != null) {
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> bluetoothCallback!!.onBluetoothOff()
                        BluetoothAdapter.STATE_TURNING_OFF -> bluetoothCallback!!.onBluetoothTurningOff()
                        BluetoothAdapter.STATE_ON -> bluetoothCallback!!.onBluetoothOn()
                        BluetoothAdapter.STATE_TURNING_ON -> bluetoothCallback!!.onBluetoothTurningOn()
                    }
                }
            }
        }
    }

    @JvmOverloads
    fun connectToAddress(address: String?, insecureConnection: Boolean = false) {
        val device = bluetoothAdapter!!.getRemoteDevice(address)
        connectToDevice(device, insecureConnection)
    }

    fun connectToDevice(device: BluetoothDevice, insecureConnection: Boolean) {
        ConnectThread(device, insecureConnection).start()
    }

    private val scanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                when (action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state =
                            intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        if (state == BluetoothAdapter.STATE_OFF) if (discoveryCallback != null) discoveryCallback!!.onError(
                            "Bluetooth turned off"
                        )
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> if (discoveryCallback != null) discoveryCallback!!.onDiscoveryStarted()
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        context.unregisterReceiver(this)
                        if (discoveryCallback != null) discoveryCallback!!.onDiscoveryFinished()
                    }

                    BluetoothDevice.ACTION_FOUND -> {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        if (discoveryCallback != null) discoveryCallback!!.onDeviceFound(device!!)
                    }
                }
            }
        }
    }

    fun send(msg: ByteArray?) {
        sendMessage(msg)
    }

    fun sendImage(byteArray: ByteArray?) {
        try {
            out!!.write(byteArray)
            out!!.write(byteArrayOf(0x0b, 0x0c))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    val pairedDevices: List<BluetoothDevice>
        get() = if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ArrayList()
        } else ArrayList(bluetoothAdapter!!.bondedDevices)

    fun startScanning() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        context!!.registerReceiver(scanReceiver, filter)
        bluetoothAdapter!!.startDiscovery()
    }

    fun onStart() {
        bluetoothManager = context!!.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (bluetoothManager != null) bluetoothAdapter = bluetoothManager!!.adapter
        context!!.registerReceiver(
            bluetoothReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    val isEnabled: Boolean
        get() = bluetoothAdapter != null && bluetoothAdapter!!.isEnabled

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (bluetoothCallback != null) {
            if (requestCode == REQUEST_ENABLE_BT) {
                if (resultCode == Activity.RESULT_CANCELED) bluetoothCallback!!.onUserDeniedActivation()
            }
        }
    }

    fun disconnect() {
        try {
            socket!!.close()
        } catch (e: IOException) {
            if (deviceCallback != null) deviceCallback!!.onError(e.message!!)
        }
    }

    fun sendMessage(msg: ByteArray?) {
        try {
            out!!.write(msg)
        } catch (e: IOException) {
            isConnected = false
            if (deviceCallback != null) deviceCallback!!.onDeviceDisconnected(device!!, e.message!!)
        }
    }

    fun pair(device: BluetoothDevice) {
        context!!.registerReceiver(
            pairReceiver,
            IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )
        devicePair = device
        try {
            val method = device.javaClass.getMethod("createBond")
            method.invoke(device)
        } catch (e: Exception) {
            if (discoveryCallback != null) discoveryCallback!!.onError(e.message!!)
        }
    }

    fun unPair(device: BluetoothDevice) {
        context!!.registerReceiver(
            pairReceiver,
            IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )
        devicePair = device
        try {
            val method = device.javaClass.getMethod("removeBond")
            method.invoke(device)
        } catch (e: Exception) {
            if (discoveryCallback != null) {
                discoveryCallback!!.onError(e.message!!)
            }
        }
    }

    fun setDeviceCallback(deviceCallback: DeviceCallback?) {
        this.deviceCallback = deviceCallback
    }

    fun setDiscoveryCallback(discoveryCallback: DiscoveryCallback?) {
        this.discoveryCallback = discoveryCallback
    }

    private inner class ReceiveThread : Thread(), Runnable {
        override fun run() {
            var msg: String
            try {
                while (input!!.readLine().also { msg = it } != null) {
                    if (deviceCallback != null) {
                        val msgCopy = msg
                        Handler(Looper.getMainLooper()).post { deviceCallback!!.onMessage(msgCopy) }
                    }
                }
            } catch (e: IOException) {
                isConnected = false
                if (deviceCallback != null) Handler(Looper.getMainLooper()).post {
                    deviceCallback!!.onDeviceDisconnected(
                        device!!, e.message!!
                    )
                }
            }
        }
    }

    fun setBluetoothCallback(bluetoothCallback: BluetoothCallback?) {
        this.bluetoothCallback = bluetoothCallback
    }

    private inner class ConnectThread internal constructor(
        device: BluetoothDevice,
        insecureConnection: Boolean
    ) : Thread() {
        init {
            this@Bluetooth.device = device
            try {
                if (insecureConnection) {
                    if (ActivityCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        socket = device.createInsecureRfcommSocketToServiceRecord(uuid)
                    }

                } else {
                    socket = device.createRfcommSocketToServiceRecord(uuid)
                }
            } catch (e: IOException) {
                if (deviceCallback != null) {
                    deviceCallback!!.onError(e.message!!)
                }
            }
        }

        override fun run() {
            bluetoothAdapter!!.cancelDiscovery()
            try {
                socket!!.connect()
                out = socket!!.outputStream
                input = BufferedReader(InputStreamReader(socket!!.inputStream))
                isConnected = true
                ReceiveThread().start()
                if (deviceCallback != null) Handler(Looper.getMainLooper()).post {
                    deviceCallback!!.onDeviceConnected(
                        device!!
                    )
                }
            } catch (e: IOException) {
                if (deviceCallback != null) Handler(Looper.getMainLooper()).post {
                    deviceCallback!!.onConnectError(
                        device!!, e.message!!
                    )
                }
                try {
                    socket!!.close()
                } catch (closeException: IOException) {
                    if (deviceCallback != null) Handler(Looper.getMainLooper()).post {
                        deviceCallback!!.onError(
                            closeException.message!!
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1111
    }
}