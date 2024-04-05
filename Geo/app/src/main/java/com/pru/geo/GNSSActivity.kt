package com.pru.geo
import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*


class GNSSActivity : Activity() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private lateinit var inputStream: InputStream
    private lateinit var textStatus: TextView
    private lateinit var textMsg: TextView
    private lateinit var btnPair: Button
    private lateinit var btnConnect: Button
    private lateinit var btnGetLocation: Button
    private lateinit var btnDisConnect: Button
    private lateinit var mBluetoothDevice: BluetoothDevice
    lateinit var mDevices: Set<BluetoothDevice>
    final var listenToStream: Boolean = true
    var gnssData: StringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gnss)
        textStatus = findViewById(R.id.textStatus) as TextView
        textMsg = findViewById(R.id.textMsg) as TextView

        btnPair = findViewById(R.id.btnPair) as Button
        btnPair.setOnClickListener {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            mDevices = bluetoothAdapter!!.bondedDevices
            for (device: BluetoothDevice in mDevices)
            {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@setOnClickListener
                }
                if (device.name.equals("GS15_1512644"))//  val deviceAddress = "00:13:43:05:97:77"
                {
                    mBluetoothDevice = device
                    textStatus.setText("Status : Paired Device " + mBluetoothDevice.name + " -- " + mBluetoothDevice.address)
                    break
                }
            }
        }
        btnConnect = findViewById(R.id.btnConnect) as Button
        btnConnect.setOnClickListener {
            if (bluetoothAdapter != null && bluetoothAdapter!!.isEnabled) {
                val gs15Device = bluetoothAdapter!!.getRemoteDevice(mBluetoothDevice.address)
                try {
                    bluetoothSocket = gs15Device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    bluetoothSocket?.connect()
                    textStatus.setText("Status : Connect to " + mBluetoothDevice.name + " -- " + mBluetoothDevice.address)

                } catch (e: IOException) {
                    System.out.println("IOException connect - "+e.toString())
                }
            }
        }
        btnGetLocation = findViewById(R.id.btnGetLocation) as Button
        btnGetLocation.setOnClickListener {
            startFileReading()
//            startReadingData()
//            getLocation(listenToStream,inputStream,bluetoothSocket,textMsg,this@GNSSActivity).execute()
        }
        btnDisConnect = findViewById(R.id.btnDisConnect) as Button
        btnDisConnect.setOnClickListener {
            try {
                if (bluetoothSocket!= null) {
                    inputStream.close()
                    bluetoothSocket?.close()
                }
                textStatus.setText("Status : Diconnected")
            } catch (e: IOException) {
            }
        }
    }

    private fun startFileReading() {
        try {
            val myInputStream = assets.open("2023-shapefile.txt")
            val file = File(Environment.getExternalStorageDirectory(), "Download/temp${System.currentTimeMillis()}.txt")

            myInputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            // Exception
            e.printStackTrace()
        }
    }

    private fun startReadingDataNWriteToFile() {
        println("startReadingDataNWriteToFile")
        Thread {
            try {
                println("ThreadEntered")
                inputStream = bluetoothSocket?.inputStream!!
                println("inputStream ${inputStream.available()}")
                val file = File(Environment.getExternalStorageDirectory(), "Download/temp${System.currentTimeMillis()}.txt")
                println("file path created stream about to start")
                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    class getLocation(
        listenToStream: Boolean,
        inputStream: InputStream,
        bluetoothSocket: BluetoothSocket?,
        textMsg: TextView,
        ctx: GNSSActivity
    ) : AsyncTask<Void, Void, String>() {
        var mlistenToStream = listenToStream
        var mInputStream = inputStream
        var mBluetoothSocket = bluetoothSocket
        var mtextMsg = textMsg
        var context = ctx
        val progressDialog = ProgressDialog(ctx)

        override fun onPreExecute() {
            super.onPreExecute()
            mlistenToStream = true
            progressDialog.setTitle("Getting location...")
            progressDialog.setMessage("Loading please wait...")
            progressDialog.show()
        }
        override fun doInBackground(vararg params: Void?): String {
            mInputStream = mBluetoothSocket?.inputStream!!
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (mlistenToStream) {
                try {
                    bytesRead = mInputStream?.read(buffer) ?: 0
                    if (bytesRead > 0) {
                        val receivedData = String(buffer, 0, bytesRead)
                        return receivedData
                    }
                } catch (e: IOException) {
                    System.out.println("IOException read - "+e.toString())
                    break
                }
            }
            return "------"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val gnssData: StringBuilder = StringBuilder()

            if (result?.startsWith("$") == true) {
                val strValues: List<String> = result.split("$")
                for (i in strValues) {
                    val GNGSA = ""
                    if (i.startsWith("$GNGSA")) {
                        val strValues: List<String> = result.split(",")
                        mtextMsg.text = gnssData.append(
                            strValues.get(0) + "---------------------" + strValues.get(strValues.size - 1)
                        )
                        mlistenToStream = false
                    }
                    val GNGGA = ""

                    if (i.startsWith("$GNGGA")) {
                        val strValues: List<String> = result.split(",")
                        mtextMsg.text = gnssData.append(
                            strValues.get(0) + "---------------------" + strValues.get(strValues.size - 1)
                        )
                        mlistenToStream = false
                    }
                }
            }
            progressDialog.dismiss()

        }


    }
}
