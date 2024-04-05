package com.pru.nfcapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pru.nfcapp.databinding.ActivityCardNfcBinding
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
import org.json.JSONObject
import sunmi.paylib.SunmiPayKernel

class NFCActivity : AppCompatActivity() {
    private var totalCount = 0
    private var successCount = 0
    private var failCount = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivityCardNfcBinding
    private lateinit var payKernel: SunmiPayKernel
    private var connectPaySDK = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        bindPaySDKService()
    }

    private fun bindPaySDKService() {
        binding.pbView.isVisible = true

        payKernel = SunmiPayKernel.getInstance()
        payKernel.initPaySDK(this, object : SunmiPayKernel.ConnectCallback {
            override fun onConnectPaySDK() {
                LogUtil.e("onConnectPaySDK...")
                connectPaySDK = true
                binding.pbView.isVisible = false
                setBuzzerEnable()
                checkCard()
            }

            override fun onDisconnectPaySDK() {
                LogUtil.e("onDisconnectPaySDK...")
                binding.pbView.isVisible = false
                connectPaySDK = false
            }
        })
    }

    private fun initView() {
        binding.toolbar.title = "Check Card"
        binding.imgBtnRefresh.setOnClickListener {
            payKernel.destroyPaySDK()
            bindPaySDKService()
        }
    }

    private fun setBuzzerEnable() {
        val basicOptV2 = payKernel.mBasicOptV2
        try {
            var jobj = JSONObject()
            val jsonStr: String? = basicOptV2?.getSysParam(AidlConstants.SysParam.RESERVED)
            if (!TextUtils.isEmpty(jsonStr)) {
                jobj = JSONObject(jsonStr!!)
            }
            jobj.put("buzzer", 1)
            basicOptV2?.setSysParam(
                AidlConstants.SysParam.RESERVED, jobj.toString()
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun checkCard() {
        try {
            val cardType: Int = AidlConstants.CardType.NFC.value
            payKernel.mReadCardOptV2?.checkCard(cardType, mCheckCardCallback, 60)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mCheckCardCallback: CheckCardCallbackV2 = object : CheckCardCallbackV2Wrapper() {
        @Throws(RemoteException::class)
        override fun findMagCard(info: Bundle) {
            LogUtil.e("findMagCard:" + Utility.bundle2String(info))
        }


        @Throws(RemoteException::class)
        override fun findICCardEx(info: Bundle) {
            LogUtil.e("findICCard:" + Utility.bundle2String(info))
        }


        @Throws(RemoteException::class)
        override fun findRFCardEx(info: Bundle) {
            LogUtil.e("findRFCard:" + Utility.bundle2String(info))
            handleResult(true, info)
        }


        @Throws(RemoteException::class)
        override fun onErrorEx(info: Bundle) {
            val code = info.getInt("code")
            val msg = info.getString("message")
            val error = "onError:$msg -- $code"
            LogUtil.e(error)
            showToast(error)
            handleResult(false, info)
        }
    }

    private fun showToast(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }


    private fun handleResult(success: Boolean, info: Bundle) {
        if (isFinishing) {
            return
        }
        handler.post {
            totalCount++
            if (success) {
                successCount++
                binding.tvDepictor.text = "Find RF card"
                binding.tvData.text = Utility.bundle2String(info, 1)
            } else { //on Error
                failCount++
                binding.tvDepictor.text = "Check card error"
            }
            binding.mbTotal.text = "Total: ".plus(totalCount)
            binding.mbSuccess.text = "Success: ".plus(successCount)
            binding.mbFail.text = "Fail: ".plus(failCount)
            binding.mbFail.text = Utility.formatStr("%s %d", "Fail: ", failCount)
            if (!isFinishing) {
                handler.postDelayed({ checkCard() }, 500)
            }
        }
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