package com.pru.printapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.pru.printlib.utilities.PrintingCallback
import com.pru.printapp.databinding.ActivityMainBinding
import com.pru.printlib.PrintLib
import com.pru.printlib.ScanActivity
import com.pru.printlib.data.printable.ImagePrintable
import com.pru.printlib.data.printable.Printable
import com.pru.printlib.data.printer.DefaultPrinter
import com.pru.printlib.utilities.Constants
import com.pru.printlib.utilities.Printing

class MainActivity : AppCompatActivity() {
    private var printing: Printing? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ))
        }
        initViews()
        initListeners()
    }

    private fun initViews() {
        if (PrintLib.hasPairedPrinter()) printing = PrintLib.printer()
        binding.btnPiarUnpair.text =
            if (PrintLib.hasPairedPrinter()) "Un-pair ${PrintLib.getPairedPrinter()?.name}" else "Pair with printer"
    }

    private fun initListeners() {
        binding.btnPrintImages.setOnClickListener {
            if (!PrintLib.hasPairedPrinter()) scanLauncher.launch(
                Intent(
                    this, ScanActivity::class.java
                )
            )
            else printSomeImages()
        }

        binding.btnPiarUnpair.setOnClickListener {
            if (PrintLib.hasPairedPrinter()) PrintLib.removeCurrentPrinter()
            else scanLauncher.launch(
                Intent(this, ScanActivity::class.java)
            )
            initViews()
        }

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@MainActivity, "Connecting with printer", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@MainActivity, "Order sent to printer", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@MainActivity, "Failed to connect printer", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@MainActivity, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@MainActivity, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun printSomeImages() {
        val btm1: Bitmap = binding.llBody.drawToBitmap()
        binding.imgView.setImageBitmap(btm1)
        val width = binding.llBody.layoutParams.width
        val layoutParams = LinearLayout.LayoutParams(Constants.INCH2DP.TWO_INCH.dp, LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        binding.llBody.layoutParams = layoutParams
        binding.llBody.post {
            val btm: Bitmap = binding.llBody.drawToBitmap()
            binding.imgView.setImageBitmap(btm)
            binding.llBody.layoutParams =
                LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT)
            val printable = arrayListOf<Printable>(
                ImagePrintable.Builder(image = btm).setNewLinesAfter(pxToDp(20)).setAlignment(
                    DefaultPrinter.ALIGNMENT_CENTER
                ).build()
            )
            printing?.print(printable)
        }

    }

    fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun Context.pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
        }else{
            //deny
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")
            }
        }

    private val scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            initViews()
        }
    }
}