package com.pru.nfcapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.pru.nfcapp.Utility.bytes2HexStr
import com.pru.nfcapp.Utility.decodeHex
import com.pru.nfcapp.Utility.hexStr2Bytes
import com.pru.nfcapp.databinding.ActivityMiFareCardBinding
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
import org.json.JSONObject
import sunmi.paylib.SunmiPayKernel
import java.util.Locale

class MiFareCardActivity : AppCompatActivity() {
    private lateinit var swingCardHintDialog: SwingCardHintDialog
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivityMiFareCardBinding
    private lateinit var payKernel: SunmiPayKernel
    private var connectPaySDK = false
    private var sector = 0
    private var keyType = 0
    private var keyBytes: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiFareCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        bindPaySDKService()
        listeners()
    }

    private fun listeners() {
        binding.mbWrite.setOnClickListener {
            val check = checkParams()
            if (check) {
                writeAllSector()
            }
        }

        binding.mbRead.setOnClickListener {
            val check = checkParams()
            if (check) {
                readAllSector()
            }
        }
    }

    private fun readAllSector() {
        val startBlockNo = sector * 4
        val result = m1Auth(keyType, startBlockNo, keyBytes!!)
        if (result) {
            var outData = ByteArray(128)
            var res: Int = m1ReadBlock(startBlockNo, outData)
            if (res in 0..16) {
                val hexStr: String = bytes2HexStr(outData.copyOf(res))
                binding.editBlock0.setText(hexStr.decodeHex())
            } else {
                binding.editBlock0.setText(R.string.fail)
            }
            outData = ByteArray(128)
            res = m1ReadBlock(startBlockNo + 1, outData)
            if (res in 0..16) {
                val hexStr: String = bytes2HexStr(outData.copyOf(res))
                binding.editBlock1.setText(hexStr.decodeHex())
            } else {
                binding.editBlock1.setText(R.string.fail)
            }
            outData = ByteArray(128)
            res = m1ReadBlock(startBlockNo + 2, outData)
            if (res in 0..16) {
                val hexStr: String = bytes2HexStr(outData.copyOf(res))
                binding.editBlock2.setText(hexStr.decodeHex())
            } else {
                binding.editBlock2.setText(R.string.fail)
            }
        }
    }

    private fun m1ReadBlock(block: Int, blockData: ByteArray): Int {
        try {
            return payKernel.mReadCardOptV2.mifareReadBlock(block, blockData)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return -123
    }

    private fun writeAllSector() {
        val startBlockNo = sector * 4
        val result: Boolean = m1Auth(keyType, startBlockNo, keyBytes!!)
        if (result) {
            var data: String = binding.editBlock0.text.toString()
            if (data.length == 32) {
                val inData: ByteArray = hexStr2Bytes(data)
                val res: Int = m1WriteBlock(startBlockNo, inData)
                if (res == 0) {
                    binding.editBlock0.setText("")
                } else {
                    binding.editBlock0.setText(R.string.fail)
                }
            }
            data = binding.editBlock1.text.toString()
            if (data.length == 32) {
                val inData: ByteArray = hexStr2Bytes(data)
                val res: Int = m1WriteBlock(startBlockNo + 1, inData)
                if (res == 0) {
                    binding.editBlock1.setText("")
                } else {
                    binding.editBlock1.setText(R.string.fail)
                }
            }
            data = binding.editBlock2.text.toString()
            if (data.length == 32) {
                val inData: ByteArray = hexStr2Bytes(data)
                val res: Int = m1WriteBlock(startBlockNo + 2, inData)
                if (res == 0) {
                    binding.editBlock2.setText("")
                } else {
                    binding.editBlock2.setText(R.string.fail)
                }
            }
        }
    }

    private fun m1WriteBlock(block: Int, blockData: ByteArray): Int {
        try {
            return payKernel.mReadCardOptV2.mifareWriteBlock(block, blockData)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return -123
    }

    private fun m1Auth(keyType: Int, block: Int, keyData: ByteArray): Boolean {
        var result = -1
        try {
            bytes2HexStr(keyData)
            result = payKernel.mReadCardOptV2.mifareAuth(keyType, block, keyData)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        if (result == 0) {
            return true
        }
        val msg = java.lang.String.format(
            Locale.getDefault(),
            "%s:%d(%s)",
            getString(R.string.card_auth_fail),
            result,
            AidlErrorCodeV2.valueOf(result).msg
        )
        showToast(msg)
        checkCard()
        return false
    }

    private fun checkParams(): Boolean {
        val sectorStr: String = binding.editSector1.text.toString()
        val keyAStr: String = binding.editKeyA1.text.toString()
        val keyBStr: String = binding.editKeyB1.text.toString()
        try {
            sector = sectorStr.toInt()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToast(getString(R.string.card_sector_hint))
            return false
        }
        if (keyAStr.length == 12) {
            keyType = 0
            keyBytes = hexStr2Bytes(keyAStr)
        }
        if (keyBStr.length == 12) {
            keyType = 1
            keyBytes = hexStr2Bytes(keyBStr)
        }
        if (keyBytes == null) {
            showToast(R.string.card_key_hint)
            return false
        }
        return true
    }

    private fun bindPaySDKService() {
        payKernel = SunmiPayKernel.getInstance()
        payKernel.initPaySDK(this, object : SunmiPayKernel.ConnectCallback {
            override fun onConnectPaySDK() {
                LogUtil.e("onConnectPaySDK...")
                connectPaySDK = true
                setBuzzerEnable()
                openSwingDialog()
                checkCard()
            }

            override fun onDisconnectPaySDK() {
                LogUtil.e("onDisconnectPaySDK...")
                connectPaySDK = false
            }
        })
    }

    private fun initView() {
        binding.toolbar.title = "MiFare Card"
        binding.imgBtnRefresh.setOnClickListener {
            clearData()
            payKernel.destroyPaySDK()
            bindPaySDKService()
        }
    }

    private fun setBuzzerEnable() {
        val basicOptV2 = payKernel.mBasicOptV2
        try {
            var jObject = JSONObject()
            val jsonStr: String? = basicOptV2?.getSysParam(AidlConstants.SysParam.RESERVED)
            if (!TextUtils.isEmpty(jsonStr)) {
                jObject = JSONObject(jsonStr!!)
            }
            jObject.put("buzzer", 1)
            basicOptV2?.setSysParam(
                AidlConstants.SysParam.RESERVED, jObject.toString()
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun checkCard() {
        try {
            val cardType: Int = AidlConstants.CardType.MIFARE.value
            payKernel.mReadCardOptV2?.checkCard(cardType, mCheckCardCallback, 60)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSwingDialog() {
        if (!this::swingCardHintDialog.isInitialized) {
            swingCardHintDialog = SwingCardHintDialog(this)
            swingCardHintDialog.setOwnerActivity(this)
        }
        if (swingCardHintDialog.isShowing || isDestroyed) return
        swingCardHintDialog.show()
    }

    private fun dismissSwingDialog() {
        runOnUiThread {
            if (this::swingCardHintDialog.isInitialized) {
                swingCardHintDialog.dismiss()
            }
        }
    }

    private val mCheckCardCallback: CheckCardCallbackV2 = object : CheckCardCallbackV2Wrapper() {
        @Throws(RemoteException::class)
        override fun findMagCard(info: Bundle) {
            LogUtil.e("findMagCard:" + Utility.bundle2String(info))
            dismissSwingDialog()
        }


        @Throws(RemoteException::class)
        override fun findICCardEx(info: Bundle) {
            LogUtil.e("findICCard:" + Utility.bundle2String(info))
            dismissSwingDialog()
        }


        @Throws(RemoteException::class)
        override fun findRFCardEx(info: Bundle) {
            LogUtil.e("findRFCard:" + Utility.bundle2String(info))
            dismissSwingDialog()
            setData()
        }


        @Throws(RemoteException::class)
        override fun onErrorEx(info: Bundle) {
            val code = info.getInt("code")
            val msg = info.getString("message")
            val error = "onError:$msg -- $code"
            LogUtil.e(error)
            showToast(error)
            dismissSwingDialog()
        }
    }

    private fun setData() {
        runOnUiThread {
            binding.editSector1.setText("1")
            val keyA = "FFFFFFFFFFFF"
            val keyB = ""
            binding.editKeyA1.setText(keyA)
            binding.editKeyB1.setText(keyB)
            binding.mbRead.performClick()
        }
    }

    private fun clearData() {
        binding.editSector1.setText("")
        binding.editKeyA1.setText("")
        binding.editKeyB1.setText("")
        binding.editBlock0.setText("")
        binding.editBlock1.setText("")
        binding.editBlock2.setText("")
        binding.mbRead.performClick()
    }

    private fun showToast(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(@StringRes error: Int) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        cancelCheckCard()
        super.onDestroy()
    }

    private fun cancelCheckCard() {
        try {
            payKernel.mReadCardOptV2?.cardOff(AidlConstants.CardType.NFC.value)
            payKernel.mReadCardOptV2?.cancelCheckCard()
            payKernel.destroyPaySDK()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}