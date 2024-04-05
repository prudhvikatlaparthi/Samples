package com.pru.printlib

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.pru.printlib.data.DiscoveryCallback
import com.pru.printlib.databinding.ActivityScanBinding
import com.pru.printlib.utilities.Bluetooth

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var bluetooth: Bluetooth
    private var devices = ArrayList<BluetoothDevice>()
    private lateinit var adapter: BluetoothDevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = BluetoothDevicesAdapter(this)
        bluetooth = Bluetooth(this)
        setup()
    }

    private fun setup() {
        initViews()
        initListeners()
        initDeviceCallback()
    }

    private fun initDeviceCallback() {
        bluetooth.setDiscoveryCallback(object : DiscoveryCallback {
            override fun onDiscoveryStarted() {
                binding.refreshLayout.isRefreshing = true
                binding.toolbar.title = "Scanning.."
                devices.clear()
                devices.addAll(bluetooth.pairedDevices)
                adapter.notifyDataSetChanged()
            }

            override fun onDiscoveryFinished() {
                binding.toolbar.title =
                    if (devices.isNotEmpty()) "Select a Printer" else "No devices"
                binding.refreshLayout.isRefreshing = false
            }

            override fun onDeviceFound(device: BluetoothDevice) {
                if (!devices.contains(device)) {
                    devices.add(device)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onDevicePaired(device: BluetoothDevice) {
                PrintLib.setPrinter(device.name, device.address)
                Toast.makeText(this@ScanActivity, "Device Paired", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
                setResult(Activity.RESULT_OK)
                this@ScanActivity.finish()
            }

            override fun onDeviceUnpaired(device: BluetoothDevice) {
                Toast.makeText(this@ScanActivity, "Device unpaired", Toast.LENGTH_SHORT).show()
                val pairedPrinter = PrintLib.getPairedPrinter()
                if (pairedPrinter != null && pairedPrinter.address == device.address)
                    PrintLib.removeCurrentPrinter()
                devices.remove(device)
                adapter.notifyDataSetChanged()
                bluetooth.startScanning()
            }

            override fun onError(message: String) {
                Toast.makeText(this@ScanActivity, "Error while pairing", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initListeners() {
        binding.refreshLayout.setOnRefreshListener { bluetooth.startScanning() }
        binding.printers.setOnItemClickListener { _, _, i, _ ->
            val device = devices[i]
            if (device.bondState == BluetoothDevice.BOND_BONDED) {
                PrintLib.setPrinter(device.name, device.address)
                setResult(Activity.RESULT_OK)
                this@ScanActivity.finish()
            } else if (device.bondState == BluetoothDevice.BOND_NONE)
                bluetooth.pair(devices[i])
            adapter.notifyDataSetChanged()
        }
    }

    private fun initViews() {
        binding.printers.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        bluetooth.onStart()
        if (!bluetooth.isEnabled)
            bluetooth.enable()
        Handler(Looper.getMainLooper()).postDelayed({
            bluetooth.startScanning()
        }, 1000)

    }

    override fun onStop() {
        super.onStop()
        bluetooth.onStop()
    }

    inner class BluetoothDevicesAdapter(context: Context) :
        ArrayAdapter<BluetoothDevice>(context, android.R.layout.simple_list_item_1) {
        override fun getCount(): Int {
            return devices.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return LayoutInflater.from(context)
                .inflate(R.layout.bluetooth_device_row, parent, false).apply {
                    findViewById<TextView>(R.id.name).text =
                        if (devices[position].name.isNullOrEmpty()) devices[position].address else devices[position].name
                    findViewById<TextView>(R.id.pairStatus).visibility =
                        if (devices[position].bondState != BluetoothDevice.BOND_NONE) View.VISIBLE else View.INVISIBLE
                    findViewById<TextView>(R.id.pairStatus).text =
                        when (devices[position].bondState) {
                            BluetoothDevice.BOND_BONDED -> "Paired"
                            BluetoothDevice.BOND_BONDING -> "Pairing.."
                            else -> ""
                        }
                    findViewById<ImageView>(R.id.pairedPrinter).visibility =
                        if (PrintLib.getPairedPrinter()?.address == devices[position].address) View.VISIBLE else View.GONE
                }
        }
    }
}