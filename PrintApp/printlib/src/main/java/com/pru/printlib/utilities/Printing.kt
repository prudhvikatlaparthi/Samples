package com.pru.printlib.utilities

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.pru.printlib.data.BluetoothCallback
import com.pru.printlib.data.DeviceCallback
import com.pru.printlib.data.PairedPrinter
import com.pru.printlib.data.printable.Printable
import com.pru.printlib.data.printer.Printer

class Printing(private var printer: Printer, private var pairedPrinter: PairedPrinter, val context: Context) {
    private lateinit var printables: List<Printable>
    private var bluetooth = Bluetooth(context)
    var printingCallback: PrintingCallback? = null
    var extraLinesAtEnd: Byte = 10

    init {
        initBluetoothCallback()
        initDeviceCallback()
    }

    private fun initBluetoothCallback() {
        bluetooth.setBluetoothCallback(object : BluetoothCallback {
            override fun onBluetoothTurningOn() {}

            override fun onBluetoothOn() {
                bluetooth.connectToAddress(pairedPrinter.address)
            }

            override fun onBluetoothTurningOff() {}

            override fun onBluetoothOff() {}

            override fun onUserDeniedActivation() {}
        })
    }

    private fun initDeviceCallback() {
        bluetooth.setDeviceCallback(object : DeviceCallback {
            override fun onDeviceConnected(device: BluetoothDevice) {
                printPrintables()
                printingCallback?.printingOrderSentSuccessfully()
            }

            override fun onDeviceDisconnected(device: BluetoothDevice, message: String) {
                printingCallback?.disconnected()
            }

            override fun onMessage(message: String) {
                printingCallback?.onMessage(message)
            }

            override fun onError(message: String) {
                printingCallback?.onError(message)
            }

            override fun onConnectError(device: BluetoothDevice, message: String) {
                printingCallback?.connectionFailed(message)
            }
        })
    }

    private fun printPrintables() {
        bluetooth.send(printer.initPrinterCommand) // init printer
        this.printables.forEach {
            it.getPrintableByteArray(printer).forEach { ops ->
                bluetooth.send(ops)
            }
        }

        //Feed 2 lines to cut the paper
        if (extraLinesAtEnd > 0) {
            bluetooth.send(printer.feedLineCommand.plus(extraLinesAtEnd))
        }

        Handler(Looper.getMainLooper()).postDelayed({
            bluetooth.disconnect()
        }, 2500)
    }

    fun print(printables: ArrayList<Printable>) {
        this.printables = printables
        printingCallback?.connectingWithPrinter()
        bluetooth.onStart()
        if (!bluetooth.isEnabled) bluetooth.enable() else bluetooth.connectToAddress(pairedPrinter.address)
    }
}